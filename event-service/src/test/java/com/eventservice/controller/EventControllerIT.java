package com.eventservice.controller;

import com.eventservice.domain.Action;
import com.eventservice.domain.EventDTO;
import com.eventservice.entity.Event;
import com.eventservice.exception.EntityNotFoundException;
import com.eventservice.exception.handler.ErrorResponse;
import com.eventservice.repository.EventRepository;
import com.eventservice.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIT {

  private final String EMP_UUID = "61cbe737-704c-4732-ac35-53c3d1b4ea64";

  @Autowired private MockMvc mockMvc;
  @Autowired private EventService eventService;
  @Autowired private EventRepository eventRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    persistEvents();
  }

  @Test
  public void shouldGetAllEventsByUuid() throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/event/{uuid}", EMP_UUID)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    Assertions.assertThat(mvcResult).isNotNull();
    Assertions.assertThat(mvcResult.getResponse().getContentAsString()).isNotEmpty();
    var eventDTOList =
        Arrays.asList(
            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), EventDTO[].class));
    Assertions.assertThat(eventDTOList).isNotEmpty();

    var expectedListOrder =
        eventDTOList.stream()
            .sorted(Comparator.comparing(EventDTO::getEventTimeStamp))
            .collect(Collectors.toUnmodifiableList());

    Assertions.assertThat(eventDTOList).containsExactlyElementsOf(expectedListOrder);

    commonAssert(eventDTOList.get(0), Action.CREATE, "2020-08-18T10:24:58.62956");
    commonAssert(eventDTOList.get(1), Action.UPDATE, "2020-08-18T11:24:58.62956");
    commonAssert(eventDTOList.get(2), Action.DELETE, "2020-08-18T12:24:58.62956");
  }

  @Test
  public void shouldThrowExceptionWhenUuidIsNotPresentInDatabase() throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/event/{uuid}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult, EntityNotFoundException.class, HttpStatus.NOT_FOUND, "Entity not found");
  }

  @ParameterizedTest
  @ValueSource(strings = {"d881e278-9148", "123", " "})
  public void shouldThrowExceptionWhenUuidIsInvalid(String uuid) throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/event/{uuid}", uuid)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult, ConstraintViolationException.class, HttpStatus.BAD_REQUEST, "Wrong input data");
  }

  @AfterEach
  public void tearDown() {
    eventRepository.deleteAll();
  }

  private void commonMvcResultAsserts(
      MvcResult mvcResult,
      Class<? extends Exception> exceptionClass,
      HttpStatus httpStatus,
      String message)
      throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
    Assertions.assertThat(mvcResult.getResponse()).isNotNull();
    var errorResponse =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
    Assertions.assertThat(errorResponse.getMessage()).isEqualTo(message);
    Assertions.assertThat(errorResponse.getErrorCode()).isEqualTo(httpStatus.value());
    Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(exceptionClass);
  }

  public void commonAssert(EventDTO eventDTO, Action action, String timestamp) {
    Assertions.assertThat(eventDTO.getEmployeeUuid()).isEqualTo(EMP_UUID);
    Assertions.assertThat(eventDTO.getEventType()).isEqualTo(action.toString());
    Assertions.assertThat(eventDTO.getEventTimeStamp()).isEqualTo(LocalDateTime.parse(timestamp));
  }

  private void persistEvents() {
    var create = buildEvent(Action.CREATE, "2020-08-18T10:24:58.62956");
    var update = buildEvent(Action.UPDATE, "2020-08-18T11:24:58.62956");
    var delete = buildEvent(Action.DELETE, "2020-08-18T12:24:58.62956");
    eventRepository.save(create);
    eventRepository.save(update);
    eventRepository.save(delete);
  }

  private Event buildEvent(Action action, String timestamp) {
    return Event.builder()
        .employeeUuid(EMP_UUID)
        .action(action)
        .created(LocalDateTime.parse(timestamp))
        .build();
  }
}
