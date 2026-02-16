package com.realestate.ai.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.ai.model.Unit;
import com.realestate.ai.service.UnitService;

@RestController
@RequestMapping("/api/admin/units")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class UnitController {

    private final UnitService service;

    public UnitController(UnitService service) {
        this.service = service;
    }

    @PostMapping
    public Unit create(@RequestBody Unit u) {
        return service.create(u);
    }

    @GetMapping("/project/{projectId}")
    public List<Unit> byProject(@PathVariable Long projectId) {
        return service.byProject(projectId);
    }

    @PutMapping("/{id}/price")
    public Unit updatePrice(@PathVariable Long id,
                            @RequestParam Double price) {
        return service.updatePrice(id, price);
    }

    @PutMapping("/{id}/status")
    public Unit status(@PathVariable Long id,
                       @RequestParam String status) {
        return service.changeStatus(id, status);
    }
}
