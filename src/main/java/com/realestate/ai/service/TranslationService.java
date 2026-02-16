package com.realestate.ai.service;

import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TranslationService {

@Value("${groq.api-key}")
private String apiKey;

@Autowired
private RestTemplate restTemplate;

private static final String GROQ_URL =
"https://api.groq.com/openai/v1/chat/completions";


// 🔥 MAIN TRANSLATE METHOD
public String translate(String text,String lang){

// NO NEED TO TRANSLATE ENGLISH
if(lang==null || lang.equalsIgnoreCase("ENGLISH"))
return text;

try{

// ✅ VALID JAVA TEXT BLOCK
String prompt = """
Translate the following real estate sentence into %s.
Return ONLY translated sentence.

Sentence:
%s
""".formatted(lang,text);


// 🔥 GROQ BODY
Map<String,Object> body =
Map.of(
"model","llama-3.3-70b-versatile",
"temperature",0,
"messages",List.of(
Map.of("role","user","content",prompt)
)
);


// 🔥 HEADERS
HttpHeaders headers=new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth(apiKey);


// 🔥 CALL
HttpEntity<Map<String,Object>> entity =
new HttpEntity<>(body,headers);

ResponseEntity<Map> response =
restTemplate.postForEntity(
GROQ_URL,
entity,
Map.class
);


// 🔥 RESPONSE
Map res=response.getBody();

List choices=(List)res.get("choices");

Map choice=(Map)choices.get(0);

Map msg=(Map)choice.get("message");

return msg.get("content").toString();

}catch(Exception e){

System.out.println("TRANSLATE ERROR: "+e.getMessage());

return text;
}
}
public String detectLanguage(String text){

text = text.toLowerCase();

// HINDI CHECK
if(text.matches(".*[\\u0900-\\u097F]+.*") ||
text.contains("hai") ||
text.contains("chahiye") ||
text.contains("mein") ||
text.contains("kya") ||
text.contains("mujhe")){
return "hi";
}

// TELUGU CHECK
if(text.matches(".*[\\u0C00-\\u0C7F]+.*")){
return "te";
}

// TAMIL CHECK
if(text.matches(".*[\\u0B80-\\u0BFF]+.*")){
return "ta";
}

// DEFAULT ENGLISH
return "en";
}

}
