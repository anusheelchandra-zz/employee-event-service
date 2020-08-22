package com.eventservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

  private String firstName;

  private String lastName;

  private String email;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String dateOfBirth;

  private String department;
}
