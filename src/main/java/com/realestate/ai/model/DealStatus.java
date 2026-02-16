package com.realestate.ai.model;

public enum DealStatus {
    DRAFT,              // Created by Sales
    PENDING_APPROVAL,   // Sent to Finance/Admin
    APPROVED,           // Finance/Admin approved
    CANCELLED           // Deal dropped
}
