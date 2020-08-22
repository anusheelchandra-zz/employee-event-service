package employeeservice.domain;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

  @ApiModelProperty(hidden = true)
  private String uuid;

  @NotNull
  @NotEmpty
  @Pattern(regexp = Constant.NAME_REGEX, message = "first name must only have alphabets")
  @ApiModelProperty(notes = "firstName must be only alphabets", required = true)
  private String firstName;

  @NotNull
  @NotEmpty
  @Pattern(regexp = Constant.NAME_REGEX, message = "last name must only have alphabets")
  @ApiModelProperty(notes = "lastName must be only alphabets", required = true)
  private String lastName;

  @NotNull
  @NotEmpty
  @Pattern(regexp = Constant.EMAIL_REGEX, message = "email must be valid as per regex")
  @ApiModelProperty(notes = "email must be only standard type", required = true)
  private String email;

  @NotNull
  @NotEmpty
  @Pattern(regexp = Constant.DATE_REGEX, message = "date must in yyyy-MM-dd format")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @ApiModelProperty(notes = "dateOfBirth must be in format yyyy-MM-dd", required = true)
  private String dateOfBirth;

  @NotNull
  @NotEmpty
  @Pattern(regexp = Constant.DEPARTMENT_REGEX, message = "department must only have alphabets")
  @ApiModelProperty(notes = "department must be only alphabets", required = true)
  private String department;
}
