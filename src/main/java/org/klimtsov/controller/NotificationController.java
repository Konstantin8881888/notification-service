package org.klimtsov.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.klimtsov.dto.ErrorResponse;
import org.klimtsov.dto.HealthStatus;
import org.klimtsov.dto.NotificationResponse;
import org.klimtsov.service.EmailService;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Уведомления", description = "API для отправки email-уведомлений")
public class NotificationController {

    private final EmailService emailService;

    @Operation(
            summary = "Отправить приветственное письмо",
            description = "Ручная отправка приветственного письма по email. "
                    + "В ответе содержатся HATEOAS-ссылки для навигации по API."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Письмо успешно отправлено",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный формат email адреса",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/welcome")
    @ResponseBody
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

    @Operation(
            summary = "Отправить письмо об удалении аккаунта",
            description = "Ручная отправка уведомления об удалении аккаунта."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Письмо успешно отправлено",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверный формат email адреса",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/deletion")
    @ResponseBody
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

    @Operation(
            summary = "Проверка здоровья сервиса",
            description = "Эндпоинт проверки здоровья с HATEOAS-ссылками на основные операции"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сервис работает",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/health")
    @ResponseBody
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