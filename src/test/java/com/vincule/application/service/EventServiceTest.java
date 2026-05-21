package com.vincule.application.service;

import com.vincule.application.dto.VolunteerEventRequest;
import com.vincule.application.dto.VolunteerEventResponse;
import com.vincule.domain.entity.Organization;
import com.vincule.domain.entity.User;
import com.vincule.domain.entity.UserImpact;
import com.vincule.domain.entity.VolunteerEvent;
import com.vincule.domain.entity.VolunteerSchedule;
import com.vincule.domain.repository.UserImpactRepository;
import com.vincule.domain.repository.UserRepository;
import com.vincule.domain.repository.VolunteerEventRepository;
import com.vincule.domain.repository.VolunteerScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private VolunteerEventRepository eventRepository;

    @Mock
    private VolunteerScheduleRepository scheduleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserImpactRepository userImpactRepository;

    @InjectMocks
    private EventService eventService;

    private User mockUser;
    private Organization mockOrg;

    @BeforeEach
    void setUp() {
        mockOrg = Organization.builder()
                .id(UUID.randomUUID())
                .name("Test Org")
                .build();

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .organization(mockOrg)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@test.com", "password")
        );
    }

    @Test
    void createEvent_ShouldCreateAndReturnEvent() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        VolunteerEventRequest request = new VolunteerEventRequest();
        request.setTitle("Mutirão");
        request.setDescription("Limpeza");
        request.setEventDate(LocalDateTime.now().plusDays(5));
        request.setMaxVolunteers(10);
        request.setCategory("Meio Ambiente");

        VolunteerEvent savedEvent = VolunteerEvent.builder()
                .id(UUID.randomUUID())
                .title("Mutirão")
                .maxVolunteers(10)
                .organization(mockOrg)
                .build();

        when(eventRepository.save(any(VolunteerEvent.class))).thenReturn(savedEvent);
        when(scheduleRepository.countByEventId(any())).thenReturn(0L);

        // Act
        VolunteerEventResponse response = eventService.createEvent(request);

        // Assert
        assertNotNull(response);
        assertEquals("Mutirão", response.getTitle());
        verify(eventRepository, times(1)).save(any(VolunteerEvent.class));
    }

    @Test
    void confirmAttendance_ShouldConfirmAndAddImpact() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        VolunteerEvent event = VolunteerEvent.builder()
                .id(eventId)
                .eventDate(LocalDateTime.now().plusDays(2))
                .maxVolunteers(5)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(scheduleRepository.existsByUserIdAndEventId(mockUser.getId(), eventId)).thenReturn(false);
        when(scheduleRepository.countByEventId(eventId)).thenReturn(2L);

        UserImpact impact = UserImpact.builder()
                .user(mockUser)
                .totalHours(4)
                .build();
        when(userImpactRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(impact));

        // Act
        eventService.confirmAttendance(eventId);

        // Assert
        verify(scheduleRepository, times(1)).save(any(VolunteerSchedule.class));
        verify(userImpactRepository, times(1)).save(impact);
        assertEquals(6, impact.getTotalHours()); // Added 2 hours
    }

    @Test
    void confirmAttendance_ShouldThrowWhenEventPast() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        VolunteerEvent event = VolunteerEvent.builder()
                .id(eventId)
                .eventDate(LocalDateTime.now().minusDays(1)) // Past event
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventService.confirmAttendance(eventId));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void confirmAttendance_ShouldThrowWhenAlreadyConfirmed() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        VolunteerEvent event = VolunteerEvent.builder()
                .id(eventId)
                .eventDate(LocalDateTime.now().plusDays(2))
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(scheduleRepository.existsByUserIdAndEventId(mockUser.getId(), eventId)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventService.confirmAttendance(eventId));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void confirmAttendance_ShouldThrowWhenFull() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        VolunteerEvent event = VolunteerEvent.builder()
                .id(eventId)
                .eventDate(LocalDateTime.now().plusDays(2))
                .maxVolunteers(5)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(scheduleRepository.existsByUserIdAndEventId(mockUser.getId(), eventId)).thenReturn(false);
        when(scheduleRepository.countByEventId(eventId)).thenReturn(5L); // Full

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> eventService.confirmAttendance(eventId));
        verify(scheduleRepository, never()).save(any());
    }
}
