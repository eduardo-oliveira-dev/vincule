package com.vincule.application.dto;

import com.vincule.domain.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String password;

    @NotNull(message = "O papel (role) do usuário é obrigatório")
    private UserRole role;

    @NotBlank(message = "O nome da organização é obrigatório")
    private String organizationName;
}
