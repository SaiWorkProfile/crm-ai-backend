package com.realestate.ai.service;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RealEstateFilterService {

public boolean isValidRequirement(
Map<String,Object> r){

if(r==null) return false;

boolean hasBhk =
r.get("bhk")!=null;

boolean hasType =
r.get("propertyType")!=null;

boolean hasCity =
r.get("city")!=null;

boolean hasLocation =
r.get("location")!=null;


// 🔥 MUST HAVE STRUCTURE + PLACE
return
(
(hasBhk || hasType)
&&
(hasCity || hasLocation)
);

}
}
