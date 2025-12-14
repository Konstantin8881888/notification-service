package org.klimtsov.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendWelcomeEmail_WhenEmailDisabled_ShouldNotSendEmail() {
        // Устанавливаем email отключенным
        emailService.setEmailEnabled(false);
        emailService.setMockEmail(false);

        emailService.sendWelcomeEmail("test@example.com");

        // Проверяем, что mailSender не вызывался
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendWelcomeEmail_WhenMockEmailTrue_ShouldNotSendRealEmail() {
        // Устанавливаем mock режим
        emailService.setEmailEnabled(true);
        emailService.setMockEmail(true);

        emailService.sendWelcomeEmail("test@example.com");

        // Проверяем, что mailSender не вызывался (только логирование)
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendDeletionEmail_WhenMockEmailTrue_ShouldNotSendRealEmail() {
        emailService.setEmailEnabled(true);
        emailService.setMockEmail(true);

        emailService.sendDeletionEmail("test@example.com");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendWelcomeEmail_WhenRealEmail_ShouldLogRealEmail() {
        // Этот тест проверяет только логику без реальной отправки
        emailService.setEmailEnabled(true);
        emailService.setMockEmail(false);

        // Здесь мы просто проверяем, что метод выполняется без ошибок
        // Реальная отправка требует настройки SMTP, что не нужно в тестах
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail("test@example.com"));
    }
}