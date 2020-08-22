package employeeservice.controller;

import employeeservice.domain.Constant;
import employeeservice.domain.EmployeeDTO;
import employeeservice.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "Employee Controller")
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping(
      value = "/employee/{uuid}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Employee Controller to get employee by uuid")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Employee returned")})
  public ResponseEntity<EmployeeDTO> getEmployeeById(
      @PathVariable @Pattern(regexp = Constant.UUID_REGEX, message = "invalid format of Uuid")
          String uuid) {
    return new ResponseEntity<>(employeeService.getEmployeeById(uuid), HttpStatus.OK);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(
      value = "/employee",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Employee Controller to create employee")
  @ApiResponses(value = {@ApiResponse(code = 201, message = "Employee created")})
  public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
    return new ResponseEntity<>(employeeService.createEmployee(employeeDTO), HttpStatus.CREATED);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping(
      value = "/employee/{uuid}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value = "Employee Controller to update employee by uuid")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Employee updated")})
  public ResponseEntity<EmployeeDTO> updateEmployee(
      @PathVariable @Pattern(regexp = Constant.UUID_REGEX, message = "invalid format of Uuid")
          String uuid,
      @Valid @RequestBody EmployeeDTO employeeDTO) {
    return new ResponseEntity<>(employeeService.updateEmployee(uuid, employeeDTO), HttpStatus.OK);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping(
      value = "/employee/{uuid}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Employee Controller to delete employee by uuid")
  @ApiResponses(value = {@ApiResponse(code = 204, message = "Employee deleted")})
  public void deleteEmployee(
      @PathVariable @Pattern(regexp = Constant.UUID_REGEX, message = "invalid format of Uuid")
          String uuid) {
    employeeService.deleteEmployeeById(uuid);
  }
}
