package com.vincule.application.service;

import com.vincule.application.dto.VolunteerEventRequest;
import com.vincule.application.dto.VolunteerEventResponse;
import com.vincule.domain.entity.User;
import com.vincule.domain.entity.VolunteerEvent;
import com.vincule.domain.entity.VolunteerSchedule;
import com.vincule.domain.repository.UserRepository;
import com.vincule.domain.repository.VolunteerEventRepository;
import com.vincule.domain.repository.VolunteerScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final VolunteerEventRepository eventRepository;
    private final VolunteerScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public VolunteerEventResponse createEvent(VolunteerEventRequest request) {
        User user = getCurrentUser();

        VolunteerEvent event = VolunteerEvent.builder()
                .organization(user.getOrganization())
                .createdBy(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .maxVolunteers(request.getMaxVolunteers())
                .category(request.getCategory())
                .build();

        event = eventRepository.save(event);
        return mapToResponse(event);
    }

    public List<VolunteerEventResponse> listFutureEvents() {
        List<VolunteerEvent> events = eventRepository.findByEventDateAfter(LocalDateTime.now());
        return events.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public void confirmAttendance(UUID eventId) {
        User user = getCurrentUser();
        
        VolunteerEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("O evento já ocorreu");
        }

        if (scheduleRepository.existsByUserIdAndEventId(user.getId(), event.getId())) {
            throw new IllegalArgumentException("Você já está confirmado neste evento");
        }

        long confirmedCount = scheduleRepository.countByEventId(event.getId());
        if (confirmedCount >= event.getMaxVolunteers()) {
            throw new IllegalArgumentException("Não há mais vagas para este evento");
        }

        VolunteerSchedule schedule = VolunteerSchedule.builder()
                .user(user)
                .event(event)
                .build();

        scheduleRepository.save(schedule);
    }

    private VolunteerEventResponse mapToResponse(VolunteerEvent event) {
        long confirmedSlots = scheduleRepository.countByEventId(event.getId());
        return VolunteerEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .maxVolunteers(event.getMaxVolunteers())
                .confirmedSlots((int) confirmedSlots)
                .category(event.getCategory())
                .build();
    }
}
