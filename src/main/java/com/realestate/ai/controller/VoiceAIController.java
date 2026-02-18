package com.realestate.ai.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.realestate.ai.model.Lead;
import com.realestate.ai.model.Project;
import com.realestate.ai.service.*;
import com.realestate.ai.util.BudgetParser;
import com.realestate.ai.util.FarewellGuard;
import com.realestate.ai.util.GreetingGuard;
import com.realestate.ai.util.RepeatGuard;
import com.realestate.ai.util.SpokenNumberParser;

@RestController
@RequestMapping("/api/voice")
@CrossOrigin
public class VoiceAIController {

@Autowired private DbLocationResolverService dbResolver;
@Autowired private RealEstateNlpService aiExtract;
@Autowired private RequirementExtractionService ruleExtract;
@Autowired private ProjectAIService projectService;
@Autowired private VoiceSessionService session;
@Autowired private LeadService leadService;
@Autowired private RealEstateFilterService filter;
@Autowired private TranslationService translator;
@Autowired private LocationMatcherService matcher;


// ================= CASUAL TALK FILTER =================
private boolean isNonRealEstateQuery(String msg){

return msg.contains("your name") ||
msg.contains("who are you") ||
msg.contains("weather") ||
msg.contains("time") ||
msg.contains("joke") ||
msg.contains("news") ||
msg.contains("sports") ||
msg.contains("movie");
}


// ================== MAIN VOICE ==================
@PostMapping("/ask")
public Map<String,String> ask(@RequestBody Map<String,String> req){

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


// 🔥 LANGUAGE DETECT
String lang = translator.detectLanguage(msg);
if(!List.of("en","hi","te","ta","kn","ml").contains(lang))
lang="en";

System.out.println("🎤 USER: "+msg+" | LANG:"+lang);


// ================= REPEAT =================
if(RepeatGuard.isRepeat(msg)){

String lastReply=session.getString(sessionId,"lastReply");
String lastStage=session.getString(sessionId,"lastStage");

if(lastReply!=null){
return Map.of(
"reply",lastReply,
"stage",lastStage!=null?lastStage:"SEARCH",
"close","no"
);
}
}


// ================= GREETING =================
if(GreetingGuard.isGreeting(msg)){

String reply=translator.translate(
"Hello, I can assist you with real estate projects. Please tell me your requirement.",
lang
);

session.set(sessionId,"lastReply",reply);
session.set(sessionId,"lastStage","SEARCH");

return Map.of(
"reply",reply,
"stage","SEARCH",
"close","no"
);
}


// ================= FAREWELL =================
if(FarewellGuard.isFarewell(msg)){

session.clear(sessionId);

return Map.of(
"reply",
translator.translate(
"Thank you for contacting us. Have a great day.",
lang
),
"stage","DONE",
"close","yes"
);
}


// ================= NON REAL ESTATE =================
if(isNonRealEstateQuery(msg)){

String reply=translator.translate(
"I assist only with real estate consultation professionally.",
lang
);

session.set(sessionId,"lastReply",reply);
session.set(sessionId,"lastStage","SEARCH");

return Map.of(
"reply",reply,
"stage","SEARCH",
"close","no"
);
}


// ================= ASK NAME =================
if(session.has(sessionId,"askName")){

session.set(sessionId,"name",msg);
session.remove(sessionId,"askName");
session.set(sessionId,"askPhone","yes");

String reply=translator.translate(
"Please share your 10 digit phone number.",
lang
);

session.set(sessionId,"lastReply",reply);
session.set(sessionId,"lastStage","PHONE");

return Map.of(
"reply",reply,
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
null);

// 🔥 SAVE CONVERSATION
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
Map<String,Object> r = aiExtract.extract(msg);
if(r==null || r.isEmpty())
r = ruleExtract.extract(msg);


// ================= DB SMART MATCH =================
String dbCity = dbResolver.resolveCity(msg);
String dbLocation = dbResolver.resolveLocation(msg);

if(dbCity!=null)
r.put("city",dbCity);

if(dbLocation!=null)
r.put("location",dbLocation);


// ================= MERGE =================
Map<String,Object> oldReq =
session.getMap(sessionId,"requirement");

if(oldReq==null)
oldReq=new HashMap<>();

for(String key : r.keySet()){

Object val = r.get(key);
if(val==null) continue;


// 🔥 Budget
if(key.equalsIgnoreCase("budget")){
Long numeric =
BudgetParser.parseBudgetToNumber(val.toString());
if(numeric!=null)
oldReq.put("budget",numeric);
continue;
}


// 🔥 BHK FIX
if(key.equalsIgnoreCase("bhk")){
try{
Integer bhk =
Integer.parseInt(
val.toString().replaceAll("\\D","")
);
oldReq.put("bhk",bhk+"BHK");
}catch(Exception e){}
continue;
}


// 🔥 CITY MATCH
if(key.equalsIgnoreCase("city")){
String closest =
matcher.findClosestLocation(
val.toString()
);
oldReq.put("city",closest);
continue;
}


// 🔥 LOCATION MATCH
if(key.equalsIgnoreCase("location")){
String closest =
matcher.findClosestLocation(
val.toString()
);
oldReq.put("location",closest);
continue;
}

oldReq.put(key,val);
}


// 🔥 LOCATION → CITY AUTO MAP
if(oldReq.containsKey("location") &&
!oldReq.containsKey("city")){
oldReq.put("city",oldReq.get("location"));
}

session.set(sessionId,"requirement",oldReq);


// ================= VALIDATE =================
if(!filter.isValidRequirement(oldReq)){

String reply=translator.translate(
"Please tell me property type, city or BHK so I can check availability.",
lang
);

session.set(sessionId,"lastReply",reply);
session.set(sessionId,"lastStage","SEARCH");

return Map.of(
"reply",reply,
"stage","SEARCH",
"close","no"
);
}


// ================= SEARCH =================
List<Project> list =
projectService.progressiveSearch(oldReq);


// ================= FOUND =================
if(!list.isEmpty()){

Project p = list.get(0);

String reply =
translator.translate("Yes, we have",lang)+" "+
translator.translate(p.getName(),lang)+" "+
translator.translate("located in",lang)+" "+
translator.translate(p.getLocation(),lang)+" "+
translator.translate("offering",lang)+" "+
translator.translate(p.getBhk(),lang)+" "+
translator.translate("starting from rupees",lang)+" "+
p.getPriceStart()+
". "+
translator.translate(
"May I know your name so our expert can assist you?",
lang
);

session.set(sessionId,"askName","yes");

// 🔥 STORE LAST AI REPLY
session.set(sessionId,"lastReply",reply);
session.set(sessionId,"lastStage","NAME");

return Map.of(
"reply",reply,
"stage","NAME",
"close","no"
);
}


// ================= NOT FOUND =================
session.set(sessionId,"askName","yes");

String reply =
translator.translate(
"Sorry, I couldn't find an exact match.",
lang
)+" "+
translator.translate(
"May I know your name so our expert can assist you?",
lang
);

session.set(sessionId,"lastReply",reply);
session.set(sessionId,"lastStage","NAME");

return Map.of(
"reply",reply,
"stage","NAME",
"close","no"
);

}
}
