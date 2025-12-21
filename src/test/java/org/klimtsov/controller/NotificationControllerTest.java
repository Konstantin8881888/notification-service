package org.klimtsov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.klimtsov.service.EmailService;
import org.klimtsov.config.ControllerTestConfig;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(ControllerTestConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Test
    void sendWelcomeEmail_ShouldReturnHateoasLinks() throws Exception {
        String email = "test@example.com";

        doNothing().when(emailService).sendWelcomeEmail(email);

        mockMvc.perform(post("/api/notifications/welcome")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Приветственное письмо отправлено"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.notificationType").value("WELCOME"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.send-deletion.href").exists())
                .andExpect(jsonPath("$._links.health.href").exists())
                .andExpect(jsonPath("$._links.self.href").value(
                        "http://localhost/api/notifications/welcome?email=test%40example.com"));

        verify(emailService, times(1)).sendWelcomeEmail(email);
    }

    @Test
    void sendDeletionEmail_ShouldReturnHateoasLinks() throws Exception {
        String email = "test@example.com";

        doNothing().when(emailService).sendDeletionEmail(email);

        mockMvc.perform(post("/api/notifications/deletion")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Письмо об удалении отправлено"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.notificationType").value("DELETION"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.send-welcome.href").exists())
                .andExpect(jsonPath("$._links.health.href").exists())
                .andExpect(jsonPath("$._links.self.href").value(
                        "http://localhost/api/notifications/deletion?email=test%40example.com"));

        verify(emailService, times(1)).sendDeletionEmail(email);
    }

    @Test
    void healthEndpoint_ShouldReturnHateoasLinks() throws Exception {
        mockMvc.perform(get("/api/notifications/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.serviceName").value("notification-service"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.send-welcome.href").exists())
                .andExpect(jsonPath("$._links.send-deletion.href").exists())
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/notifications/health"))
                .andExpect(jsonPath("$._links.send-welcome.title").value("Send welcome email"))
                .andExpect(jsonPath("$._links.send-deletion.title").value("Send deletion email"));
    }

    @Test
    void sendWelcomeEmail_WithoutEmailParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/welcome"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendDeletionEmail_WithoutEmailParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/deletion"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendWelcomeEmail_WhenEmailServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        String email = "test@example.com";

        doThrow(new RuntimeException("SMTP error"))
                .when(emailService).sendWelcomeEmail(email);

        mockMvc.perform(post("/api/notifications/welcome")
                        .param("email", email))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void sendWelcomeEmail_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/welcome")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['email']").exists());
    }

    @Test
    void sendWelcomeEmail_WithEmptyEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/welcome")
                        .param("email", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['email']").exists());
    }

    @Test
    void sendWelcomeEmail_WithNullEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/welcome"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendDeletionEmail_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/deletion")
                        .param("email", "not-an-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['email']").exists());
    }
}