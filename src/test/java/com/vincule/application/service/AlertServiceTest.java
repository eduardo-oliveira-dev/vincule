package com.vincule.application.service;

import com.vincule.domain.entity.InventoryItem;
import com.vincule.domain.entity.Organization;
import com.vincule.domain.repository.InventoryItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void checkCriticalStock_ShouldUpdateCriticalItemsAndCheckExpiry() {
        // Arrange
        Organization org = Organization.builder().name("Test Org").build();
        InventoryItem criticalItem = InventoryItem.builder()
                .id(UUID.randomUUID())
                .name("Arroz")
                .quantity(5)
                .minimumStock(10)
                .isCritical(false)
                .organization(org)
                .build();

        InventoryItem expiringItem = InventoryItem.builder()
                .id(UUID.randomUUID())
                .name("Feijão")
                .expiryDate(LocalDate.now().plusDays(3))
                .quantity(20)
                .minimumStock(10)
                .organization(org)
                .build();

        when(inventoryItemRepository.findItemsNeedingAlert()).thenReturn(List.of(criticalItem));
        when(inventoryItemRepository.findItemsExpiringWithinDays(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(expiringItem));

        // Act
        alertService.checkCriticalStock();

        // Assert
        verify(inventoryItemRepository, times(1)).save(criticalItem);
        assert (criticalItem.getIsCritical());
    }

    @Test
    void checkCriticalStock_WhenNoItems_ShouldDoNothing() {
        // Arrange
        when(inventoryItemRepository.findItemsNeedingAlert()).thenReturn(List.of());
        when(inventoryItemRepository.findItemsExpiringWithinDays(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        // Act
        alertService.checkCriticalStock();

        // Assert
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }
}
