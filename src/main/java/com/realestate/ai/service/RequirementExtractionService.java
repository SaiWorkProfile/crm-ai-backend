package com.realestate.ai.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class RequirementExtractionService {

public Map<String,Object> extract(
String msg){

msg=msg.toLowerCase();

Map<String,Object> map=
new HashMap<>();

if(msg.contains("1bhk"))
map.put("bhk","1BHK");

if(msg.contains("2bhk"))
map.put("bhk","2BHK");

if(msg.contains("3bhk"))
map.put("bhk","3BHK");

if(msg.contains("4bhk"))
map.put("bhk","4BHK");

if(msg.contains("gated"))
map.put("gated",true);

if(msg.contains("bangalore"))
map.put("city","Bangalore");

if(msg.contains("vizag"))
map.put("city","Vizag");

if(msg.contains("hyderabad"))
map.put("city","Hyderabad");

if(msg.contains("pune"))
map.put("city","Pune");

return map;
}
}
