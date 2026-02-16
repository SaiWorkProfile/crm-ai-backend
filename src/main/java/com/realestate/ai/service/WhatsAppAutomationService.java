package com.realestate.ai.service;

import com.realestate.ai.dto.AiChatMessage;
import com.realestate.ai.dto.Language;
import com.realestate.ai.model.Lead;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhatsAppAutomationService {

private final LeadService leadService;
private final AiChatService aiChatService;
private final NotificationService notificationService;
private final TwilioWhatsAppService twilio;
private final VoiceSessionService session;

public WhatsAppAutomationService(
LeadService leadService,
AiChatService aiChatService,
NotificationService notificationService,
TwilioWhatsAppService twilio,
VoiceSessionService session
){
this.leadService=leadService;
this.aiChatService=aiChatService;
this.notificationService=notificationService;
this.twilio=twilio;
this.session=session;
}

public void processIncomingMessage(
String from,
String body
){

try{

String phone =
from.replace("whatsapp:","");

String sessionId = phone;

// 🔥 DETECT PHONE FROM USER MESSAGE
String digits =
body.replaceAll("\\D","");

if(digits.length()==10){

session.set(sessionId,"phone",digits);

twilio.sendMessage(
from,
"Thanks. May I know your name?"
);
return;
}

// 🔥 DETECT NAME
if(body.matches("^[a-zA-Z ]{3,}$")){

session.set(sessionId,"name",body);

twilio.sendMessage(
from,
"Thank you. Please share your 10 digit phone number."
);
return;
}

// 🔥 CHECK BOTH
String name =
session.getString(sessionId,"name");

String ph =
session.getString(sessionId,"phone");

if(name!=null && ph!=null){

Lead lead =
leadService.createVoiceAiLead(
name,
ph,
null,
null
);

leadService.saveConversation(
lead,
body,
"Lead created from WhatsApp",
"WHATSAPP_AI"
);

notificationService.notifyNewLead(ph);

session.clear(sessionId);

twilio.sendMessage(
from,
"Thank you "+name+
". Our team will contact you shortly."
);
return;
}

// 🔥 NORMAL AI CHAT
String aiReply =
aiChatService.chat(
List.of(
new AiChatMessage("user",body)
),
Language.ENGLISH
);

twilio.sendMessage(from,aiReply);

}catch(Exception e){
e.printStackTrace();
}
}
}
