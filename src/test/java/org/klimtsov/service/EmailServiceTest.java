package org.klimtsov.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

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
        //Устанавливаем email отключенным через ReflectionTestUtils.
        ReflectionTestUtils.setField(emailService, "emailEnabled", false);
        ReflectionTestUtils.setField(emailService, "mockEmail", false);

        emailService.sendWelcomeEmail("test@example.com");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendWelcomeEmail_WhenMockEmailTrue_ShouldNotSendRealEmail() {
        //Устанавливаем mock режим через ReflectionTestUtils.
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        ReflectionTestUtils.setField(emailService, "mockEmail", true);

        emailService.sendWelcomeEmail("test@example.com");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendDeletionEmail_WhenMockEmailTrue_ShouldNotSendRealEmail() {
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        ReflectionTestUtils.setField(emailService, "mockEmail", true);

        emailService.sendDeletionEmail("test@example.com");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendWelcomeEmail_WhenRealEmail_ShouldCallMailSender() {
        //Для реальной отправки.
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
        ReflectionTestUtils.setField(emailService, "mockEmail", false);
        //Устанавливаем fromEmail, чтобы не было null.
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");

        emailService.sendWelcomeEmail("recipient@example.com");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}