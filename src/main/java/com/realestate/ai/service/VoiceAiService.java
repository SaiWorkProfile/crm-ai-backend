package com.realestate.ai.service;

import com.realestate.ai.model.Project;
import com.realestate.ai.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VoiceAiService {

private final VoiceSessionService sessionService;
private final GroqService groqService;
private final ProjectRepository projectRepo;

public VoiceAiService(
VoiceSessionService sessionService,
GroqService groqService,
ProjectRepository projectRepo){

this.sessionService=sessionService;
this.groqService=groqService;
this.projectRepo=projectRepo;
}



// =======================================================
// 🔥 MAIN ASK FLOW
// =======================================================
public Map<String,String> ask(
String sessionId,
String msg){

Map<String,String> res=new HashMap<>();


// =======================================================
// NAME STAGE
// =======================================================
if("NAME".equals(sessionService.getString(sessionId,"stage"))){

sessionService.set(sessionId,"name",msg);
sessionService.set(sessionId,"stage","PHONE");

res.put("stage","PHONE");
res.put("reply",
"Thank you. May I have your 10 digit phone number so our expert can assist you?");
return res;
}


// =======================================================
// PHONE STAGE
// =======================================================
if("PHONE".equals(sessionService.getString(sessionId,"stage"))){

sessionService.set(sessionId,"phone",msg);

res.put("stage","DONE");
res.put("close","yes");
res.put("reply",
"Thank you. Our expert will contact you shortly regarding your property requirement.");
return res;
}



// =======================================================
// 🔥 EXTRACT REQUIREMENT
// =======================================================
String cityPrompt=
"Extract only the locality or city from:\n"+msg+
"\nReturn only place name";

String city=
groqService.callGroq(cityPrompt)
.replaceAll("[^a-zA-Z ]","")
.trim();


// =======================================================
// 🔥 LOCALITY → CITY
// Vepagunta → Visakhapatnam
// =======================================================
city = groqService.normalizeLocationAI(city);


// =======================================================
// 🔥 SAVE INTO SESSION
// =======================================================
sessionService.set(sessionId,"city",city);


// =======================================================
// 🔥 PROGRESSIVE DB MATCH
// =======================================================
String bhk = msg.contains("3")?"3bhk":
msg.contains("2")?"2bhk":null;

Long budget=parseBudget(msg);

List<Project> matches=
projectRepo.progressiveMatch(
city,
bhk,
null,
null,
budget
);


// =======================================================
// PROJECT FOUND
// =======================================================
if(!matches.isEmpty()){

Project p=matches.get(0);

String explain =
"Yes, we have " + p.getName() +
" located in " + p.getLocation() + ", " + p.getCity() +
" offering " + p.getBhk() +
" starting from " + p.getPriceStart() + ".";

sessionService.set(sessionId,"stage","NAME");

res.put("stage","NAME");
res.put("reply",
explain+
" May I know your name so our expert can assist you?");
return res;
}



// =======================================================
// ❌ NOT REAL ESTATE
// =======================================================
String realEstateCheck=
groqService.callGroq(
"Is this related to real estate property buying? "+
"Answer only YES or NO:\n"+msg
).toLowerCase();


// =======================================================
// CASUAL TALK → REDIRECT
// =======================================================
if(realEstateCheck.contains("no")){

res.put("stage","SEARCH");
res.put("reply",
"I can assist you professionally with real estate projects. "+
"Please let me know your preferred city or location.");
return res;
}



// =======================================================
// REAL ESTATE BUT NO MATCH
// =======================================================
sessionService.set(sessionId,"stage","NAME");

res.put("stage","NAME");
res.put("reply",
"I could not find an exact project match right now. "+
"May I know your name so our expert can assist you?");
return res;

}
private Long parseBudget(String text){

text=text.toLowerCase();

if(text.contains("cr"))
return Long.parseLong(
text.replaceAll("[^0-9]","")
)*10000000L;

if(text.contains("lakh"))
return Long.parseLong(
text.replaceAll("[^0-9]","")
)*100000L;

return null;
}

}
