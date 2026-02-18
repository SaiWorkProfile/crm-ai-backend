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
You are an Indian Real Estate requirement extraction engine.

Extract ONLY the following:

bhk
propertyType
budget
gatedCommunity

DO NOT extract:
city
location
area
place

City and location will be handled separately from database.

Return STRICT JSON ONLY.

{
 "bhk": null,
 "propertyType": null,
 "budget": null,
 "gatedCommunity": null
}

Examples:

"I want 2bhk in Hyderabad"
→ {"bhk":"2BHK"}

"Need villa gated community"
→ {"propertyType":"Villa","gatedCommunity":true}

"Flat under 50 lakhs"
→ {"budget":"50 lakh"}

Sentence:
""" + msg;


Map<String,Object> body =
Map.of(
 "model","llama-3.3-70b-versatile",
 "temperature",0,
 "messages",List.of(
  Map.of("role","user","content",prompt)
 ),
 "response_format",
 Map.of("type","json_object")
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
