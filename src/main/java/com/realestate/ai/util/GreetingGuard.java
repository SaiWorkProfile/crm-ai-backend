package com.realestate.ai.util;

import java.util.List;

public class GreetingGuard {

private static final List<String> GREETINGS = List.of(

 // English
 "hi","hello","hey","hi there",
 "good morning","good evening",
 "good afternoon",

 // Common Indian
 "namaste","namaskar",
 "namaskaram","namaskara",
 "vanakkam","salaam",
 "assalamualaikum",

 // Telugu
 "నమస్కారం",

 // Hindi
 "नमस्ते","नमस्कार",

 // Tamil
 "வணக்கம்",

 // Kannada
 "ನಮಸ್ಕಾರ",

 // Malayalam
 "നമസ്കാരം",

 // Marathi
 "नमस्कार",

 // Casual speech
 "hi sir","hello sir",
 "hi madam","hello madam",
 "hello ji","hi ji",
 "bro","sir","madam"
);

public static boolean isGreeting(String msg){

if(msg==null) return false;

msg=msg.toLowerCase().trim();

for(String g:GREETINGS){

if(msg.equals(g) ||
msg.startsWith(g+" ") ||
msg.endsWith(" "+g))
return true;

}

return false;
}
}
