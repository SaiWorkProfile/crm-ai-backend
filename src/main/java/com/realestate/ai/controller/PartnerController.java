package com.realestate.ai.controller;

import com.realestate.ai.model.Partner;
import com.realestate.ai.repository.PartnerRepository;
import com.realestate.ai.service.PartnerService;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

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
    public Partner onboard(@RequestBody Partner p){

        System.out.println("🔥 CONTROLLER HIT: POST /partners");
        System.out.println("AUTH: " + SecurityContextHolder.getContext().getAuthentication());

        return service.onboard(p);
    }

    // ================= VERIFY KYC =================
    @PutMapping("/{id}/verify-kyc")
    public Partner verifyKyc(@PathVariable Long id){

        System.out.println("🔥 CONTROLLER HIT: VERIFY KYC");
        System.out.println("AUTH: " + SecurityContextHolder.getContext().getAuthentication());

        return service.verifyKyc(id);
    }

    // ================= APPROVE =================
    @PutMapping("/{id}/approve")
    public Partner approve(@PathVariable Long id){

        System.out.println("🔥 CONTROLLER HIT: APPROVE");
        System.out.println("AUTH: " + SecurityContextHolder.getContext().getAuthentication());

        return service.approve(id);
    }

    // ================= SUSPEND =================
    @PutMapping("/{id}/suspend")
    public Partner suspend(@PathVariable Long id){

        System.out.println("🔥 CONTROLLER HIT: SUSPEND");
        System.out.println("AUTH: " + SecurityContextHolder.getContext().getAuthentication());

        return service.suspend(id);
    }

    // ================= SOFT DELETE =================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){

        System.out.println("🔥 CONTROLLER HIT: DELETE");
        System.out.println("AUTH: " + SecurityContextHolder.getContext().getAuthentication());

        service.delete(id);
    }

    // ================= GET ALL PARTNERS =================
    @GetMapping
    public List<Partner> all(){

        System.out.println("🔥 CONTROLLER HIT: GET ALL");
        System.out.println("AUTH: " + SecurityContextHolder.getContext().getAuthentication());

        return repo.findByDeletedFalse();
    }
}