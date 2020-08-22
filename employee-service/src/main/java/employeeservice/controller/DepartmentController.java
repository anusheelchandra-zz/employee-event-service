package employeeservice.controller;

import employeeservice.domain.DepartmentDTO;
import employeeservice.domain.DepartmentRequestDTO;
import employeeservice.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@Api(value = "Department Controller")
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping(
      value = "/department",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Department Controller to create department")
  @ApiResponses(value = {@ApiResponse(code = 201, message = "Department created")})
  public ResponseEntity<DepartmentDTO> createDepartment(
      @Valid @RequestBody DepartmentRequestDTO requestDTO) {
    return new ResponseEntity<>(
        departmentService.createDepartment(requestDTO.getName()), HttpStatus.CREATED);
  }
}
