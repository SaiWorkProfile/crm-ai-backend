package com.realestate.ai.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.realestate.ai.repository.ProjectRepository;

@Service
public class DbLocationResolverService {

@Autowired
private ProjectRepository projectRepo;


// ================= MATCH CITY FROM DB =================
public String resolveCity(String msg){

msg = msg.toLowerCase();

List<String> cities = projectRepo.findAllCities();

for(String c : cities){

String clean = c.toLowerCase();

if(msg.contains(clean))
return clean;

// VIZAG → VISAKHAPATNAM TYPE MATCH
if(clean.startsWith(msg) || msg.startsWith(clean))
return clean;

if(levenshtein(msg,clean) <= 3)
return clean;

}

return null;
}


// ================= MATCH LOCATION =================
public String resolveLocation(String msg){

msg = msg.toLowerCase();

List<String> locs = projectRepo.findAllLocations();

for(String l : locs){

String clean = l.toLowerCase();

if(msg.contains(clean))
return clean;

if(levenshtein(msg,clean) <= 3)
return clean;
}

return null;
}


// ================= FUZZY MATCH =================
private int levenshtein(String a,String b){

int[][] dp=new int[a.length()+1][b.length()+1];

for(int i=0;i<=a.length();i++)
dp[i][0]=i;

for(int j=0;j<=b.length();j++)
dp[0][j]=j;

for(int i=1;i<=a.length();i++){
for(int j=1;j<=b.length();j++){

dp[i][j]=Math.min(
Math.min(
dp[i-1][j]+1,
dp[i][j-1]+1),
dp[i-1][j-1]+
(a.charAt(i-1)==b.charAt(j-1)?0:1)
);
}
}
return dp[a.length()][b.length()];
}
}
