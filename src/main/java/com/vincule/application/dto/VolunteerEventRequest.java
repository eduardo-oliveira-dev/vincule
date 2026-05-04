package com.vincule.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VolunteerEventRequest {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "A data do evento é obrigatória")
    @Future(message = "A data do evento deve ser no futuro")
    private LocalDateTime eventDate;

    @NotNull(message = "O número máximo de voluntários é obrigatório")
    @Min(value = 1, message = "Deve haver pelo menos 1 vaga")
    private Integer maxVolunteers;

    @NotBlank(message = "A categoria é obrigatória")
    private String category;
}
