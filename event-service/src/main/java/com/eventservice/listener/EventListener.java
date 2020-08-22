package com.eventservice.listener;

import com.eventservice.domain.message.EventMessage;
import com.eventservice.mapper.EventMapper;
import com.eventservice.service.EventService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@Slf4j
@EnableBinding(Sink.class)
@RequiredArgsConstructor
public class EventListener {

  private final EventService eventService;

  @StreamListener(target = Sink.INPUT)
  public void listenEvent(@Valid EventMessage eventMessage) {
    log.info(" Received new event: {} ", eventMessage);
    var persistedEvent = eventService.persistEvent(EventMapper.toEvent(eventMessage));
    log.info(" Persisted new event: {} ", persistedEvent);
  }
}
