package employeeservice.mapper;

import employeeservice.entity.Department;
import employeeservice.util.TestUtil;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EmployeeMapperTest {

  private static final String EMP_UUID = "d881e278-9148-4ea2-b4fc-1735978ac4a";

  @Test
  public void shouldMapToEmployeeDTO() {
    var employee = TestUtil.buildEmployee(EMP_UUID);
    var employeeDTO = EmployeeMapper.toEmployeeDTO(employee);

    Assertions.assertThat(employeeDTO).isNotNull();
    Assertions.assertThat(employeeDTO.getUuid()).isEqualTo(employee.getId());
    Assertions.assertThat(employeeDTO.getFirstName()).isEqualTo(employee.getFirstName());
    Assertions.assertThat(employeeDTO.getLastName()).isEqualTo(employee.getLastName());
    Assertions.assertThat(employeeDTO.getEmail()).isEqualTo(employee.getEmail());
    Assertions.assertThat(employeeDTO.getDateOfBirth())
        .isEqualTo(employee.getDateOfBirth().toString());
    Assertions.assertThat(employeeDTO.getDepartment())
        .isEqualTo(employee.getDepartment().getName());
  }

  @Test
  public void shouldMapToEmployee() {
    var employeeDTO = TestUtil.buildEmployeeDTO(EMP_UUID);
    var employee =
        EmployeeMapper.toEmployee(employeeDTO, Department.builder().name("finance").build());

    Assertions.assertThat(employee).isNotNull();
    Assertions.assertThat(employee.getId()).isNull();
    Assertions.assertThat(employee.getFirstName()).isEqualTo(employeeDTO.getFirstName());
    Assertions.assertThat(employee.getLastName()).isEqualTo(employeeDTO.getLastName());
    Assertions.assertThat(employee.getEmail()).isEqualTo(employeeDTO.getEmail());
    Assertions.assertThat(employee.getDateOfBirth())
        .isEqualTo(LocalDate.parse(employeeDTO.getDateOfBirth()));
    Assertions.assertThat(employee.getDepartment().getName())
        .isEqualTo(employeeDTO.getDepartment());
  }
}
