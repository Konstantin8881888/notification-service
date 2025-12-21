package org.klimtsov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.klimtsov.dto.HealthStatus;
import org.klimtsov.dto.NotificationResponse;
import org.klimtsov.service.EmailService;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Validated
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/welcome")
    public EntityModel<NotificationResponse> sendWelcomeEmail(
            @RequestParam @Email @NotBlank String email) {
        log.info("Ручная отправка приветственного письма на: {}", email);
        emailService.sendWelcomeEmail(email);

        NotificationResponse response = new NotificationResponse(
                "Приветственное письмо отправлено",
                email,
                "WELCOME"
        );

        return EntityModel.of(response,
                linkTo(methodOn(NotificationController.class).sendWelcomeEmail(email)).withSelfRel(),
                linkTo(methodOn(NotificationController.class).sendDeletionEmail(email)).withRel("send-deletion"),
                linkTo(methodOn(NotificationController.class).health()).withRel("health")
        );
    }

    @PostMapping("/deletion")
    public EntityModel<NotificationResponse> sendDeletionEmail(
            @RequestParam @Email @NotBlank String email) {
        log.info("Ручная отправка письма об удалении на: {}", email);
        emailService.sendDeletionEmail(email);

        NotificationResponse response = new NotificationResponse(
                "Письмо об удалении отправлено",
                email,
                "DELETION"
        );

        return EntityModel.of(response,
                linkTo(methodOn(NotificationController.class).sendDeletionEmail(email)).withSelfRel(),
                linkTo(methodOn(NotificationController.class).sendWelcomeEmail(email)).withRel("send-welcome"),
                linkTo(methodOn(NotificationController.class).health()).withRel("health")
        );
    }

    @GetMapping("/health")
    public EntityModel<HealthStatus> health() {
        HealthStatus healthStatus = new HealthStatus(
                "RUNNING",
                Instant.now().toString(),
                "notification-service"
        );

        return EntityModel.of(healthStatus,
                linkTo(methodOn(NotificationController.class).health()).withSelfRel(),
                linkTo(methodOn(NotificationController.class).sendWelcomeEmail("")).withRel("send-welcome").withTitle("Send welcome email"),
                linkTo(methodOn(NotificationController.class).sendDeletionEmail("")).withRel("send-deletion").withTitle("Send deletion email")
        );
    }
}