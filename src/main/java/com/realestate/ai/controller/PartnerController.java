package com.realestate.ai.controller;

import com.realestate.ai.model.Partner;
import com.realestate.ai.repository.PartnerRepository;
import com.realestate.ai.service.PartnerService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin/partners")
@PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
public class PartnerController {
    private final PartnerService service;
    private final PartnerRepository repo;

    public PartnerController(
            PartnerService service,
            PartnerRepository repo){

        this.service = service;
        this.repo = repo;
    }

    // ================= ONBOARD PARTNER =================
    @PostMapping
    public Partner onboard(
            @RequestBody Partner p){

        return service.onboard(p);
    }

    // ================= VERIFY KYC =================
    @PutMapping("/{id}/verify-kyc")
    public Partner verifyKyc(
            @PathVariable Long id){

        return service.verifyKyc(id);
    }

    // ================= APPROVE =================
    @PutMapping("/{id}/approve")
    public Partner approve(
            @PathVariable Long id){

        return service.approve(id);
    }

    // ================= SUSPEND =================
    @PutMapping("/{id}/suspend")
    public Partner suspend(
            @PathVariable Long id){

        return service.suspend(id);
    }

    // ================= SOFT DELETE =================
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id){

        service.delete(id);
    }

    // ================= GET ALL PARTNERS =================
    @GetMapping
    public List<Partner> all(){

        return repo.findByDeletedFalse();
    }
}
