package org.klimtsov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${notification.email.mock:true}")
    private boolean mockEmail;

    @Value("${spring.mail.from:}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail) {
        String subject = "Добро пожаловать!";
        String text = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";

        sendEmail(toEmail, subject, text);
    }

    public void sendDeletionEmail(String toEmail) {
        String subject = "Ваш аккаунт удален";
        String text = "Здравствуйте! Ваш аккаунт был удалён.";

        sendEmail(toEmail, subject, text);
    }

    private void sendEmail(String toEmail, String subject, String text) {
        if (!emailEnabled) {
            log.info("Отправка email отключена в настройках. Письмо не отправлено.");
            return;
        }

        if (mockEmail) {
            log.info("📧 МОК-отправка email:");
            log.info("  От: {}", fromEmail);
            log.info("  Кому: {}", toEmail);
            log.info("  Тема: {}", subject);
            log.info("  Текст: {}", text);
            return;
        }

        //РЕАЛЬНАЯ отправка email.
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("✅ Email успешно отправлен с {} на {}", fromEmail, toEmail);
        } catch (Exception e) {
            log.error("❌ Ошибка отправки email с {} на {}: {}", fromEmail, toEmail, e.getMessage());
        }
    }
}