package com.eventservice;

import com.eventservice.controller.EventController;
import com.eventservice.repository.EventRepository;
import com.eventservice.service.EventService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class EventServiceApplicationTests {

  @Autowired private EventController eventController;
  @Autowired private EventService eventService;
  @Autowired private EventRepository eventRepository;

  @Test
  public void shouldLoadContext() {
    Assertions.assertThat(eventController).isNotNull();
    Assertions.assertThat(eventService).isNotNull();
    Assertions.assertThat(eventRepository).isNotNull();
  }
}
