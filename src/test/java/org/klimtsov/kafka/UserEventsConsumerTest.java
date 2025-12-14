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
        UserEvent event = new UserEvent("test@example.com", "CREATE");

        userEventsConsumer.handleUserEvent(event);

        verify(emailService, times(1)).sendWelcomeEmail("test@example.com");
    }

    @Test
    void handleUserEvent_WhenOperationIsDelete_ShouldCallSendDeletionEmail() {
        UserEvent event = new UserEvent("test@example.com", "DELETE");

        userEventsConsumer.handleUserEvent(event);

        verify(emailService, times(1)).sendDeletionEmail("test@example.com");
    }

    @Test
    void handleUserEvent_WhenOperationIsUnknown_ShouldNotCallEmailService() {
        UserEvent event = new UserEvent("test@example.com", "UNKNOWN");

        userEventsConsumer.handleUserEvent(event);

        verify(emailService, never()).sendWelcomeEmail(anyString());
        verify(emailService, never()).sendDeletionEmail(anyString());
    }

    @Test
    void handleUserEvent_WhenEmailServiceThrowsException_ShouldLogError() {
        UserEvent event = new UserEvent("test@example.com", "CREATE");

        doThrow(new RuntimeException("Ошибка отправки"))
                .when(emailService)
                .sendWelcomeEmail(anyString());

        userEventsConsumer.handleUserEvent(event);

        verify(emailService, times(1)).sendWelcomeEmail("test@example.com");
    }
}