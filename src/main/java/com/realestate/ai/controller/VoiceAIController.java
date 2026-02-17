package com.realestate.ai.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.realestate.ai.model.Lead;
import com.realestate.ai.model.Project;
import com.realestate.ai.service.*;
import com.realestate.ai.util.BudgetParser;
import com.realestate.ai.util.SpokenNumberParser;

@RestController
@RequestMapping("/api/voice")
@CrossOrigin
public class VoiceAIController {

@Autowired
private RealEstateNlpService aiExtract;

@Autowired
private RequirementExtractionService ruleExtract;

@Autowired
private ProjectAIService projectService;

@Autowired
private VoiceSessionService session;

@Autowired
private LeadService leadService;

@Autowired
private RealEstateFilterService filter;

@Autowired
private TranslationService translator;


// ================== MAIN VOICE ==================
@PostMapping("/ask")
public Map<String,String> ask(
@RequestBody Map<String,String> req){

String sessionId = req.get("sessionId");
String msg = req.get("message");

// 🔥 LANGUAGE FROM FRONTEND
String lang = req.getOrDefault("language","en");

if(msg == null || msg.isBlank()){
    return Map.of(
        "reply", translator.translate(
            "Sorry, I didn't catch that. Please say again.", lang),
        "stage","SEARCH",
        "close","no"
    );
}

msg = msg.toLowerCase();

if(sessionId == null || sessionId.isBlank()){
    return Map.of(
        "reply", translator.translate(
            "Session expired. Please restart consultation.", lang),
        "stage","DONE",
        "close","yes"
    );
}

System.out.println("🎤 USER: "+msg);


// ============================================
// STEP 1 : ASK NAME
// ============================================

if(session.has(sessionId,"askName")){

session.set(sessionId,"name",msg);
session.remove(sessionId,"askName");
session.set(sessionId,"askPhone","yes");

String reply =
translator.translate(
"Please share your 10 digit phone number.",
lang
);

return Map.of(
"reply",reply,
"stage","PHONE",
"close","no"
);
}


// ============================================
// STEP 2 : ASK PHONE
// ============================================

if(session.has(sessionId,"askPhone")){

msg = SpokenNumberParser.parse(msg);
String phone = msg.replaceAll("\\D","");

if(phone.length()!=10 ||
!phone.matches("^[6-9]\\d{9}$")){

String reply =
translator.translate(
"Please say valid 10 digit phone number.",
lang
);

return Map.of(
"reply",reply,
"stage","PHONE",
"close","no"
);
}

String name =
session.getString(sessionId,"name");

Map<String,Object> reqMap =
session.getMap(sessionId,"requirement");

if(reqMap==null)
reqMap=new HashMap<>();

Lead lead =
leadService.createVoiceAiLead(
name,
phone,
reqMap,
null
);

leadService.saveConversation(
lead,
msg,
"Lead Created from Voice AI",
"VOICE_AI"
);

session.clear(sessionId);

String reply =
translator.translate(
"Thank you "+name+
". Our property expert will contact you shortly.",
lang
);

return Map.of(
"reply",reply,
"stage","DONE",
"close","yes"
);
}


// ============================================
// STEP 3 : NLP EXTRACTION
// ============================================

Map<String,Object> r =
aiExtract.extract(msg);

if(r==null || r.isEmpty()){
r = ruleExtract.extract(msg);
}


// ============================================
// STEP 4 : MERGE REQUIREMENTS
// ============================================

Map<String,Object> oldReq =
session.getMap(sessionId,"requirement");

if(oldReq==null)
oldReq=new HashMap<>();

for(String key : r.keySet()){

Object val = r.get(key);
if(val==null) continue;

if(key.equalsIgnoreCase("budget")){

Long numeric =
BudgetParser.parseBudgetToNumber(
val.toString()
);

if(numeric!=null)
oldReq.put("budget",numeric);

continue;
}

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

if(!val.toString().isBlank())
oldReq.put(key,val);
}


// LOCATION → CITY FALLBACK
if(oldReq.containsKey("location") &&
!oldReq.containsKey("city")){

oldReq.put(
"city",
oldReq.get("location")
);
}

session.set(sessionId,"requirement",oldReq);


// ============================================
// STEP 5 : VALIDATE
// ============================================

if(!filter.isValidRequirement(oldReq)){

String reply =
translator.translate(
"Please tell me property type, city or BHK so I can check availability.",
lang
);

return Map.of(
"reply",reply,
"stage","SEARCH",
"close","no"
);
}


// ============================================
// STEP 6 : SEARCH
// ============================================

List<Project> list =
projectService.progressiveSearch(oldReq);


// ============================================
// STEP 7 : MATCH FOUND
// ============================================

if(!list.isEmpty()){

StringBuilder props = new StringBuilder();

String intro =
translator.translate(
"We found these matching properties for you:",
lang
);

props.append(intro).append("\n");

int count = 1;

for(Project p : list){

String line =
p.getName()+
" at "+
p.getLocation()+
" starting from ₹"+
p.getPriceStart();

String translatedLine =
translator.translate(line,lang);

props.append(count++)
.append(". ")
.append(translatedLine)
.append(".\n");
}

String askName =
translator.translate(
"May I know your name?",
lang
);

session.set(sessionId,"askName","yes");

return Map.of(
"reply",props.toString()+"\n"+askName,
"stage","NAME",
"close","no"
);
}


// ============================================
// STEP 8 : NOT FOUND
// ============================================

String reply =
translator.translate(
"Sorry, I couldn't find an exact match. May I know your name so our expert can assist you?",
lang
);

session.set(sessionId,"askName","yes");

return Map.of(
"reply",reply,
"stage","NAME",
"close","no"
);

}
}
