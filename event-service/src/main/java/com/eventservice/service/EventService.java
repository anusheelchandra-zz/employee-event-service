package com.eventservice.service;

import com.eventservice.domain.EventDTO;
import com.eventservice.entity.Event;
import com.eventservice.exception.EmployeeAttributeException;
import com.eventservice.exception.EntityNotFoundException;
import com.eventservice.mapper.EventDTOMapper;
import com.eventservice.repository.EventRepository;
import java.util.List;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

  private static final String SORT_BY_FIELD = "created";

  private final EventRepository eventRepository;

  public Event persistEvent(Event event) {
    validateEvent(event);
    return eventRepository.save(event);
  }

  public List<EventDTO> getAllEventsByEmployeeUuid(String employeeUuid) {
    validationEmployeeUuid(employeeUuid);
    var eventList =
        eventRepository.findAllByEmployeeUuid(employeeUuid, Sort.by(Direction.ASC, SORT_BY_FIELD));
    if (eventList.isEmpty()) {
      throw new EntityNotFoundException(
          String.format("No events found for uuid: %s", employeeUuid));
    }
    return EventDTOMapper.toEventDTOs(eventList);
  }

  private void validationEmployeeUuid(String uuid) {
    if (uuid == null || uuid.isEmpty())
      throw new ValidationException("Employee uuid is null or empty");
  }

  private void validateEvent(Event event) {
    if (event.getEmployeeUuid() == null || event.getEmployeeUuid().isEmpty()) {
      throw new EmployeeAttributeException("EmployeeUuid has null/empty value");
    }
    if (event.getAction() == null) {
      throw new EmployeeAttributeException("Action has null value");
    }
    if (event.getCreated() == null) {
      throw new EmployeeAttributeException("Created timestamp has null value");
    }
  }
}
