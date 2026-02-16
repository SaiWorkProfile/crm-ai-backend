package com.realestate.ai.util;

import java.util.Set;

public class IndianCities {

    public static final Set<String> CITIES = Set.of(
        "bangalore","bengaluru",
        "hyderabad",
        "chennai",
        "mumbai",
        "pune",
        "delhi","noida","gurgaon","gurugram","faridabad","ghaziabad",
        "kolkata",
        "ahmedabad","surat",
        "jaipur","indore","bhopal",
        "coimbatore","madurai","trichy",
        "vijayawada","guntur","nellore",
        "visakhapatnam","vizag","tirupati",
        "warangal","nizamabad",
        "kochi","ernakulam",
        "trivandrum",
        "calicut",
        "thrissur"
    );

    public static boolean containsCity(String text) {
        if (text == null || text.isBlank()) return false;

        String lower = text.toLowerCase();
        return CITIES.stream().anyMatch(lower::contains);
    }
}
