package com.vincule.application.service;

import com.vincule.application.dto.UserProfileResponse;
import com.vincule.domain.entity.User;
import com.vincule.domain.entity.UserImpact;
import com.vincule.domain.repository.UserImpactRepository;
import com.vincule.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserImpactRepository userImpactRepository;

    public UserProfileResponse getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        UserImpact impact = userImpactRepository.findByUserId(user.getId())
                .orElse(UserImpact.builder().totalDonations(0).totalHours(0).build());

        return UserProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .organizationName(user.getOrganization().getName())
                .totalDonations(impact.getTotalDonations())
                .totalHours(impact.getTotalHours())
                .lastActivity(impact.getLastActivity())
                .build();
    }
}
