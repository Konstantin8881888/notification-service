package org.klimtsov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.klimtsov.config.ControllerTestConfig;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import({ControllerTestConfig.class, GlobalExceptionHandlerTest.TestControllerConfig.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenMissingServletRequestParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/missing-param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(containsString("Отсутствует обязательный параметр")));
    }

    @Test
    void whenMethodArgumentTypeMismatch_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/type-mismatch")
                        .param("number", "not-a-number"))
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

    @RestController
    static class TestController {

        @GetMapping("/api/test/missing-param")
        public String testMissingParam(@RequestParam String required) {
            return "OK";
        }

        @GetMapping("/api/test/type-mismatch")
        public String testTypeMismatch(@RequestParam Integer number) {
            return "Number: " + number;
        }

        @GetMapping("/api/test/exception")
        public String testException() {
            throw new RuntimeException("Test exception");
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