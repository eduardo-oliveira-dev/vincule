package com.vincule.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryItemRequest {

    @NotBlank(message = "O nome do item é obrigatório")
    private String name;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantity;

    @NotBlank(message = "A unidade é obrigatória")
    private String unit;

    private LocalDate expiryDate;

    @NotNull(message = "O estoque mínimo é obrigatório")
    @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
    private Integer minimumStock;

    @NotBlank(message = "A categoria é obrigatória")
    private String category;
}
