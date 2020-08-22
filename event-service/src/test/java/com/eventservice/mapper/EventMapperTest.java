package com.eventservice.mapper;

import com.eventservice.domain.Action;
import com.eventservice.domain.message.EventMessage;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EventMapperTest {

  @Test
  private void shouldMapAndReturnNonEmptyEventMessageList() {
    var eventMessage =
        EventMessage.builder()
            .action(Action.CREATE)
            .employeeUuid("d881e278-9148-4ea2-b4fc-1735978ac4a")
            .timestamp(LocalDateTime.parse("2020-08-18T10:24:58.62956"))
            .build();
    var event = EventMapper.toEvent(eventMessage);
    Assertions.assertThat(event).isNotNull();
    Assertions.assertThat(event.getEmployeeUuid()).isEqualTo(eventMessage.getEmployeeUuid());
    Assertions.assertThat(event.getAction()).isEqualTo(eventMessage.getAction());
    Assertions.assertThat(event.getCreated()).isEqualTo(eventMessage.getTimestamp());
  }
}
