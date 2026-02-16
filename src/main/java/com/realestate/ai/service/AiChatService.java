package com.realestate.ai.service;

import com.realestate.ai.dto.AiChatMessage;
import com.realestate.ai.dto.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiChatService {

@Value("${groq.api-key}")
private String apiKey;

private final RestTemplate restTemplate;

public AiChatService(RestTemplate restTemplate){
this.restTemplate=restTemplate;
}

private static final String GROQ_URL =
"https://api.groq.com/openai/v1/chat/completions";


// 🔥 NORMAL CHAT ONLY
//🔥 NORMAL CHAT WITH LANGUAGE SUPPORT
public String chat(
List<AiChatMessage> messages,
Language language
){

try{

List<Map<String,String>> payload =
new ArrayList<>();

//================= LANGUAGE FORCE =================

String langPrompt = switch(language){

case TAMIL -> """
You MUST reply ONLY in Tamil language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in Tamil script.
""";

case TELUGU -> """
You MUST reply ONLY in Telugu language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in Telugu script.
""";

case MALAYALAM -> """
You MUST reply ONLY in Malayalam language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in Malayalam script.
""";

case HINDI -> """
You MUST reply ONLY in Hindi language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in Hindi script.
""";

default -> """
Reply ONLY in English.
""";
};

//================= SYSTEM PROMPT =================

payload.add(Map.of(
"role","system",
"content",
"""
You are a professional real estate assistant.

Speak short and polite.
Do not explain too much.
Do not give market education.
Do not provide agent contacts.

""" + langPrompt
));

//================= FULL CHAT HISTORY =================

for(AiChatMessage m:messages){

payload.add(
Map.of(
"role",m.getRole(),
"content",m.getContent()
));
}

//================= GROQ REQUEST =================

Map<String,Object> body =
Map.of(
"model","llama-3.3-70b-versatile",
"temperature",0.5,
"messages",payload
);

HttpHeaders headers =
new HttpHeaders();

headers.setContentType(
MediaType.APPLICATION_JSON
);

headers.setBearerAuth(apiKey);

HttpEntity<Map<String,Object>> entity =
new HttpEntity<>(body,headers);

ResponseEntity<Map> response =
restTemplate.postForEntity(
GROQ_URL,
entity,
Map.class
);

Map res = response.getBody();

List choices =
(List)res.get("choices");

Map choice =
(Map)choices.get(0);

Map msg =
(Map)choice.get("message");

return msg.get("content").toString();

}catch(Exception e){

return "Sorry, I'm unable to respond right now.";
}
}

}
