package com.vincule.application.service;

import com.vincule.domain.entity.InventoryItem;
import com.vincule.domain.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final InventoryItemRepository inventoryItemRepository;

    @Scheduled(cron = "0 0 * * * *") // Executa no minuto 0 de cada hora
    @Transactional
    public void checkCriticalStock() {
        log.info("Iniciando verificação de estoque crítico...");

        List<InventoryItem> itemsNeedingAlert = inventoryItemRepository.findItemsNeedingAlert();

        if (itemsNeedingAlert.isEmpty()) {
            log.info("Nenhum item em estoque crítico encontrado.");
            return;
        }

        for (InventoryItem item : itemsNeedingAlert) {
            item.setIsCritical(true);
            inventoryItemRepository.save(item);
            
            // Simulação de envio de e-mail ou push notification
            log.warn("ALERTA: O item '{}' (ID: {}) atingiu o estoque crítico! Quantidade atual: {}, Mínimo: {}",
                    item.getName(), item.getId(), item.getQuantity(), item.getMinimumStock());
            
            sendPushNotification(item);
        }

        log.info("Verificação de estoque crítico finalizada. {} itens atualizados.", itemsNeedingAlert.size());
    }

    private void sendPushNotification(InventoryItem item) {
        // Integração fake com o Firebase Cloud Messaging (FCM) / SendGrid
        log.info("[FCM] Simulando envio de push notification para os administradores da org: {}", item.getOrganization().getName());
    }
}
