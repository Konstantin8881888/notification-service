package org.klimtsov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.klimtsov.service.EmailService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @Test
    void sendWelcomeEmail_ShouldCallEmailServiceAndReturnSuccessMessage() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(post("/api/notifications/welcome")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Приветственное письмо отправлено на: " + email));

        verify(emailService).sendWelcomeEmail(email);
    }

    @Test
    void sendDeletionEmail_ShouldCallEmailServiceAndReturnSuccessMessage() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(post("/api/notifications/deletion")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Письмо об удалении отправлено на: " + email));

        verify(emailService).sendDeletionEmail(email);
    }

    @Test
    void healthEndpoint_ShouldReturnServiceStatus() throws Exception {
        mockMvc.perform(get("/api/notifications/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification service is running on port 8081"));
    }
}