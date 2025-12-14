package org.klimtsov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.klimtsov.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/welcome")
    public String sendWelcomeEmail(@RequestParam String email) {
        log.info("Ручная отправка приветственного письма на: {}", email);
        emailService.sendWelcomeEmail(email);
        return "Приветственное письмо отправлено на: " + email;
    }

    @PostMapping("/deletion")
    public String sendDeletionEmail(@RequestParam String email) {
        log.info("Ручная отправка письма об удалении на: {}", email);
        emailService.sendDeletionEmail(email);
        return "Письмо об удалении отправлено на: " + email;
    }

    @GetMapping("/health")
    public String health() {
        return "Notification service is running on port 8081";
    }
}