package com.eventservice.controller;

import com.eventservice.domain.EventDTO;
import com.eventservice.service.EventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "Event Controller")
public class EventController {

  public static final String UUID_REGEX =
      "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

  private final EventService eventService;

  @GetMapping(
      value = "/event/{uuid}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Event Controller to get all event by uuid")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Events returned")})
  public ResponseEntity<List<EventDTO>> getAllEventsByEmployeeUuid(
      @PathVariable @NotEmpty @Pattern(regexp = UUID_REGEX, message = "invalid format of Uuid")
          String uuid) {
    return new ResponseEntity<>(eventService.getAllEventsByEmployeeUuid(uuid), HttpStatus.OK);
  }
}
