package com.realestate.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GroqService {

@Value("${groq.api-key}")
private String apiKey;

private final RestTemplate restTemplate = new RestTemplate();


// =======================================================
// 🔥 CALL GROQ
// =======================================================
public String callGroq(String prompt){

String url="https://api.groq.com/openai/v1/chat/completions";

HttpHeaders headers=new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth(apiKey);

Map<String,Object> body=new HashMap<>();
body.put("model","llama3-70b-8192");

List<Map<String,String>> messages=new ArrayList<>();

messages.add(Map.of(
"role","system",
"content",
"You are an Indian real estate AI assistant. "+
"Only respond in English or Indian languages (Hindi, Telugu, Tamil, Kannada, Malayalam). "+
"Never respond in Spanish or any foreign language."
));

messages.add(Map.of(
"role","user",
"content",prompt
));

body.put("messages",messages);

HttpEntity<Map<String,Object>> entity=
new HttpEntity<>(body,headers);

ResponseEntity<Map> res=restTemplate.exchange(
url,
HttpMethod.POST,
entity,
Map.class
);

Map choice=(Map)
((List)res.getBody().get("choices"))
.get(0);

Map message=(Map)choice.get("message");

return message.get("content").toString();
}



// =======================================================
// 🧠 LOCATION NORMALIZER AI
// LOCALITY → CITY
// =======================================================
public String normalizeLocationAI(String userText){

String prompt=
"You are an Indian real estate location expert.\n"+
"If the user says a locality, area, landmark, nickname or suburb, convert it into its official Indian CITY name.\n\n"+

"Examples:\n"+
"Vizag → Visakhapatnam\n"+
"City of destiny → Visakhapatnam\n"+
"Rushikonda → Visakhapatnam\n"+
"Vepagunta → Visakhapatnam\n"+
"Gajuwaka → Visakhapatnam\n"+
"Madhurawada → Visakhapatnam\n"+
"Steel plant → Visakhapatnam\n"+

"Gachibowli → Hyderabad\n"+
"Kukatpally → Hyderabad\n"+
"Financial district → Hyderabad\n"+
"Cyber city → Hyderabad\n"+

"Whitefield → Bangalore\n"+
"IT hub → Bangalore\n\n"+

"Return ONLY the CITY name.\n"+
"Do NOT explain anything.\n"+
"User Input:\n"+userText;

String city = callGroq(prompt);

return city
.replaceAll("[^a-zA-Z ]","")
.trim()
.toLowerCase();
}

}
