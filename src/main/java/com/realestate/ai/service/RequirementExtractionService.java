package com.realestate.ai.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RequirementExtractionService {

public Map<String,Object> extract(String msg){

msg = msg.toLowerCase();

Map<String,Object> map = new HashMap<>();


// ================= BHK =================
if(msg.matches(".*\\b1\\s*bhk\\b.*") ||
msg.contains("single bedroom") ||
msg.contains("1 bedroom") ||
msg.contains("one bhk"))
map.put("bhk","1BHK");

if(msg.matches(".*\\b2\\s*bhk\\b.*") ||
msg.contains("double bedroom") ||
msg.contains("2 bedroom") ||
msg.contains("two bhk"))
map.put("bhk","2BHK");

if(msg.matches(".*\\b3\\s*bhk\\b.*") ||
msg.contains("triple bedroom") ||
msg.contains("3 bedroom") ||
msg.contains("three bhk"))
map.put("bhk","3BHK");

if(msg.matches(".*\\b4\\s*bhk\\b.*") ||
msg.contains("4 bedroom") ||
msg.contains("four bhk"))
map.put("bhk","4BHK");


// ================= PROPERTY TYPE =================
if(msg.contains("villa") ||
msg.contains("independent house") ||
msg.contains("duplex"))
map.put("propertyType","Villa");

if(msg.contains("flat") ||
msg.contains("apartment"))
map.put("propertyType","Apartment");

if(msg.contains("plot") ||
msg.contains("land"))
map.put("propertyType","Plot");


// ================= GATED =================
if(msg.contains("gated") ||
msg.contains("gated community") ||
msg.contains("security"))
map.put("gatedCommunity",true);


// ================= BUDGET WORDS =================
if(msg.contains("lakh") ||
msg.contains("crore") ||
msg.contains("budget") ||
msg.matches(".*\\d+.*"))
map.put("budget","yes");


// 🚨 NO CITY OR LOCATION HERE
// DB resolver + matcher will do Pan-India match


return map;
}
}
