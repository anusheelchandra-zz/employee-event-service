package com.eventservice.exception.handler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class ErrorHandlerTest {

  private ErrorHandler errorHandler;

  @BeforeEach
  public void setup() {
    errorHandler = new ErrorHandler();
  }

  @Test
  public void shouldHandleEmployeeAttributeException() {
    var errorResponseEntity = errorHandler.employeeAttributeException(new RuntimeException("test"));
    Assertions.assertThat(errorResponseEntity.getBody()).isNotNull();
    Assertions.assertThat(errorResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(errorResponseEntity.getBody().getMessage())
        .isEqualTo("Wrong Employee input data");
  }

  @Test
  public void shouldHandleEntityNotFoundException() {
    var errorResponseEntity = errorHandler.entityNotFoundException(new RuntimeException("test"));
    Assertions.assertThat(errorResponseEntity.getBody()).isNotNull();
    Assertions.assertThat(errorResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(errorResponseEntity.getBody().getMessage()).isEqualTo("Entity not found");
  }

  @Test
  public void shouldHandleUnknownException() {
    var errorResponseEntity = errorHandler.unknownException(new RuntimeException("test"));
    Assertions.assertThat(errorResponseEntity.getBody()).isNotNull();
    Assertions.assertThat(errorResponseEntity.getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    Assertions.assertThat(errorResponseEntity.getBody().getMessage())
        .isEqualTo("Internal Server Error");
  }
}
