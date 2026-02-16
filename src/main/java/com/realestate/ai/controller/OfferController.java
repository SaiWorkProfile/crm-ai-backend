package com.realestate.ai.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.ai.model.Offer;
import com.realestate.ai.repository.OfferRepository;

import jakarta.transaction.Transactional;
@Transactional
@RestController
@RequestMapping("/api/admin/offers")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class OfferController {

    private final OfferRepository repo;

    public OfferController(OfferRepository repo) {
        this.repo = repo;
    }

    // Create offer
    @PostMapping
    public Offer create(@RequestBody Offer o) {
        return repo.save(o);
    }

    // View all offers
    @GetMapping
    public List<Offer> all() {
        return repo.findAll();
    }

    // Disable offer
    @PutMapping("/{id}/disable")
    public void disable(@PathVariable Long id) {
        Offer o = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        o.setActive(false);
        repo.save(o);
    }
}
