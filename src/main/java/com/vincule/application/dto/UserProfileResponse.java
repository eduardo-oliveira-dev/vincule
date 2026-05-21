package com.vincule.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private String name;
    private String email;
    private String role;
    private String organizationName;
    private Integer totalDonations;
    private Integer totalHours;
    private LocalDateTime lastActivity;
}
