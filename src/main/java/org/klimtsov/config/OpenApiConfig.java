package org.klimtsov.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notificationServiceOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:8082")
                .description("Локальный сервер разработки");

        Server dockerServer = new Server()
                .url("http://localhost:8080/api")
                .description("Docker окружение");

        return new OpenAPI()
                .servers(List.of(localServer, dockerServer))
                .info(new Info()
                        .title("Notification Service API")
                        .description("""
                            ## Микросервис уведомлений
                            
                            Сервис предоставляет REST API для отправки email-уведомлений:
                            - Приветственные письма при создании пользователя
                            - Письма об удалении аккаунта
                            
                            ## Особенности
                            - Поддержка HATEOAS (гипермедиа ссылки в ответах)
                            - Интеграция с Kafka для асинхронной обработки событий
                            - Настраиваемый режим работы (реальная отправка / логирование)
                            """));
    }
}