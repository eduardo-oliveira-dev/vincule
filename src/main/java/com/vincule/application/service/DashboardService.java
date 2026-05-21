package com.vincule.application.service;

import com.vincule.application.dto.DashboardResponse;
import com.vincule.domain.entity.InventoryItem;
import com.vincule.domain.entity.User;
import com.vincule.domain.entity.VolunteerEvent;
import com.vincule.domain.repository.InventoryItemRepository;
import com.vincule.domain.repository.UserRepository;
import com.vincule.domain.repository.VolunteerEventRepository;
import com.vincule.domain.repository.VolunteerScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InventoryItemRepository inventoryItemRepository;
    private final VolunteerEventRepository eventRepository;
    private final VolunteerScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public DashboardResponse getDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        UUID orgId = user.getOrganization().getId();

        // Inventário da organização
        List<InventoryItem> items = inventoryItemRepository.findByOrganizationId(orgId);
        long criticalCount = items.stream().filter(InventoryItem::getIsCritical).count();

        LocalDate today = LocalDate.now();
        LocalDate weekLater = today.plusDays(7);
        long expiringCount = items.stream()
                .filter(i -> i.getExpiryDate() != null
                        && !i.getExpiryDate().isBefore(today)
                        && !i.getExpiryDate().isAfter(weekLater))
                .count();

        // Eventos futuros da organização
        List<VolunteerEvent> futureEvents = eventRepository.findByOrganizationId(orgId).stream()
                .filter(e -> e.getEventDate().isAfter(LocalDateTime.now()))
                .toList();

        long totalConfirmed = futureEvents.stream()
                .mapToLong(e -> scheduleRepository.countByEventId(e.getId()))
                .sum();

        return DashboardResponse.builder()
                .totalItems(items.size())
                .criticalItems(criticalCount)
                .itemsExpiringThisWeek(expiringCount)
                .totalEvents(futureEvents.size())
                .confirmedVolunteers(totalConfirmed)
                .build();
    }
}
