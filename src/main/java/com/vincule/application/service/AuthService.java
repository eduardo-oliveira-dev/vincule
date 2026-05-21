package com.vincule.application.service;

import com.vincule.application.dto.AuthResponse;
import com.vincule.application.dto.ForgotPasswordRequest;
import com.vincule.application.dto.LoginRequest;
import com.vincule.application.dto.RegisterRequest;
import com.vincule.application.dto.ResetPasswordRequest;
import com.vincule.config.security.TokenService;
import com.vincule.domain.entity.Organization;
import com.vincule.domain.entity.PasswordResetToken;
import com.vincule.domain.entity.User;
import com.vincule.domain.entity.UserImpact;
import com.vincule.domain.repository.OrganizationRepository;
import com.vincule.domain.repository.PasswordResetTokenRepository;
import com.vincule.domain.repository.UserImpactRepository;
import com.vincule.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserImpactRepository userImpactRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = tokenService.generateToken(authentication);
        User user = (User) authentication.getPrincipal();

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        Organization org = Organization.builder()
                .name(request.getOrganizationName())
                .build();
        org = organizationRepository.save(org);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .organization(org)
                .build();
        user = userRepository.save(user);

        UserImpact impact = UserImpact.builder()
                .user(user)
                .build();
        userImpactRepository.save(impact);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        String token = tokenService.generateToken(authentication);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // RF10 — Recuperação de senha (simulada via log)
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("E-mail não encontrado no sistema."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        passwordResetTokenRepository.save(resetToken);

        // Simulação de envio de e-mail (substituir por integração SMTP/SendGrid em produção)
        log.info("[EMAIL SIMULADO] Reset de senha para: {}. Link: http://localhost:5173/reset-password?token={}",
                request.getEmail(), token);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado."));

        if (resetToken.getUsed()) {
            throw new IllegalArgumentException("Este token já foi utilizado.");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado. Solicite um novo link de recuperação.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("[SEGURANÇA] Senha alterada com sucesso para o usuário: {}", user.getEmail());
    }
}
