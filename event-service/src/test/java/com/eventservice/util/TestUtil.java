package com.eventservice.util;

import com.eventservice.domain.Action;
import com.eventservice.domain.EventDTO;
import com.eventservice.entity.Event;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

@UtilityClass
public class TestUtil {

  public List<Event> createEvents(String uuid) {
    Event create = buildEvent(uuid, Action.CREATE, "2020-08-18T10:24:58.62956");
    Event update = buildEvent(uuid, Action.UPDATE, "2020-08-18T11:24:58.62956");
    return List.of(create, update);
  }

  private Event buildEvent(String uuid, Action action, String localDateTime) {
    return Event.builder()
        .employeeUuid(uuid)
        .action(action)
        .created(LocalDateTime.parse(localDateTime))
        .build();
  }

  public void commonAssert(EventDTO eventDTO, String uuid, Action action, String timestamp) {
    Assertions.assertThat(eventDTO.getEmployeeUuid()).isEqualTo(uuid);
    Assertions.assertThat(eventDTO.getEventType()).isEqualTo(action.toString());
    Assertions.assertThat(eventDTO.getEventTimeStamp()).isEqualTo(LocalDateTime.parse(timestamp));
  }
}
