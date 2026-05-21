package com.vincule.application.service;

import com.vincule.application.dto.InventoryItemRequest;
import com.vincule.application.dto.InventoryItemResponse;
import com.vincule.domain.entity.InventoryItem;
import com.vincule.domain.entity.Organization;
import com.vincule.domain.entity.User;
import com.vincule.domain.repository.InventoryItemRepository;
import com.vincule.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InventoryService inventoryService;

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
    void createItem_ShouldCreateAndReturnItem() {
        // Arrange
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Arroz");
        request.setQuantity(20);
        request.setMinimumStock(10);
        request.setUnit("kg");
        request.setCategory("Alimentos");

        InventoryItem savedItem = InventoryItem.builder()
                .id(UUID.randomUUID())
                .name("Arroz")
                .quantity(20)
                .minimumStock(10)
                .unit("kg")
                .category("Alimentos")
                .isCritical(false)
                .organization(mockOrg)
                .build();

        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(savedItem);

        // Act
        InventoryItemResponse response = inventoryService.createItem(request);

        // Assert
        assertNotNull(response);
        assertEquals("Arroz", response.getName());
        assertFalse(response.getIsCritical());
        verify(inventoryItemRepository, times(1)).save(any(InventoryItem.class));
    }

    @Test
    void updateItem_ShouldUpdateWhenAuthorized() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        InventoryItem existingItem = InventoryItem.builder()
                .id(itemId)
                .name("Arroz Antigo")
                .quantity(5)
                .minimumStock(10)
                .isCritical(true)
                .organization(mockOrg)
                .build();

        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(existingItem);

        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Arroz Novo");
        request.setQuantity(15); // Above minimum stock
        request.setMinimumStock(10);
        request.setUnit("kg");
        request.setCategory("Alimentos");

        // Act
        InventoryItemResponse response = inventoryService.updateItem(itemId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Arroz Novo", response.getName());
        assertFalse(response.getIsCritical()); // Should be updated to false
        verify(inventoryItemRepository, times(1)).save(existingItem);
    }

    @Test
    void updateItem_ShouldThrowExceptionWhenUnauthorized() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        Organization otherOrg = Organization.builder().id(UUID.randomUUID()).build();
        InventoryItem existingItem = InventoryItem.builder()
                .id(itemId)
                .organization(otherOrg)
                .build();

        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        InventoryItemRequest request = new InventoryItemRequest();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> inventoryService.updateItem(itemId, request));
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void deleteItem_ShouldDeleteWhenAuthorized() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        InventoryItem existingItem = InventoryItem.builder()
                .id(itemId)
                .organization(mockOrg)
                .build();

        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // Act
        inventoryService.deleteItem(itemId);

        // Assert
        verify(inventoryItemRepository, times(1)).delete(existingItem);
    }
}
