package com.realestate.ai.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.ai.model.Project;
import com.realestate.ai.repository.ProjectRepository;

@Service
public class ProjectAIService {

@Autowired
private ProjectRepository projectRepository;


public List<Project> progressiveSearch(
Map<String,Object> req){

// CITY
String city =
req.get("city")!=null ?
req.get("city").toString() : null;


// 🔥 FIX BHK FORMAT
String bhk=null;

if(req.get("bhk")!=null){

try{

Integer b =
Integer.parseInt(
req.get("bhk").toString()
.replaceAll("\\D","")
);

bhk=b+"BHK";

}catch(Exception e){}
}


// PROPERTY TYPE
String type =
req.get("propertyType")!=null ?
req.get("propertyType").toString() : null;


// GATED
Boolean gated =
req.get("gatedCommunity")!=null ?
Boolean.valueOf(
req.get("gatedCommunity").toString()) : null;


// BUDGET
Long budget=null;

try{

budget =
req.get("budget")!=null ?
Long.parseLong(
req.get("budget").toString()) : null;

}catch(Exception e){}


// CALL PROGRESSIVE MATCH
return projectRepository
.progressiveMatch(
city,
bhk,
type,
gated,
budget
);

}

}
