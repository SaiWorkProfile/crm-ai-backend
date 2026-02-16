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


// 🔥 BHK MUST BE STRING (DB HAS 2BHK)
String bhk =
req.get("bhk")!=null ?
req.get("bhk").toString() : null;


// PROPERTY TYPE
String type =
req.get("propertyType")!=null ?
req.get("propertyType").toString() : null;


// GATED
Boolean gated =
req.get("gatedCommunity")!=null ?
Boolean.valueOf(
req.get("gatedCommunity").toString()) : null;


// BUDGET (NUMERIC)
Long budget =
req.get("budget")!=null ?
Long.parseLong(
req.get("budget").toString()) : null;


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
