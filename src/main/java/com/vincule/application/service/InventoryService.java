package com.vincule.application.service;

import com.vincule.application.dto.InventoryItemRequest;
import com.vincule.application.dto.InventoryItemResponse;
import com.vincule.domain.entity.InventoryItem;
import com.vincule.domain.entity.User;
import com.vincule.domain.repository.InventoryItemRepository;
import com.vincule.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public InventoryItemResponse createItem(InventoryItemRequest request) {
        User user = getCurrentUser();

        InventoryItem item = InventoryItem.builder()
                .organization(user.getOrganization())
                .name(request.getName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .expiryDate(request.getExpiryDate())
                .minimumStock(request.getMinimumStock())
                .category(request.getCategory())
                .isCritical(request.getQuantity() < request.getMinimumStock())
                .build();

        item = inventoryItemRepository.save(item);
        return mapToResponse(item);
    }

    public InventoryItemResponse updateItem(UUID id, InventoryItemRequest request) {
        User user = getCurrentUser();
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));

        if (!item.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new IllegalArgumentException("Você não tem permissão para editar este item");
        }

        item.setName(request.getName());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());
        item.setExpiryDate(request.getExpiryDate());
        item.setMinimumStock(request.getMinimumStock());
        item.setCategory(request.getCategory());
        item.setIsCritical(request.getQuantity() < request.getMinimumStock());

        item = inventoryItemRepository.save(item);
        return mapToResponse(item);
    }

    public void deleteItem(UUID id) {
        User user = getCurrentUser();
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));

        if (!item.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new IllegalArgumentException("Você não tem permissão para remover este item");
        }

        inventoryItemRepository.delete(item);
    }

    public List<InventoryItemResponse> listItems() {
        User user = getCurrentUser();
        List<InventoryItem> items = inventoryItemRepository.findByOrganizationId(user.getOrganization().getId());
        return items.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<InventoryItemResponse> listCriticalItems() {
        List<InventoryItem> items = inventoryItemRepository.findByIsCriticalTrue();
        return items.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private InventoryItemResponse mapToResponse(InventoryItem item) {
        return InventoryItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .expiryDate(item.getExpiryDate())
                .minimumStock(item.getMinimumStock())
                .category(item.getCategory())
                .isCritical(item.getIsCritical())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
