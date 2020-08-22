package com.eventservice.mapper;

import com.eventservice.domain.EventDTO;
import com.eventservice.entity.Event;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventDTOMapper {

  public List<EventDTO> toEventDTOs(List<Event> events) {
    return events.stream()
        .filter(Objects::nonNull)
        .map(EventDTOMapper::toEventDTO)
        .collect(Collectors.toUnmodifiableList());
  }

  private EventDTO toEventDTO(Event event) {
    return EventDTO.builder()
        .employeeUuid(event.getEmployeeUuid())
        .eventType(event.getAction().toString())
        .eventTimeStamp(event.getCreated())
        .build();
  }
}
