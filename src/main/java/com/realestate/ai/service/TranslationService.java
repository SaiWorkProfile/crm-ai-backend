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


// ================= LANG NAME =================
private String langName(String lang){

return switch(lang){

case "te" -> "Telugu";
case "hi" -> "Hindi";
case "ta" -> "Tamil";
case "kn" -> "Kannada";
case "ml" -> "Malayalam";

default -> "English";
};
}


// ================= TRANSLATE =================
public String translate(String text,String lang){

String language = langName(lang);

// ENGLISH NO NEED
if(language.equals("English"))
return text;

try{

String prompt = """
You are a professional Indian Real Estate Voice Assistant.

Translate the following real estate sentence into %s.

Allowed output languages ONLY:
English, Hindi, Telugu, Tamil, Kannada, Malayalam.

Never translate into Spanish, French or any other language.

Use polite professional property consultation tone.

Return ONLY translated sentence.

Sentence:
%s
""".formatted(language,text);


// ================= BODY =================
Map<String,Object> body =
Map.of(
"model","llama-3.3-70b-versatile",
"temperature",0,
"messages",List.of(
Map.of("role","user","content",prompt)
)
);


// ================= HEADERS =================
HttpHeaders headers=new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth(apiKey);


// ================= CALL =================
HttpEntity<Map<String,Object>> entity =
new HttpEntity<>(body,headers);

ResponseEntity<Map> response =
restTemplate.postForEntity(
GROQ_URL,
entity,
Map.class
);


// ================= RESPONSE =================
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


// ================= LANGUAGE DETECT =================
public String detectLanguage(String text){

text = text.toLowerCase();


// ===== TELUGU SCRIPT =====
if(text.matches(".*[\\u0C00-\\u0C7F]+.*"))
return "te";


// ===== HINDI SCRIPT =====
if(text.matches(".*[\\u0900-\\u097F]+.*"))
return "hi";


// ===== TAMIL SCRIPT =====
if(text.matches(".*[\\u0B80-\\u0BFF]+.*"))
return "ta";


// ===== KANNADA SCRIPT =====
if(text.matches(".*[\\u0C80-\\u0CFF]+.*"))
return "kn";


// ===== MALAYALAM SCRIPT =====
if(text.matches(".*[\\u0D00-\\u0D7F]+.*"))
return "ml";


// ===== TELUGU HINGLISH =====
if(text.contains("kavali")
|| text.contains("lo")
|| text.contains("undi")
|| text.contains("ivvandi")
|| text.contains("unnaya"))
return "te";


// ===== HINDI HINGLISH =====
if(text.contains("mujhe")
|| text.contains("chahiye")
|| text.contains("ghar")
|| text.contains("kya")
|| text.contains("hai"))
return "hi";


// ===== TAMIL HINGLISH =====
if(text.contains("venum")
|| text.contains("iruku"))
return "ta";


return "en";
}

}
