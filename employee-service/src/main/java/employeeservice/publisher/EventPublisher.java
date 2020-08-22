package employeeservice.publisher;

import employeeservice.domain.Action;
import employeeservice.domain.message.EventMessage;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@EnableBinding(Source.class)
@Component
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {

  private final Source source;

  public void publishEvent(String employeeUuid, Action action) {
    EventMessage eventMessage =
        EventMessage.builder()
            .employeeUuid(employeeUuid)
            .action(action)
            .timestamp(LocalDateTime.now())
            .build();

    source.output().send(MessageBuilder.withPayload(eventMessage).build());
    log.info(
        "Published: {} event for employee with Uuid: {}",
        eventMessage.getAction(),
        eventMessage.getEmployeeUuid());
  }
}
