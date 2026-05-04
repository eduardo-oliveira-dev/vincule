package com.vincule.application.controller;

import com.vincule.application.dto.InventoryItemRequest;
import com.vincule.application.dto.InventoryItemResponse;
import com.vincule.application.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryItemResponse> createItem(@Valid @RequestBody InventoryItemRequest request) {
        InventoryItemResponse response = inventoryService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> listItems() {
        List<InventoryItemResponse> items = inventoryService.listItems();
        return ResponseEntity.ok(items);
    }
}
