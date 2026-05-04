package com.vincule.application.service;

import com.vincule.application.dto.AuthResponse;
import com.vincule.application.dto.LoginRequest;
import com.vincule.application.dto.RegisterRequest;
import com.vincule.config.security.TokenService;
import com.vincule.domain.entity.Organization;
import com.vincule.domain.entity.User;
import com.vincule.domain.entity.UserImpact;
import com.vincule.domain.repository.OrganizationRepository;
import com.vincule.domain.repository.UserImpactRepository;
import com.vincule.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserImpactRepository userImpactRepository;
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
}
