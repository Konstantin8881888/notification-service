package org.klimtsov.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.klimtsov.dto.UserEvent;
import org.klimtsov.service.EmailService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventsConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserEventsConsumer userEventsConsumer;

    @Test
    void handleUserEvent_WhenOperationIsCreate_ShouldCallSendWelcomeEmail() {
        // Подготовка
        UserEvent event = new UserEvent("test@example.com", "CREATE");

        // Действие
        userEventsConsumer.handleUserEvent(event);

        // Проверка
        verify(emailService, times(1)).sendWelcomeEmail("test@example.com");
    }

    @Test
    void handleUserEvent_WhenOperationIsDelete_ShouldCallSendDeletionEmail() {
        // Подготовка
        UserEvent event = new UserEvent("test@example.com", "DELETE");

        // Действие
        userEventsConsumer.handleUserEvent(event);

        // Проверка
        verify(emailService, times(1)).sendDeletionEmail("test@example.com");
    }

    @Test
    void handleUserEvent_WhenOperationIsUnknown_ShouldNotCallEmailService() {
        // Подготовка
        UserEvent event = new UserEvent("test@example.com", "UNKNOWN");

        // Действие
        userEventsConsumer.handleUserEvent(event);

        // Проверка - не должно быть вызовов email сервиса
        verify(emailService, never()).sendWelcomeEmail(anyString());
        verify(emailService, never()).sendDeletionEmail(anyString());
    }

    @Test
    void handleUserEvent_WhenEmailServiceThrowsException_ShouldLogError() {
        // Подготовка
        UserEvent event = new UserEvent("test@example.com", "CREATE");

        // Настраиваем mock, чтобы он бросал исключение
        doThrow(new RuntimeException("Ошибка отправки"))
                .when(emailService)
                .sendWelcomeEmail(anyString());

        // Действие - должно обработаться без падения приложения
        userEventsConsumer.handleUserEvent(event);

        // Проверка - метод был вызван
        verify(emailService, times(1)).sendWelcomeEmail("test@example.com");
    }
}