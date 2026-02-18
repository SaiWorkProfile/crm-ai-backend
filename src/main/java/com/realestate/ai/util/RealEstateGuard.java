package com.realestate.ai.util;

import java.util.List;
import java.util.regex.Pattern;

public class RealEstateGuard {

private static final List<String> PROPERTY_WORDS = List.of(

"flat","apartment","villa","plot","land","house","home",
"office","commercial",

// Hindi
"मकान","फ्लैट","जमीन","प्लॉट",

// Telugu
"ఇల్లు","ఫ్లాట్","విల్లా","ప్లాట్",

// Tamil
"வீடு","பிளாட்","வில்லா","நிலம்",

// Kannada
"ಮನೆ","ಫ್ಲಾಟ್","ವಿಲ್ಲಾ","ಭೂಮಿ",

// Marathi
"घर","फ्लॅट","जमीन"
);


// 🔥 BHK VOICE SPEECH SUPPORT
private static final Pattern BHK_PATTERN =
Pattern.compile(
"(1|one|single|2|two|double|3|three|4|four)\\s*(bhk|bed(room)?)"
);


// 🔥 PRICE SPEECH SUPPORT
private static final Pattern PRICE_PATTERN =
Pattern.compile(
"(lakh|lakhs|crore|cr|budget)"
);


// 🔥 LOCALITY SPEECH SUPPORT
private static final Pattern LOCATION_HINT =
Pattern.compile(
"(near|in|lo|mein)"
);


public static boolean isRealEstateQuery(
String message){

if(message==null ||
message.isBlank())
return false;

String lower =
message.toLowerCase();


// PROPERTY WORD
for(String word:PROPERTY_WORDS){
if(lower.contains(word))
return true;
}


// BHK VOICE
if(BHK_PATTERN.matcher(lower)
.find())
return true;


// PRICE
if(PRICE_PATTERN.matcher(lower)
.find())
return true;


// CITY
if(IndianCities.containsCity(lower))
return true;


// LOCATION SPEECH
if(LOCATION_HINT.matcher(lower)
.find())
return true;


return false;
}
}
