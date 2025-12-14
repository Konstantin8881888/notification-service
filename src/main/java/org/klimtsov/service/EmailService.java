package org.klimtsov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${notification.email.mock:true}")
    private boolean mockEmail;

    public void sendWelcomeEmail(String email) {
        String subject = "Добро пожаловать!";
        String text = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";

        sendEmail(email, subject, text);
    }

    public void sendDeletionEmail(String email) {
        String subject = "Ваш аккаунт удален";
        String text = "Здравствуйте! Ваш аккаунт был удалён.";

        sendEmail(email, subject, text);
    }

    private void sendEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            log.info("Отправка email отключена. Письмо не отправлено.");
            return;
        }

        //Логируем вместо реальной отправки.
        log.info("📧 МОК-отправка email:");
        log.info("  Кому: {}", to);
        log.info("  Тема: {}", subject);
        log.info("  Текст: {}", text);
    }
}