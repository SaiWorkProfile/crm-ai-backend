package com.realestate.ai.controller;

import java.util.*;
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

@Autowired private RealEstateNlpService aiExtract;
@Autowired private RequirementExtractionService ruleExtract;
@Autowired private ProjectAIService projectService;
@Autowired private VoiceSessionService session;
@Autowired private LeadService leadService;
@Autowired private RealEstateFilterService filter;
@Autowired private TranslationService translator;


// ================== MAIN VOICE ==================
@PostMapping("/ask")
public Map<String,String> ask(
@RequestBody Map<String,String> req){

String sessionId = req.get("sessionId");
String msg = req.get("message");

if(msg==null || msg.isBlank()){
return Map.of(
"reply","Sorry, I didn't catch that.",
"stage","SEARCH",
"close","no"
);
}

msg = msg.toLowerCase();

if(sessionId==null || sessionId.isBlank()){
return Map.of(
"reply","Session expired.",
"stage","DONE",
"close","yes"
);
}


// 🔥 AUTO LANGUAGE DETECT
String lang = translator.detectLanguage(msg);

System.out.println("🎤 USER: "+msg+" | LANG:"+lang);


// ================= ASK NAME =================
if(session.has(sessionId,"askName")){

session.set(sessionId,"name",msg);
session.remove(sessionId,"askName");
session.set(sessionId,"askPhone","yes");

return Map.of(
"reply",
translator.translate(
"Please share your 10 digit phone number.",
lang
),
"stage","PHONE",
"close","no"
);
}


// ================= ASK PHONE =================
if(session.has(sessionId,"askPhone")){

msg = SpokenNumberParser.parse(msg);
String phone = msg.replaceAll("\\D","");

if(phone.length()!=10 ||
!phone.matches("^[6-9]\\d{9}$")){

return Map.of(
"reply",
translator.translate(
"Please say valid 10 digit phone number.",
lang
),
"stage","PHONE",
"close","no"
);
}

String name = session.getString(sessionId,"name");

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

return Map.of(
"reply",
translator.translate(
"Thank you "+name+
". Our property expert will contact you shortly.",
lang
),
"stage","DONE",
"close","yes"
);
}


// ================= NLP =================
Map<String,Object> r =
aiExtract.extract(msg);

if(r==null || r.isEmpty())
r = ruleExtract.extract(msg);


// ================= MERGE =================
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
val.toString().replaceAll("\\D","")
);
oldReq.put("bhk",bhk);
}catch(Exception e){}
continue;
}

if(!val.toString().isBlank())
oldReq.put(key,val);
}

if(oldReq.containsKey("location") &&
!oldReq.containsKey("city")){
oldReq.put(
"city",
oldReq.get("location")
);
}

session.set(sessionId,"requirement",oldReq);


// ================= VALIDATE =================
if(!filter.isValidRequirement(oldReq)){

return Map.of(
"reply",
translator.translate(
"Please tell me property type, city or BHK so I can check availability.",
lang
),
"stage","SEARCH",
"close","no"
);
}


// ================= SEARCH =================
List<Project> list =
projectService.progressiveSearch(oldReq);


// ================= FOUND =================
if(!list.isEmpty()){

StringBuilder props =
new StringBuilder(
translator.translate(
"We found these matching properties for you:",
lang
)
).append("\n");

int count=1;

for(Project p:list){

String name =
translator.translate(
p.getName(),lang
);

String location =
translator.translate(
p.getLocation(),lang
);

String price =
translator.translate(
"starting from ₹"+p.getPriceStart(),
lang
);

props.append(count++)
.append(". ")
.append(name)
.append(" ")
.append(translator.translate("at",lang))
.append(" ")
.append(location)
.append(" ")
.append(price)
.append(".\n");
}

props.append(
translator.translate(
"May I know your name?",
lang
)
);

session.set(sessionId,"askName","yes");

return Map.of(
"reply",props.toString(),
"stage","NAME",
"close","no"
);
}


// ================= NOT FOUND =================
session.set(sessionId,"askName","yes");

return Map.of(
"reply",
translator.translate(
"Sorry, I couldn't find an exact match.",
lang
)
+" "+
translator.translate(
"May I know your name so our expert can assist you?",
lang
),
"stage","NAME",
"close","no"
);

}
}
