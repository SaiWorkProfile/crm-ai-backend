package com.realestate.ai.controller;

import com.realestate.ai.service.WhatsAppAutomationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook/twilio")
public class TwilioWebhookController {

private final WhatsAppAutomationService automationService;

public TwilioWebhookController(
WhatsAppAutomationService automationService
){
this.automationService=automationService;
}

@PostMapping
public void receiveMessage(
@RequestParam("From") String from,
@RequestParam("Body") String body
){

System.out.println("WHATSAPP FROM: "+from);

// 🔥 IGNORE WEBSITE AI LEADS
if(from.contains("WEB_")){
System.out.println("IGNORED WEBSITE AI MESSAGE");
return;
}

automationService.processIncomingMessage(
from,
body
);
}
}
