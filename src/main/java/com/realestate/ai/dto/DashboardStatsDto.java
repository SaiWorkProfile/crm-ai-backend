package com.realestate.ai.dto;

public class DashboardStatsDto {

    private long totalLeads;
    private long leadsToday;
    private long activeDeals;
    private long pendingApprovals;

    public DashboardStatsDto(long totalLeads, long leadsToday, long activeDeals, long pendingApprovals) {
        this.totalLeads = totalLeads;
        this.leadsToday = leadsToday;
        this.activeDeals = activeDeals;
        this.pendingApprovals = pendingApprovals;
    }

    public long getTotalLeads() { return totalLeads; }
    public long getLeadsToday() { return leadsToday; }
    public long getActiveDeals() { return activeDeals; }
    public long getPendingApprovals() { return pendingApprovals; }
}
