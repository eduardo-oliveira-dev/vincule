package com.vincule.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private long totalItems;
    private long criticalItems;
    private long itemsExpiringThisWeek;
    private long totalEvents;
    private long confirmedVolunteers;
}
