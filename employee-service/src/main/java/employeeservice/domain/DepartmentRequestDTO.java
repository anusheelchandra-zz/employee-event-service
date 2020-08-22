package employeeservice.domain;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDTO {

  @NonNull
  @Pattern(regexp = Constant.DEPARTMENT_REGEX, message = "department must only have alphabets")
  @ApiModelProperty(notes = "department must be only alphabets", required = true)
  private String name;
}
