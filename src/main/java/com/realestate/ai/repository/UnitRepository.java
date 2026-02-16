package com.realestate.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.ai.model.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByProjectId(Long projectId);
}
