package com.realestate.ai.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioWhatsAppService {

    @Value("${twilio.whatsapp-number}")
    private String twilioNumber;

    public void sendMessage(String to, String message) {

        try {

            if (to == null || to.isBlank()) return;

            // 🚨 SANDBOX RULE
            // Reply EXACTLY to incoming sender
            if (!to.startsWith("whatsapp:")) {
                to = "whatsapp:" + to;
            }

            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioNumber),
                    message
            ).create();

            System.out.println("✅ Twilio reply sent to: " + to);

        } catch (Exception e) {
            System.out.println("❌ Twilio ERROR sending to: " + to);
            e.printStackTrace();
        }
    }
}
