package com.realestate.ai.service;

import com.realestate.ai.model.Lead;
import com.realestate.ai.model.LeadConversation;
import com.realestate.ai.model.LeadStatus;
import com.realestate.ai.repository.LeadConversationRepository;
import com.realestate.ai.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadConversationRepository leadConversationRepository;

    public LeadService(
            LeadRepository leadRepository,
            LeadConversationRepository leadConversationRepository
    ) {
        this.leadRepository = leadRepository;
        this.leadConversationRepository = leadConversationRepository;
    }

    // ================= CREATE =================

    public Lead createLead(Lead lead) {

        if (lead.getSource() == null) {
            throw new RuntimeException("Lead source is required");
        }

        if (lead.getStatus() == null) {
            lead.setStatus(LeadStatus.NEW);
        }

        return leadRepository.save(lead);
    }
    public Lead findOrCreateAiLead(String tempPhone) {

        return leadRepository.findByPhone(tempPhone)
            .orElseGet(() -> {

                Lead lead = new Lead();

                lead.setName("Website AI User");
                lead.setPhone(tempPhone);
                lead.setStatus(LeadStatus.NEW);
                lead.setSource("AI_CHAT");   // source is STRING in your entity

                lead.setCreatedAt(LocalDateTime.now());
                lead.setUpdatedAt(LocalDateTime.now());


                return leadRepository.save(lead);
            });
    }


    // WhatsApp auto-create
    public Lead findOrCreateByPhone(String phone) {

        Optional<Lead> existing = leadRepository.findByPhone(phone);

        if (existing.isPresent()) {
            return existing.get();
        }

        Lead newLead = new Lead();
        newLead.setPhone(phone);
        newLead.setName("WhatsApp User");
        newLead.setSource("WHATSAPP");

        return leadRepository.save(newLead);
    }

    // ================= READ =================

    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    public Lead getLeadById(Long id) {
        return getLead(id);
    }

    // ================= UPDATE =================

    public Lead assignLead(Long leadId, Long adminId) {
        Lead lead = getLead(leadId);
        lead.setAssignedAdminId(adminId);
        return leadRepository.save(lead);
    }

    public Lead updateLeadDetails(Long leadId, Lead updated) {
        Lead lead = getLead(leadId);

        lead.setName(updated.getName());
        lead.setPhone(updated.getPhone());
        lead.setEmail(updated.getEmail());
        lead.setProjectId(updated.getProjectId());
        lead.setUnitId(updated.getUnitId());

        return leadRepository.save(lead);
    }

    public Lead updateStatus(Long leadId, LeadStatus newStatus) {
        Lead lead = getLead(leadId);

        if (!isValidTransition(lead.getStatus(), newStatus)) {
            throw new RuntimeException(
                    "Invalid transition: " + lead.getStatus() + " → " + newStatus
            );
        }

        lead.setStatus(newStatus);
        return leadRepository.save(lead);
    }

    // ================= DASHBOARD =================

    public long totalLeads() {
        return leadRepository.count();
    }

    public long leadsToday() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        return leadRepository.countByCreatedAtBetween(start, end);
    }

    // ================= CONVERSATION =================

    public void saveConversation(
            Lead lead,
            String userMessage,
            String aiReply,
            String source
    ) {

        LeadConversation convo = new LeadConversation();

        convo.setLead(lead);
        convo.setUserMessage(userMessage);
        convo.setAiReply(aiReply);
        convo.setSource(source);     // 🔥 VERY IMPORTANT

        leadConversationRepository.save(convo);
    }


    // ================= AUTOMATION =================

    public List<Lead> findInactiveLeads(int hours) {
        LocalDateTime threshold =
                LocalDateTime.now().minusHours(hours);

        return leadRepository.findByUpdatedAtBefore(threshold);
    }

    // ================= INTERNAL =================

    private Lead getLead(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
    }

    private boolean isValidTransition(LeadStatus current, LeadStatus next) {

        if (next == LeadStatus.CLOSED_LOST) return true;

        Map<LeadStatus, LeadStatus[]> rules = new EnumMap<>(LeadStatus.class);

        rules.put(LeadStatus.NEW, new LeadStatus[]{LeadStatus.CONTACTED});
        rules.put(LeadStatus.CONTACTED, new LeadStatus[]{LeadStatus.QUALIFIED});
        rules.put(LeadStatus.QUALIFIED, new LeadStatus[]{LeadStatus.SITE_VISIT});
        rules.put(LeadStatus.SITE_VISIT, new LeadStatus[]{LeadStatus.NEGOTIATION});
        rules.put(LeadStatus.NEGOTIATION, new LeadStatus[]{LeadStatus.BOOKING});
        rules.put(LeadStatus.BOOKING, new LeadStatus[]{LeadStatus.CLOSED_WON});

        if (!rules.containsKey(current)) return false;

        for (LeadStatus allowed : rules.get(current)) {
            if (allowed == next) return true;
        }
        return false;
    }
 // ================= FIND BY PHONE =================

    public Lead findByPhone(String phone) {

        if(phone == null) return null;

        return leadRepository
                .findByPhone(phone)
                .orElse(null);
    }


    // ================= SAVE LEAD =================

    public Lead save(Lead lead) {
        return leadRepository.save(lead);
    }

 // ================= AI LEAD CREATION =================

    public Lead createLeadFromAi(
            String name,
            String phone,
            String requirement,
            Long projectId,
            Long unitId
    ) {

        Lead lead = new Lead();

        lead.setName(name);
        lead.setPhone(phone);
        lead.setEmail(null);              // optional
        lead.setSource("AI_CHAT");        // REQUIRED (IMMUTABLE)

        lead.setProjectId(projectId);
        lead.setUnitId(unitId);

        // status + createdAt handled by @PrePersist

        return leadRepository.save(lead);
    }
 // ================= VOICE AI LEAD =================
 // ================= VOICE AI LEAD =================
    public Lead createVoiceAiLead(
    String name,
    String phone,
    Map<String,Object> requirement,
    Long projectId
    ){

    Lead lead = new Lead();

    lead.setName(name);
    lead.setPhone(phone);
    lead.setSource("VOICE_AI");
    lead.setStatus(LeadStatus.NEW);
    lead.setProjectId(projectId);

    // 🔥 SAVE AI REQUIREMENT
    String req = buildRequirementText(requirement);
    lead.setRequirement(req);

    return leadRepository.save(lead);
    }
    private String buildRequirementText(
    		Map<String,Object> r){

    		StringBuilder sb=new StringBuilder();

    		if(r.get("bhk")!=null)
    		sb.append(r.get("bhk")).append(" ");

    		if(r.get("propertyType")!=null)
    		sb.append(r.get("propertyType")).append(" ");

    		if(Boolean.TRUE.equals(
    		r.get("gatedCommunity")))
    		sb.append("Gated ");

    		if(r.get("location")!=null)
    		sb.append("in ")
    		.append(r.get("location")).append(" ");

    		if(r.get("city")!=null)
    		sb.append("at ")
    		.append(r.get("city")).append(" ");

    		if(r.get("budget")!=null)
    		sb.append("under ")
    		.append(r.get("budget")).append(" ");

    		if(r.get("status")!=null)
    		sb.append(r.get("status"));

    		return sb.toString();
    		}

}
