package com.realestate.ai.controller;

import com.realestate.ai.dto.AiLeadRequest;
import com.realestate.ai.service.LeadService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/lead")
@CrossOrigin
public class AiLeadController {

    private final LeadService leadService;

    public AiLeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public void createAiLead(@RequestBody AiLeadRequest request) {

        // 🔥 STEP 1 - FIND TEMP LEAD (WEB USER)
        var lead = leadService.findByPhone(request.getTempPhone());

        if (lead == null) return;

        // 🔥 STEP 2 - CONVERT INTO REAL LEAD
        lead.setName(request.getName());
        lead.setPhone("+91" + request.getPhone());
        lead.setSource("WEBSITE_AI");

        leadService.save(lead);
    }
}
