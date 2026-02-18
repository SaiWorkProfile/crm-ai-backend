package com.realestate.ai.util;

import java.util.List;

public class FarewellGuard {

private static final List<String> FAREWELLS = List.of(

 // English
 "bye","ok bye","goodbye",
 "thanks","thank you",
 "ok thanks","thank you sir",
 "thank you madam",
 "that's it","done","enough",

 // Hindi
 "ठीक है","बस","धन्यवाद",

 // Telugu
 "సరే","చాలు","ధన్యవాదాలు",
 "అయిపోయింది",

 // Tamil
 "சரி","நன்றி",

 // Kannada
 "ಸರಿ","ಧನ್ಯವಾದಗಳು",

 // Malayalam
 "ശരി","നന്ദി",

 // Marathi
 "ठीक आहे","धन्यवाद"
);

public static boolean isFarewell(String msg){

if(msg==null) return false;

msg=msg.toLowerCase().trim();

for(String f:FAREWELLS){

if(msg.equals(f) ||
msg.startsWith(f+" ") ||
msg.endsWith(" "+f))
return true;

}

return false;
}
}
