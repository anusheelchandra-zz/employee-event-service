package employeeservice.controller;

import employeeservice.domain.Constant;
import employeeservice.domain.DepartmentDTO;
import employeeservice.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "Department Controller")
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping(
      value = "/department/{department}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Department Controller to create department")
  @ApiResponses(value = {@ApiResponse(code = 201, message = "Department created")})
  public ResponseEntity<DepartmentDTO> createDepartment(
      @PathVariable
          @NotEmpty
          @Pattern(
              regexp = Constant.DEPARTMENT_REGEX,
              message = "department must only have alphabets")
          String department) {
    return new ResponseEntity<>(departmentService.createDepartment(department), HttpStatus.CREATED);
  }
}
