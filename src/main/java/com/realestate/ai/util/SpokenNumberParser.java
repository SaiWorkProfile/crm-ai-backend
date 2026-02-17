package com.realestate.ai.util;

public class SpokenNumberParser {

public static String parse(String msg){

msg = msg.toLowerCase();

/* -------- ENGLISH -------- */
msg = msg.replaceAll("zero","0");
msg = msg.replaceAll("one","1");
msg = msg.replaceAll("two","2");
msg = msg.replaceAll("three","3");
msg = msg.replaceAll("four","4");
msg = msg.replaceAll("five","5");
msg = msg.replaceAll("six","6");
msg = msg.replaceAll("seven","7");
msg = msg.replaceAll("eight","8");
msg = msg.replaceAll("nine","9");

/* -------- HINDI -------- */
msg = msg.replaceAll("शून्य","0");
msg = msg.replaceAll("एक","1");
msg = msg.replaceAll("दो","2");
msg = msg.replaceAll("तीन","3");
msg = msg.replaceAll("चार","4");
msg = msg.replaceAll("पांच","5");
msg = msg.replaceAll("छह","6");
msg = msg.replaceAll("सात","7");
msg = msg.replaceAll("आठ","8");
msg = msg.replaceAll("नौ","9");

/* -------- TELUGU -------- */
msg = msg.replaceAll("సున్నా","0");
msg = msg.replaceAll("ఒకటి","1");
msg = msg.replaceAll("రెండు","2");
msg = msg.replaceAll("మూడు","3");
msg = msg.replaceAll("నాలుగు","4");
msg = msg.replaceAll("ఐదు","5");
msg = msg.replaceAll("ఆరు","6");
msg = msg.replaceAll("ఏడు","7");
msg = msg.replaceAll("ఎనిమిది","8");
msg = msg.replaceAll("తొమ్మిది","9");

/* -------- TAMIL -------- */
msg = msg.replaceAll("பூஜ்ஜியம்","0");
msg = msg.replaceAll("ஒன்று","1");
msg = msg.replaceAll("இரண்டு","2");
msg = msg.replaceAll("மூன்று","3");
msg = msg.replaceAll("நான்கு","4");
msg = msg.replaceAll("ஐந்து","5");
msg = msg.replaceAll("ஆறு","6");
msg = msg.replaceAll("ஏழு","7");
msg = msg.replaceAll("எட்டு","8");
msg = msg.replaceAll("ஒன்பது","9");

/* -------- MALAYALAM -------- */
msg = msg.replaceAll("പൂജ്യം","0");
msg = msg.replaceAll("ഒന്ന്","1");
msg = msg.replaceAll("രണ്ട്","2");
msg = msg.replaceAll("മൂന്ന്","3");
msg = msg.replaceAll("നാല്","4");
msg = msg.replaceAll("അഞ്ച്","5");
msg = msg.replaceAll("ആറ്","6");
msg = msg.replaceAll("ഏഴ്","7");
msg = msg.replaceAll("എട്ട്","8");
msg = msg.replaceAll("ഒമ്പത്","9");

return msg;
}
}
