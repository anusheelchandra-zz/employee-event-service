package employeeservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import employeeservice.domain.Action;
import employeeservice.domain.message.EventMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.test.context.ActiveProfiles;

@Disabled(
    "Manually execute with RabbitMQ running or started using : docker run -d --hostname my-rabbit --name my-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management")
@ActiveProfiles("test")
@EnableBinding(Sink.class)
@SpringBootTest
class EventPublisherIT {

  private static final String EMP_UUID = "d881e278-9148-4ea2-b4fc-1735978ac4a";

  @Autowired private EventPublisher eventPublisher;
  @Autowired private Sink sink;
  @Autowired private AmqpAdmin admin;
  @Autowired private RabbitTemplate template;
  @Autowired private ObjectMapper objectMapper;

  @Test
  public void shouldSuccessfullyPublishEventMessage() throws IOException {
    var queue = this.admin.declareQueue();
    this.admin.declareBinding(
        new Binding(
            Objects.requireNonNull(queue).getName(),
            DestinationType.QUEUE,
            "event-exchange",
            "#",
            null));

    eventPublisher.publishEvent(EMP_UUID, Action.CREATE);
    var publishedMessage = template.receive(queue.getName());

    Assertions.assertThat(publishedMessage).isNotNull();
    Assertions.assertThat(publishedMessage.getBody()).isNotEmpty();

    var receivedEventMessage =
        objectMapper.readValue(publishedMessage.getBody(), EventMessage.class);

    Assertions.assertThat(receivedEventMessage.getEmployeeUuid()).isEqualTo(EMP_UUID);
    Assertions.assertThat(receivedEventMessage.getAction()).isEqualTo(Action.CREATE);
    Assertions.assertThat(receivedEventMessage.getTimestamp())
        .isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
  }
}
