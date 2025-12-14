package org.klimtsov.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.klimtsov.dto.UserEvent;
import org.klimtsov.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventsConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = "user-events",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserEvent(@Payload UserEvent event, Acknowledgment acknowledgment) {
        log.info("📨 Получено событие из Kafka: {}", event);

        try {
            if ("CREATE".equals(event.getOperation())) {
                log.info("Отправляем приветственное письмо на {}", event.getEmail());
                emailService.sendWelcomeEmail(event.getEmail());
            } else if ("DELETE".equals(event.getOperation())) {
                log.info("Отправляем письмо об удалении на {}", event.getEmail());
                emailService.sendDeletionEmail(event.getEmail());
            } else {
                log.warn("Неизвестная операция: {}", event.getOperation());
            }

            acknowledgment.acknowledge();
            log.info("✅ Событие обработано успешно");

        } catch (Exception e) {
            log.error("❌ Ошибка обработки события: {}", e.getMessage());
        }
    }
}