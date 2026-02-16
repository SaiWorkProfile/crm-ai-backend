package com.realestate.ai.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.*;

@Service
public class RealEstateNlpService {

@Autowired
private WebClient.Builder builder;

@Autowired
private ObjectMapper mapper;

@Value("${groq.api-key}")
private String groqApiKey;


// 🔥 USED BY BOTH VOICE + WEBSITE CHAT
public Map<String,Object> extract(String msg){

try{

String prompt = """
You are a real estate NLP engine.

Extract requirements from this sentence.

Return STRICT JSON ONLY.

{
 "bhk": null,
 "propertyType": null,
 "city": null,
 "location": null,
 "budget": null,
 "status": null,
 "gatedCommunity": null
}

Examples:

"I want 2bhk in Hyderabad"
→ {"bhk":"2BHK","city":"Hyderabad"}

"Need villa in Bangalore gated community"
→ {"propertyType":"Villa","city":"Bangalore","gatedCommunity":true}

Sentence:
""" + msg;


Map<String,Object> body =
Map.of(
 "model","llama-3.3-70b-versatile",   // 🔥 WORKING MODEL
 "temperature",0,
 "messages",List.of(
  Map.of("role","user","content",prompt)
 ),
 "response_format",
 Map.of("type","json_object")          // 🔥 VERY IMPORTANT
);


String response =
builder.build()
.post()
.uri("https://api.groq.com/openai/v1/chat/completions")
.header("Authorization","Bearer "+groqApiKey)
.header("Content-Type","application/json")
.bodyValue(body)
.retrieve()
.bodyToMono(String.class)
.block();


JsonNode root =
mapper.readTree(response);

String content =
root.path("choices")
.get(0)
.path("message")
.path("content")
.asText();

System.out.println("NLP JSON: "+content);

return mapper.readValue(content,Map.class);

}catch(Exception e){

System.out.println("NLP FAILED → "+e.getMessage());
return new HashMap<>();
}

}
}
