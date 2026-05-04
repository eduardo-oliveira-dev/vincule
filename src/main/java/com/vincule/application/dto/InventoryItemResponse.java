package com.vincule.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InventoryItemResponse {
    private UUID id;
    private String name;
    private Integer quantity;
    private String unit;
    private LocalDate expiryDate;
    private Integer minimumStock;
    private String category;
    private Boolean isCritical;
    private LocalDateTime createdAt;
}
