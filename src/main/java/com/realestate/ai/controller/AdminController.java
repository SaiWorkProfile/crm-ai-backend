package com.realestate.ai.controller;

import com.realestate.ai.dto.DashboardStatsDto;
import com.realestate.ai.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // 🔐 ADMIN + SUPER_ADMIN (already enforced by SecurityConfig)
    @GetMapping("/dashboard")
    public DashboardStatsDto dashboard() {
        return dashboardService.getDashboardStats();
    }

    // 🔐 SUPER_ADMIN only (already enforced by SecurityConfig)
    @GetMapping("/super")
    public String superAdminOnly() {
        return "Hello SUPER ADMIN";
    }
}
