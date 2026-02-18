package com.realestate.ai.util;

import java.util.List;

public class RepeatGuard {

private static final List<String> REPEAT = List.of(

 // English
 "repeat",
 "again",
 "once again",
 "say again",
 "can you repeat",
 "can you say that again",
 "didn't get you",
 "what",
 "sorry",

 // Hindi
 "फिर से",
 "एक बार और",
 "phirse bolo",

 // Telugu
 "మళ్లీ చెప్పండి",
 "malli cheppandi",

 // Tamil
 "மீண்டும் சொல்லுங்கள்",

 // Kannada
 "ಮತ್ತೊಮ್ಮೆ ಹೇಳಿ",

 // Malayalam
 "വീണ്ടും പറയൂ"
);

public static boolean isRepeat(String msg){

if(msg==null) return false;

msg=msg.toLowerCase().trim();

for(String r:REPEAT){

if(msg.equals(r) ||
msg.startsWith(r+" ") ||
msg.endsWith(" "+r))
return true;

}

return false;
}
}
