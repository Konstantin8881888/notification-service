package org.klimtsov.kafka;

import org.junit.jupiter.api.Test;
import org.klimtsov.config.TestProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.klimtsov.dto.UserEvent;
import org.klimtsov.service.EmailService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.group-id=notification-group",
        "notification.email.enabled=true",
        "notification.email.mock=true",
        "spring.main.allow-bean-definition-overriding=true"
})
@Import(TestProducerConfig.class)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = { "user-events" })
class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> testKafkaTemplate;

    @MockitoSpyBean
    private EmailService emailService;

    @Test
    void whenUserEventCreateIsSentToKafka_thenEmailServiceSendWelcomeEmailIsCalled() {
        UserEvent event = new UserEvent("test@example.com", "CREATE");
        testKafkaTemplate.send("user-events", event);
        verify(emailService, timeout(10000).times(1))
                .sendWelcomeEmail("test@example.com");
    }

    @Test
    void whenUserEventDeleteIsSentToKafka_thenEmailServiceSendDeletionEmailIsCalled() {
        UserEvent event = new UserEvent("test@example.com", "DELETE");
        testKafkaTemplate.send("user-events", event);
        verify(emailService, timeout(10000).times(1))
                .sendDeletionEmail("test@example.com");
    }
}