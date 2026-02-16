package com.realestate.ai.service;

import com.realestate.ai.dto.DashboardStatsDto;
import com.realestate.ai.model.DealStatus;
import com.realestate.ai.repository.DealRepository;
import com.realestate.ai.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;

    public DashboardService(LeadRepository leadRepository,
                            DealRepository dealRepository) {
        this.leadRepository = leadRepository;
        this.dealRepository = dealRepository;
    }

    public DashboardStatsDto getDashboardStats() {

        // ✅ Total leads (built-in, safest)
        long totalLeads = leadRepository.count();

        // ✅ Leads created today (DB-safe)
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        long leadsToday =
                leadRepository.countByCreatedAtBetween(startOfDay, endOfDay);

        // ✅ Deal metrics
        long activeDeals =
                dealRepository.countByStatus(DealStatus.APPROVED);

        long pendingApprovals =
                dealRepository.countByStatus(DealStatus.PENDING_APPROVAL);


        return new DashboardStatsDto(
                totalLeads,
                leadsToday,
                activeDeals,
                pendingApprovals
        );
    }
}
