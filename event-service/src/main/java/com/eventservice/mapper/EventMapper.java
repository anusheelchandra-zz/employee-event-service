package com.eventservice.mapper;

import com.eventservice.domain.message.EventMessage;
import com.eventservice.entity.Event;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventMapper {

  public Event toEvent(EventMessage eventMessage) {
    return Event.builder()
        .employeeUuid(eventMessage.getEmployeeUuid())
        .action(eventMessage.getAction())
        .created(eventMessage.getTimestamp())
        .build();
  }
}
