package com.vincule.application.controller;

import com.vincule.application.dto.VolunteerEventRequest;
import com.vincule.application.dto.VolunteerEventResponse;
import com.vincule.application.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<VolunteerEventResponse> createEvent(@Valid @RequestBody VolunteerEventRequest request) {
        VolunteerEventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VolunteerEventResponse>> listFutureEvents() {
        List<VolunteerEventResponse> events = eventService.listFutureEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmAttendance(@PathVariable UUID id) {
        eventService.confirmAttendance(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
