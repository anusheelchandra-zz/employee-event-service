package com.eventservice.repository;

import com.eventservice.domain.Action;
import com.eventservice.entity.Event;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class EventRepositoryIT {

  private final String EMP_UUID = "d881e278-9148-4ea2-b4fc-1735978ac4a";

  @Autowired private EventRepository eventRepository;

  @BeforeEach
  public void setup() {
    persistEvents();
  }

  @Test
  public void shouldReturnEventListInAscendingOrder() {
    var eventList =
        eventRepository.findAllByEmployeeUuid(EMP_UUID, Sort.by(Direction.ASC, "created"));
    Assertions.assertThat(eventList).isNotEmpty();
    Assertions.assertThat(eventList).hasSize(3);
    var expectedListOrder =
        eventList.stream()
            .sorted(Comparator.comparing(Event::getCreated))
            .collect(Collectors.toUnmodifiableList());

    Assertions.assertThat(eventList).containsExactlyElementsOf(expectedListOrder);

    commonAssert(eventList.get(0), Action.CREATE, "2020-08-18T10:24:58.62956");
    commonAssert(eventList.get(1), Action.UPDATE, "2020-08-18T11:24:58.62956");
    commonAssert(eventList.get(2), Action.DELETE, "2020-08-18T12:24:58.62956");
  }

  @Test
  public void shouldReturEmptyEventListWhenUuidDoesntExistInDatabase() {
    var eventList =
        eventRepository.findAllByEmployeeUuid(
            UUID.randomUUID().toString(), Sort.by(Direction.ASC, "created"));
    Assertions.assertThat(eventList).isEmpty();
  }

  @AfterEach
  public void tearDown() {
    eventRepository.deleteAll();
  }

  public void commonAssert(Event event, Action action, String timestamp) {
    Assertions.assertThat(event.getEmployeeUuid()).isEqualTo(EMP_UUID);
    Assertions.assertThat(event.getAction()).isEqualTo(action);
    Assertions.assertThat(event.getCreated()).isEqualTo(LocalDateTime.parse(timestamp));
  }

  private void persistEvents() {
    var create = buildEvent(Action.CREATE, "2020-08-18T10:24:58.62956");
    var update = buildEvent(Action.UPDATE, "2020-08-18T11:24:58.62956");
    var delete = buildEvent(Action.DELETE, "2020-08-18T12:24:58.62956");
    eventRepository.saveAll(List.of(create, update, delete));
  }

  private Event buildEvent(Action action, String timestamp) {
    return Event.builder()
        .employeeUuid(EMP_UUID)
        .action(action)
        .created(LocalDateTime.parse(timestamp))
        .build();
  }
}
