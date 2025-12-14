package org.klimtsov.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.klimtsov.dto.UserEvent;
import org.klimtsov.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventsConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        log.info("📨 Получено событие из Kafka: {}", event);

        try {
            if ("CREATE".equals(event.getOperation())) {
                emailService.sendWelcomeEmail(event.getEmail());
            } else if ("DELETE".equals(event.getOperation())) {
                emailService.sendDeletionEmail(event.getEmail());
            }
        } catch (Exception e) {
            log.error("❌ Ошибка обработки события: {}", e.getMessage());
        }
    }
}