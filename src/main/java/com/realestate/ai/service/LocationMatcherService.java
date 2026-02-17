package com.realestate.ai.service;

import java.util.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.realestate.ai.repository.ProjectRepository;

@Service
public class LocationMatcherService {

@Autowired
private ProjectRepository repo;

private final LevenshteinDistance ld =
new LevenshteinDistance();


// 🔥 AUTO MATCH USER CITY/LOCATION
public String findClosestLocation(String input){

if(input==null) return null;

input=input.toLowerCase();

List<String> dbLocations =
repo.findAllLocations();

String bestMatch=null;
int min=999;

for(String loc:dbLocations){

int d=ld.apply(
input,
loc.toLowerCase()
);

if(d<min){
min=d;
bestMatch=loc;
}
}

// distance <5 = match
return min<=5?bestMatch:input;
}
}
