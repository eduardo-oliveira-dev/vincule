package com.vincule.application.controller;

import com.vincule.application.dto.InventoryItemResponse;
import com.vincule.application.dto.VolunteerEventResponse;
import com.vincule.application.service.EventService;
import com.vincule.application.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final InventoryService inventoryService;
    private final EventService eventService;

    @GetMapping("/inventory/critical")
    public ResponseEntity<List<InventoryItemResponse>> listCriticalItems() {
        return ResponseEntity.ok(inventoryService.listCriticalItems());
    }

    @GetMapping("/events")
    public ResponseEntity<List<VolunteerEventResponse>> listFutureEvents() {
        return ResponseEntity.ok(eventService.listFutureEvents());
    }
}
