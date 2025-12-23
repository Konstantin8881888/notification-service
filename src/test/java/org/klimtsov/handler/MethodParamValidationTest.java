package org.klimtsov.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.*;
import org.klimtsov.config.ControllerTestConfig;

import jakarta.validation.constraints.Size;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import({ControllerTestConfig.class, MethodParamValidationTest.TestControllerConfig.class})
class MethodParamValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenValidationOnRequestParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/validation-request-param")
                        .param("email", "inv"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации")));
    }

    @Test
    void whenValidationOnRequestParam_ValidEmail_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/test/validation-request-param")
                        .param("email", "valid@email.com"))
                .andExpect(status().isOk());
    }

    @RestController
    @Validated
    static class TestController {

        @GetMapping("/api/test/validation-request-param")
        public String testValidationRequestParam(
                @RequestParam
                @Size(min = 5, max = 50, message = "Email must be between 5 and 50 characters")
                String email) {
            return "Email: " + email;
        }
    }

    @Configuration
    @Import(ControllerTestConfig.class)
    static class TestControllerConfig {
        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }
}