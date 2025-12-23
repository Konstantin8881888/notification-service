package org.klimtsov.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.klimtsov.config.ControllerTestConfig;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import({ControllerTestConfig.class, GlobalExceptionHandlerIntegrationTest.TestControllerConfig.class})
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenMissingRequiredParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/test/missing-param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Отсутствует обязательный параметр")));
    }

    @Test
    void whenConstraintViolation_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/constraint-violation")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации")));
    }

    @Test
    void whenMethodArgumentNotValid_ShouldReturnBadRequest() throws Exception {
        String jsonBody = "{\"email\":\"\"}";
        mockMvc.perform(post("/api/test/method-argument-not-valid")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации полей")));
    }

    @Test
    void whenMethodArgumentTypeMismatch_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/type-mismatch")
                        .param("id", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Неверный тип параметра")));
    }

    @Test
    void whenGenericException_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/api/test/exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Внутренняя ошибка сервера")));
    }

    @Test
    void whenConstraintViolationWithNullEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/constraint-violation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Отсутствует обязательный параметр")));
    }

    @Test
    void whenMethodArgumentNotValidWithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        String jsonBody = "{\"email\":\"invalid-email\"}";
        mockMvc.perform(post("/api/test/method-argument-not-valid")
                        .contentType("application/json")
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации полей")));
    }

    @RestController
    @Validated
    static class TestController {

        @PostMapping("/api/test/missing-param")
        public String testMissingParam(@RequestParam String required) {
            return "OK";
        }

        @GetMapping("/api/test/constraint-violation")
        public String testConstraintViolation(@RequestParam @Email String email) {
            return "Email: " + email;
        }

        @PostMapping("/api/test/method-argument-not-valid")
        public String testMethodArgumentNotValid(@RequestBody @Valid TestRequest request) {
            return "OK";
        }

        @GetMapping("/api/test/type-mismatch")
        public String testTypeMismatch(@RequestParam Integer id) {
            return "ID: " + id;
        }

        @GetMapping("/api/test/exception")
        public String testException() {
            throw new RuntimeException("Test exception");
        }
    }

    static class TestRequest {
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    @Configuration
    @Import(ControllerTestConfig.class)
    static class TestControllerConfig {
        @Bean
        public TestController testController() {
            return new TestController();
        }
    }
}