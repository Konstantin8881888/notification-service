package org.klimtsov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.klimtsov.config.ControllerTestConfig;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Отдельный тестовый класс для MethodArgumentTypeMismatchException чтобы избежать конфликтов с основным тестовым классом.
@WebMvcTest
@Import({ControllerTestConfig.class, GlobalExceptionHandlerTest.TestControllerConfig.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenInvalidIntegerParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/int")
                        .param("number", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.number").exists())
                .andExpect(jsonPath("$.number").value(containsString("Неверный тип параметра")));
    }

    @Test
    void whenInvalidBooleanParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/test/bool")
                        .param("flag", "not-a-boolean"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").exists())
                .andExpect(jsonPath("$.flag").value(containsString("Неверный тип параметра")));
    }

    //Тестовый контроллер.
    @RestController
    static class TestController {

        @GetMapping("/api/test/int")
        public String testInt(@RequestParam Integer number) {
            return "Number: " + number;
        }

        @GetMapping("/api/test/bool")
        public String testBool(@RequestParam Boolean flag) {
            return "Flag: " + flag;
        }
    }

    //Конфигурация для регистрации тестового контроллера.
    @Configuration
    @Import(ControllerTestConfig.class)
    static class TestControllerConfig {
        @Bean
        public TestController testController() {
            return new TestController();
        }
    }
}