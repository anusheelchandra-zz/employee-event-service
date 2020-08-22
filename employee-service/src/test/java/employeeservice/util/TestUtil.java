package employeeservice.util;

import employeeservice.domain.EmployeeDTO;
import employeeservice.entity.Department;
import employeeservice.entity.Employee;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtil {

  public Employee buildEmployee(String uuid) {
    return Employee.builder()
        .id(uuid)
        .firstName("first")
        .lastName("last")
        .dateOfBirth(LocalDate.parse("2000-12-01"))
        .email("first.last@mail.com")
        .department(Department.builder().name("finance").build())
        .build();
  }

  public Employee buildEmployee() {
    return Employee.builder()
        .firstName("first")
        .lastName("last")
        .dateOfBirth(LocalDate.parse("2000-12-01"))
        .email("first.last@mail.com")
        .build();
  }

  public EmployeeDTO buildEmployeeDTO(String uuid) {
    return EmployeeDTO.builder()
        .uuid(uuid)
        .firstName("first")
        .lastName("last")
        .dateOfBirth("2000-12-01")
        .email("first.last@mail.com")
        .department("finance")
        .build();
  }

  public EmployeeDTO buildEmployeeDTO(
      String firstName, String lastName, String dob, String email, String department) {
    return EmployeeDTO.builder()
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(dob)
        .email(email)
        .department(department)
        .build();
  }
}
