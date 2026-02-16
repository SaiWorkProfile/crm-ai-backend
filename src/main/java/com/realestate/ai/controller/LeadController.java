package com.realestate.ai.controller;

import com.realestate.ai.model.Lead;
import com.realestate.ai.model.LeadConversation;
import com.realestate.ai.model.LeadStatus;
import com.realestate.ai.repository.LeadConversationRepository;
import com.realestate.ai.service.LeadService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/leads")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SALES_MANAGER','SUPPORT')")
public class LeadController {

    private final LeadService leadService;
    private final LeadConversationRepository leadConversationRepository;

    public LeadController(
            LeadService leadService,
            LeadConversationRepository leadConversationRepository
    ) {
        this.leadService = leadService;
        this.leadConversationRepository = leadConversationRepository;
    }

    // ================= VIEW =================

    // 🔹 View all leads
    @GetMapping
    public List<Lead> getAllLeads() {
        return leadService.getAllLeads();
    }

    // 🔹 View single lead
    @GetMapping("/{id}")
    public Lead getLead(@PathVariable Long id) {
        return leadService.getLeadById(id);
    }

    // 🔹 View lead conversations (CRM timeline)
    @GetMapping("/{id}/conversations")
    public List<LeadConversation> getConversations(@PathVariable Long id) {
        return leadConversationRepository.findByLead_Id(id);
    }

    // ================= CREATE =================

    // 🔹 Create lead manually (Admin only)
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public Lead createLead(@RequestBody Lead lead) {
        return leadService.createLead(lead);
    }

    // ================= ASSIGN =================

    @PostMapping("/{id}/assign/{adminId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SALES_MANAGER')")
    public Lead assignLead(
            @PathVariable Long id,
            @PathVariable Long adminId
    ) {
        return leadService.assignLead(id, adminId);
    }

    // ================= STATUS FLOW =================

    @PostMapping("/{id}/status/{status}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SALES_MANAGER')")
    public Lead updateStatus(
            @PathVariable Long id,
            @PathVariable LeadStatus status
    ) {
        return leadService.updateStatus(id, status);
    }

    // ================= DASHBOARD =================

    @GetMapping("/stats/total")
    public long totalLeads() {
        return leadService.totalLeads();
    }

    @GetMapping("/stats/today")
    public long leadsToday() {
        return leadService.leadsToday();
    }
}
