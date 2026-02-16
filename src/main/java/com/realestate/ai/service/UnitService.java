package com.realestate.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.realestate.ai.model.Unit;
import com.realestate.ai.repository.UnitRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UnitService {

    private final UnitRepository repo;

    public UnitService(UnitRepository repo) {
        this.repo = repo;
    }

    public Unit create(Unit u) {
        u.setStatus("AVAILABLE");
        return repo.save(u);
    }

    public List<Unit> byProject(Long projectId) {
        return repo.findByProjectId(projectId);
    }

    public Unit updatePrice(Long id, Double price) {
        Unit u = repo.findById(id).orElseThrow();
        u.setOverridePrice(price);
        return repo.save(u);
    }

    public Unit changeStatus(Long id, String status) {
        Unit u = repo.findById(id).orElseThrow();
        u.setStatus(status);
        return repo.save(u);
    }
}
