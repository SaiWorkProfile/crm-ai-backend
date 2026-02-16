package com.realestate.ai.controller;

import com.realestate.ai.dto.CreateDealRequest;
import com.realestate.ai.model.Deal;
import com.realestate.ai.model.DealStatus;
import com.realestate.ai.repository.DealRepository;
import com.realestate.ai.service.DealService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/deals")
public class DealController {

    private final DealService dealService;
    private final DealRepository dealRepository;

    public DealController(
            DealService dealService,
            DealRepository dealRepository
    ) {
        this.dealService = dealService;
        this.dealRepository = dealRepository;
    }

    // ================= CREATE =================

    // ✅ CREATE DEAL FROM LEAD
    @PostMapping("/from-lead")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SALES_MANAGER')")
    public Deal createFromLead(@RequestBody CreateDealRequest req) {
        return dealService.createDealFromLead(
                req.getLeadId(),
                req.getDealValue()
        );
    }

    // ================= READ =================

    // ✅ LIST ALL DEALS
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SALES_MANAGER','FINANCE')")
    public List<Deal> getAllDeals() {
        return dealRepository.findAll();
    }

    // ✅ LIST DEALS BY STATUS (FINANCE / SALES)
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SALES_MANAGER','FINANCE')")
    public List<Deal> getByStatus(@PathVariable DealStatus status) {
        return dealRepository.findByStatus(status);
    }

    // ================= WORKFLOW =================

    // 🔹 SALES → FINANCE
    @PostMapping("/{id}/pending")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SALES_MANAGER')")
    public Deal markPending(@PathVariable Long id) {
        return dealService.markPendingApproval(id);
    }

    // 🔹 FINANCE / ADMIN
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','FINANCE')")
    public Deal approve(@PathVariable Long id) {
        return dealService.approveDeal(id);
    }

    // 🔹 SALES / ADMIN
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SALES_MANAGER','FINANCE')")
    public Deal cancel(@PathVariable Long id) {
        return dealService.cancelDeal(id);
    }
}
