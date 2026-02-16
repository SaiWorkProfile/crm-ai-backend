package com.realestate.ai.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final TwilioWhatsAppService twilio;

    public NotificationService(TwilioWhatsAppService twilio) {
        this.twilio = twilio;
    }

    public void notifyNewLead(String phone) {

        twilio.sendMessage(
            "+917095090938",
            "📥 New WhatsApp Lead\n\nPhone: " + phone
        );
    }

    public void notifyStrongLead(String phone, String msg) {

        twilio.sendMessage(
            "+917095090938",
            "🔥 Strong Lead Detected\n\n"
            + "Phone: " + phone
            + "\nMsg: " + msg
        );
    }
}
