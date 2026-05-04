package com.vincule.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VolunteerEventResponse {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private Integer maxVolunteers;
    private Integer confirmedSlots;
    private String category;
}
