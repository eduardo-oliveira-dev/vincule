package com.vincule.domain.repository;

import com.vincule.domain.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    List<InventoryItem> findByOrganizationId(UUID organizationId);
    List<InventoryItem> findByIsCriticalTrue();

    @Query("SELECT i FROM InventoryItem i WHERE i.quantity < i.minimumStock AND i.isCritical = false")
    List<InventoryItem> findItemsNeedingAlert();
}
