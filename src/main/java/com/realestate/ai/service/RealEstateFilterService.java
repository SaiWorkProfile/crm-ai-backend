package com.realestate.ai.service;


import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RealEstateFilterService {

    public boolean isValidRequirement(Map<String,Object> r){

        if(r==null) return false;

        return
        r.get("bhk")!=null ||
        r.get("city")!=null ||
        r.get("location")!=null ||
        r.get("propertyType")!=null;
    }
}
