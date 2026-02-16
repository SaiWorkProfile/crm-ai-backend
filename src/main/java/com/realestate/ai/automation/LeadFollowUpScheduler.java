package com.realestate.ai.automation;

import com.realestate.ai.model.Lead;
import com.realestate.ai.service.LeadService;
import com.realestate.ai.service.TwilioWhatsAppService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeadFollowUpScheduler {

    private final LeadService leadService;
    private final TwilioWhatsAppService twilio;

    public LeadFollowUpScheduler(
            LeadService leadService,
            TwilioWhatsAppService twilio
    ) {
        this.leadService = leadService;
        this.twilio = twilio;
    }

    //@Scheduled(fixedRate = 300000) // every 5 mins
    public void followUpInactiveLeads() {

        List<Lead> inactive =
                leadService.findInactiveLeads(6);

        for (Lead lead : inactive) {

            // 🔥 SANDBOX FIX
            if (!"WHATSAPP".equals(lead.getSource())) {
                System.out.println("❌ Skipping non-whatsapp lead: "
                        + lead.getId());
                continue;
            }

            String phone = lead.getPhone();

            if (phone == null || !phone.startsWith("+91")) {
                System.out.println("❌ Invalid phone skipped: "
                        + phone);
                continue;
            }

            try {

                twilio.sendMessage(
                        phone,
                        "Hi 👋 Still looking for a property?"
                );

            } catch (Exception e) {

                System.out.println("⚠️ Twilio send failed for: "
                        + phone);
            }
        }
    }
}
