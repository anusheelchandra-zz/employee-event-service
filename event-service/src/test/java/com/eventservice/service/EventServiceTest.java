package com.eventservice.service;

import com.eventservice.domain.Action;
import com.eventservice.domain.EventDTO;
import com.eventservice.entity.Event;
import com.eventservice.exception.EmployeeAttributeException;
import com.eventservice.exception.EntityNotFoundException;
import com.eventservice.repository.EventRepository;
import com.eventservice.util.TestUtil;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

class EventServiceTest {

  private final String EMP_UUID = "d881e278-9148-4ea2-b4fc-1735978ac4a";

  @Mock private EventRepository eventRepository;
  private EventService eventService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    eventService = new EventService(eventRepository);

    Mockito.when(
            eventRepository.findAllByEmployeeUuid(
                ArgumentMatchers.anyString(), ArgumentMatchers.any(Sort.class)))
        .thenReturn(TestUtil.createEvents(EMP_UUID));
  }

  @Test
  public void shouldSaveEvent() {
    Mockito.when(eventRepository.save(ArgumentMatchers.any(Event.class))).thenReturn(buildEvent());
    var persistedEvent = eventService.persistEvent(buildEvent());
    Assertions.assertThat(persistedEvent).isNotNull();
    Assertions.assertThat(persistedEvent.getEmployeeUuid()).isEqualTo(EMP_UUID);
    Assertions.assertThat(persistedEvent.getAction()).isEqualTo(Action.CREATE);
    Assertions.assertThat(persistedEvent.getCreated())
        .isEqualTo(LocalDateTime.parse("2020-08-18T11:24:58.62956"));
  }

  @Test
  public void shouldThrowExceptionWhenEventHasNullEmployeeUuid() {
    var event = buildEvent();
    event.setEmployeeUuid(null);
    Assertions.assertThatThrownBy(() -> eventService.persistEvent(event))
        .isInstanceOf(EmployeeAttributeException.class)
        .hasMessage("EmployeeUuid has null/empty value");
  }

  @Test
  public void shouldThrowExceptionWhenEventHasEmptyEmployeeUuid() {
    var event = buildEvent();
    event.setEmployeeUuid("");
    Assertions.assertThatThrownBy(() -> eventService.persistEvent(event))
        .isInstanceOf(EmployeeAttributeException.class)
        .hasMessage("EmployeeUuid has null/empty value");
  }

  @Test
  public void shouldThrowExceptionWhenEventHasNullAction() {
    var event = buildEvent();
    event.setAction(null);
    Assertions.assertThatThrownBy(() -> eventService.persistEvent(event))
        .isInstanceOf(EmployeeAttributeException.class)
        .hasMessage("Action has null value");
  }

  @Test
  public void shouldThrowExceptionWhenEventHasNullCreatedTimestamp() {
    var event = buildEvent();
    event.setCreated(null);
    Assertions.assertThatThrownBy(() -> eventService.persistEvent(event))
        .isInstanceOf(EmployeeAttributeException.class)
        .hasMessage("Created timestamp has null value");
  }

  @Test
  public void shouldGetAllEventsByEmployeeUuid() {
    var eventDTOList = eventService.getAllEventsByEmployeeUuid(EMP_UUID);
    Assertions.assertThat(eventDTOList).isNotEmpty();
    Assertions.assertThat(eventDTOList).hasSize(2);

    var expectedListOrder =
        eventDTOList.stream()
            .sorted(Comparator.comparing(EventDTO::getEventTimeStamp))
            .collect(Collectors.toUnmodifiableList());

    Assertions.assertThat(eventDTOList).containsExactlyElementsOf(expectedListOrder);
    TestUtil.commonAssert(
        eventDTOList.get(0), EMP_UUID, Action.CREATE, "2020-08-18T10:24:58.62956");
    TestUtil.commonAssert(
        eventDTOList.get(1), EMP_UUID, Action.UPDATE, "2020-08-18T11:24:58.62956");
  }

  @Test
  public void shouldNotGetAllEventsByEmployeeUuid() {
    Mockito.when(
            eventRepository.findAllByEmployeeUuid(
                ArgumentMatchers.anyString(), ArgumentMatchers.any(Sort.class)))
        .thenReturn(Collections.emptyList());
    Assertions.assertThatThrownBy(
            () -> eventService.getAllEventsByEmployeeUuid(UUID.randomUUID().toString()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("No events found for uuid");
  }

  @Test
  public void shouldNotGetAllEventsByEmployeeUuidWithNullUuid() {
    Mockito.when(
            eventRepository.findAllByEmployeeUuid(
                ArgumentMatchers.anyString(), ArgumentMatchers.any(Sort.class)))
        .thenReturn(Collections.emptyList());
    Assertions.assertThatThrownBy(() -> eventService.getAllEventsByEmployeeUuid(null))
        .isInstanceOf(ValidationException.class)
        .hasMessage("Employee uuid is null or empty");
  }

  @Test
  public void shouldNotGetAllEventsByEmployeeUuidWithEmptyUuid() {
    Mockito.when(
            eventRepository.findAllByEmployeeUuid(
                ArgumentMatchers.anyString(), ArgumentMatchers.any(Sort.class)))
        .thenReturn(Collections.emptyList());
    Assertions.assertThatThrownBy(() -> eventService.getAllEventsByEmployeeUuid(""))
        .isInstanceOf(ValidationException.class)
        .hasMessage("Employee uuid is null or empty");
  }

  private Event buildEvent() {
    return Event.builder()
        .employeeUuid(EMP_UUID)
        .action(Action.CREATE)
        .created(LocalDateTime.parse("2020-08-18T11:24:58.62956"))
        .build();
  }
}
