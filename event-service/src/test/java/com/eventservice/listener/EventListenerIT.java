package com.eventservice.listener;

import com.eventservice.domain.Action;
import com.eventservice.domain.message.EventMessage;
import com.eventservice.exception.EntityNotFoundException;
import com.eventservice.repository.EventRepository;
import com.eventservice.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

@Disabled(
    "Manually execute with RabbitMQ running or started using : docker run -d --hostname my-rabbit --name my-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management")
@ActiveProfiles("test")
@EnableBinding(Source.class)
@SpringBootTest
class EventListenerIT {

  private static final String EMP_UUID = "d881e278-9148-4ea2-b4fc-1735978ac4a";
  private static final String EVENT_TIMESTAMP = "2020-08-18T10:24:58.62956";

  @Autowired private Source source;
  @Autowired private EventListener eventListener;
  @Autowired private EventService eventService;
  @Autowired private AmqpAdmin admin;
  @Autowired private RabbitTemplate template;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private EventRepository eventRepository;

  @Test
  public void shouldSuccessfullyListenToEventMessage() throws IOException {
    var queue = this.admin.declareQueue();
    this.admin.declareBinding(
        new Binding(
            Objects.requireNonNull(queue).getName(),
            DestinationType.QUEUE,
            "event-exchange",
            "#",
            null));

    var eventMessage = objectMapper.writeValueAsString(createEventMessage());
    source.output().send(MessageBuilder.withPayload(eventMessage).build());

    var receivedMessage = template.receive(queue.getName());

    Assertions.assertThat(receivedMessage).isNotNull();

    var receivedEventMessage =
        objectMapper.readValue(receivedMessage.getBody(), EventMessage.class);

    Assertions.assertThat(receivedEventMessage.getEmployeeUuid()).isEqualTo(EMP_UUID);
    Assertions.assertThat(receivedEventMessage.getAction()).isEqualTo(Action.CREATE);
    Assertions.assertThat(receivedEventMessage.getTimestamp())
        .isEqualTo(LocalDateTime.parse(EVENT_TIMESTAMP));
  }

  @Test
  public void shouldDiscardInvalidEventMessage() throws IOException {
    var queue = this.admin.declareQueue();
    this.admin.declareBinding(
        new Binding(
            Objects.requireNonNull(queue).getName(),
            DestinationType.QUEUE,
            "event-exchange",
            "#",
            null));

    var eventMessage = objectMapper.writeValueAsString(createEventMessage()).replace(EMP_UUID, "");
    source.output().send(MessageBuilder.withPayload(eventMessage).build());
    template.receive(queue.getName());

    Assertions.assertThatThrownBy(() -> eventService.getAllEventsByEmployeeUuid(EMP_UUID))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("No events found for uuid");
  }

  @Test
  public void shouldSuccessfullyPersistTheReceivedEventMessage() {
    source.output().send(MessageBuilder.withPayload(createEventMessage()).build());

    var eventsByEmployeeUuid = eventService.getAllEventsByEmployeeUuid(EMP_UUID);

    Assertions.assertThat(eventsByEmployeeUuid).isNotEmpty();
    Assertions.assertThat(eventsByEmployeeUuid).hasSize(1);
    Assertions.assertThat(eventsByEmployeeUuid.get(0).getEmployeeUuid()).isEqualTo(EMP_UUID);
    Assertions.assertThat(eventsByEmployeeUuid.get(0).getEventType())
        .isEqualTo(Action.CREATE.toString());
    Assertions.assertThat(eventsByEmployeeUuid.get(0).getEventTimeStamp())
        .isEqualTo(EVENT_TIMESTAMP);
  }

  @AfterEach
  public void tearDown() {
    eventRepository.deleteAll();
  }

  private EventMessage createEventMessage() {
    return EventMessage.builder()
        .action(Action.CREATE)
        .employeeUuid(EMP_UUID)
        .timestamp(LocalDateTime.parse(EVENT_TIMESTAMP))
        .build();
  }
}
