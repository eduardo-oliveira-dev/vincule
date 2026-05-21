package com.vincule.application.service;

import com.vincule.domain.entity.InventoryItem;
import com.vincule.domain.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final InventoryItemRepository inventoryItemRepository;

    @Scheduled(cron = "0 0 * * * *") // Executa no minuto 0 de cada hora
    @Transactional
    public void checkCriticalStock() {
        log.info("Iniciando verificação de estoque crítico e validade...");

        // RF02 — Estoque abaixo do mínimo
        List<InventoryItem> itemsNeedingAlert = inventoryItemRepository.findItemsNeedingAlert();
        if (!itemsNeedingAlert.isEmpty()) {
            for (InventoryItem item : itemsNeedingAlert) {
                item.setIsCritical(true);
                inventoryItemRepository.save(item);
                log.warn("ALERTA ESTOQUE: '{}' (ID: {}) atingiu estoque crítico! Atual: {}, Mínimo: {}",
                        item.getName(), item.getId(), item.getQuantity(), item.getMinimumStock());
                sendPushNotification(item);
            }
            log.info("Verificação de estoque concluída. {} item(ns) marcado(s) como crítico(s).", itemsNeedingAlert.size());
        } else {
            log.info("Nenhum item em estoque crítico encontrado.");
        }

        // RF08 — Validade próxima (7 dias)
        checkExpiringItems();
    }

    private void checkExpiringItems() {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(7);

        List<InventoryItem> expiringItems = inventoryItemRepository.findItemsExpiringWithinDays(today, warningDate);
        if (expiringItems.isEmpty()) {
            log.info("Nenhum item próximo da validade encontrado.");
            return;
        }

        for (InventoryItem item : expiringItems) {
            log.warn("ALERTA VALIDADE: '{}' (Org: {}) expira em {}. Estoque atual: {} {}.",
                    item.getName(),
                    item.getOrganization().getName(),
                    item.getExpiryDate(),
                    item.getQuantity(),
                    item.getUnit());
            // Simulação de notificação de validade
            log.info("[EMAIL SIMULADO] Alerta de validade enviado para administradores da org '{}'.",
                    item.getOrganization().getName());
        }
        log.info("Verificação de validade concluída. {} item(ns) próximo(s) do vencimento.", expiringItems.size());
    }

    private void sendPushNotification(InventoryItem item) {
        // Integração simulada com Firebase Cloud Messaging (FCM)
        log.info("[FCM] Simulando push notification para administradores da org: '{}'.",
                item.getOrganization().getName());
    }
}
