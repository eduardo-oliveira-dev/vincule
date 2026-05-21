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
import java.util.UUID;

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

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryItemRequest request) {
        InventoryItemResponse response = inventoryService.updateItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
