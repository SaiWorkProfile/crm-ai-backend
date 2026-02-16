package com.realestate.ai.controller;

import com.realestate.ai.dto.*;
import com.realestate.ai.model.Lead;
import com.realestate.ai.model.Project;
import com.realestate.ai.service.*;
import com.realestate.ai.util.BudgetParser;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/chat")
@CrossOrigin
public class AiChatController {

@Autowired
private AiChatService aiChatService;

@Autowired
private TranslationService translator;

@Autowired
private LeadService leadService;

@Autowired
private VoiceSessionService session;

@Autowired
private RealEstateNlpService aiExtract;

@Autowired
private ProjectAIService projectService;


// ================= AI CHAT =================
@PostMapping
public AiChatResponse chat(
@RequestBody AiChatRequest request
){

String sessionId = request.getSessionId();

String msg =
request.getMessages()
.get(request.getMessages().size()-1)
.getContent();

String lang =
request.getLanguage().name();


// ============================================
// STEP 1 : ASK NAME
// ============================================
if(session.has(sessionId,"askName")){

session.set(sessionId,"name",msg);
session.remove(sessionId,"askName");
session.set(sessionId,"askPhone","yes");

return new AiChatResponse(
translator.translate(
"Please share your 10 digit phone number.",
lang
));
}


// ============================================
// STEP 2 : ASK PHONE
// ============================================
if(session.has(sessionId,"askPhone")){

String phone =
msg.replaceAll("\\D","");

if(phone.length()!=10
|| !phone.matches("^[6-9]\\d{9}$")){

return new AiChatResponse(
translator.translate(
"Please enter a valid 10 digit Indian mobile number starting with 6-9.",
lang
));
}

String name =
session.getString(sessionId,"name");

Lead lead =
leadService.createLeadFromAi(
name,
phone,
null,
null,
null
);

leadService.saveConversation(
lead,
msg,
"Lead created from Project Match",
"AI_CHAT"
);

session.clear(sessionId);

return new AiChatResponse(
translator.translate(
"Thank you "+name+
". Our property expert will contact you shortly.",
lang
));
}


//============================================
//STEP 3 : NLP EXTRACTION
//============================================
Map<String,Object> r =
aiExtract.extract(msg);


//============================================
//MERGE REQUIREMENTS (PROGRESSIVE)
//============================================

Map<String,Object> oldReq =
session.getMap(sessionId,"requirement");

if(oldReq==null)
oldReq=new HashMap<>();

for(String key : r.keySet()){

Object val = r.get(key);

if(val==null) continue;

//🔥 BUDGET PARSE
if(key.equalsIgnoreCase("budget")){

Long numeric =
BudgetParser.parseBudgetToNumber(
val.toString()
);

if(numeric!=null)
oldReq.put("budget",numeric);

continue;
}

//🔥 BHK PARSE
if(key.equalsIgnoreCase("bhk")){

try{
Integer bhk =
Integer.parseInt(
val.toString()
.replaceAll("\\D","")
);
oldReq.put("bhk",bhk);
}catch(Exception e){}

continue;
}

//🔥 NORMAL MERGE
if(!val.toString().isBlank())
oldReq.put(key,val);
}


// 🔥 LOCATION → CITY FALLBACK
if(oldReq.containsKey("location") &&
!oldReq.containsKey("city")){

oldReq.put(
"city",
oldReq.get("location")
);
}

//SAVE MERGED BACK
session.set(sessionId,
"requirement",
oldReq);


//============================================
//STEP 4 : CHECK MERGED DATA
//============================================

if(!hasRealRequirement(oldReq)){

String reply =
aiChatService.chat(
request.getMessages(),
request.getLanguage()
);

reply =
translator.translate(reply,lang);

return new AiChatResponse(reply);
}


//============================================
//STEP 5 : PROJECT SEARCH
//============================================
List<Project> list =
projectService
.progressiveSearch(oldReq);


//============================================
//STEP 6 : PROJECT FOUND
//============================================
if(!list.isEmpty()){

StringBuilder props =
new StringBuilder(
"We found these matching properties:\n\n"
);

int count = 1;

for(Project p : list){

props.append(count++)
.append(". ")
.append(p.getName())
.append(" – ")
.append(p.getLocation())
.append(" – ₹")
.append(p.getPriceStart())
.append("\n");
}

// SAVE PROJECT LIST IN SESSION (OPTIONAL)
session.set(sessionId,"matchedProjects",list);

// ASK NAME AFTER SHOWING
session.set(sessionId,"askName","yes");

return new AiChatResponse(
translator.translate(
props.toString()+
"\nMay I know your name?",
lang
));
}



//============================================
//STEP 7 : PROJECT NOT FOUND
//============================================
session.set(sessionId,"askName","yes");

return new AiChatResponse(
translator.translate(
"Sorry, I couldn't find an exact match. May I know your name so our expert can assist you?",
lang
));
}


// ================= REQUIREMENT GUARD =================
private boolean hasRealRequirement(
Map<String,Object> r){

return
r.get("bhk")!=null ||
r.get("city")!=null ||
r.get("location")!=null ||
r.get("propertyType")!=null ||
r.get("budget")!=null ||
Boolean.TRUE.equals(r.get("gatedCommunity"));
}

}
