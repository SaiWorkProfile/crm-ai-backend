package com.realestate.ai.repository;

import com.realestate.ai.model.Deal;
import com.realestate.ai.model.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {

    // ================= DASHBOARD COUNTS =================
    long countByStatus(DealStatus status);

    // ================= LISTING =================
    List<Deal> findByLeadId(Long leadId);

    List<Deal> findByStatus(DealStatus status);

    // ================= SAFETY / VALIDATION =================
    boolean existsByLeadIdAndStatusIn(
            Long leadId,
            List<DealStatus> statuses
    );
}
