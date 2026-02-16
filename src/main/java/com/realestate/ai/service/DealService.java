package com.realestate.ai.service;

import com.realestate.ai.model.*;
import com.realestate.ai.repository.DealRepository;
import com.realestate.ai.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealService {

    private final DealRepository dealRepository;
    private final LeadRepository leadRepository;

    public DealService(DealRepository dealRepository,
                       LeadRepository leadRepository) {
        this.dealRepository = dealRepository;
        this.leadRepository = leadRepository;
    }

    // ================= CREATE =================

    // ✅ CREATE DEAL FROM LEAD (CRM STANDARD)
    public Deal createDealFromLead(Long leadId, Double dealValue) {

        if (dealValue == null || dealValue <= 0) {
            throw new RuntimeException("Invalid deal value");
        }

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        if (lead.getStatus() != LeadStatus.CLOSED_WON) {
            throw new RuntimeException(
                "Deal can be created only for CLOSED_WON leads"
            );
        }

        // 🚫 Prevent duplicate active deals
        boolean exists = dealRepository.existsByLeadIdAndStatusIn(
                leadId,
                List.of(
                        DealStatus.DRAFT,
                        DealStatus.PENDING_APPROVAL,
                        DealStatus.APPROVED
                )
        );

        if (exists) {
            throw new RuntimeException(
                "Active deal already exists for this lead"
            );
        }

        Deal deal = new Deal();
        deal.setLeadId(lead.getId());
        deal.setProjectId(lead.getProjectId());
        deal.setUnitId(lead.getUnitId());
        deal.setDealValue(dealValue);
        deal.setStatus(DealStatus.DRAFT);

        // (optional) set creator later from SecurityContext
        // deal.setCreatedByAdminId(adminId);

        return dealRepository.save(deal);
    }

    // ================= WORKFLOW =================

    // ✅ SALES → FINANCE
    public Deal markPendingApproval(Long dealId) {
        Deal deal = getDeal(dealId);

        if (deal.getStatus() != DealStatus.DRAFT) {
            throw new RuntimeException(
                "Only DRAFT deals can be sent for approval"
            );
        }

        deal.setStatus(DealStatus.PENDING_APPROVAL);
        return dealRepository.save(deal);
    }

    // ✅ FINANCE / ADMIN
    public Deal approveDeal(Long dealId) {
        Deal deal = getDeal(dealId);

        if (deal.getStatus() != DealStatus.PENDING_APPROVAL) {
            throw new RuntimeException(
                "Deal is not pending approval"
            );
        }

        deal.setStatus(DealStatus.APPROVED);
        return dealRepository.save(deal);
    }

    // ✅ SALES / ADMIN
    public Deal cancelDeal(Long dealId) {
        Deal deal = getDeal(dealId);

        if (deal.getStatus() == DealStatus.APPROVED) {
            throw new RuntimeException(
                "Approved deals cannot be cancelled"
            );
        }

        deal.setStatus(DealStatus.CANCELLED);
        return dealRepository.save(deal);
    }

    // ================= UTIL =================

    private Deal getDeal(Long id) {
        return dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deal not found"));
    }
}
