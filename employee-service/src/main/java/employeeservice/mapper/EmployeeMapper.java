package employeeservice.mapper;

import employeeservice.domain.EmployeeDTO;
import employeeservice.entity.Department;
import employeeservice.entity.Employee;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EmployeeMapper {

  private final String FORMAT = "yyyy-MM-dd";

  public EmployeeDTO toEmployeeDTO(Employee employee) {
    return EmployeeDTO.builder()
        .uuid(employee.getId())
        .firstName(employee.getFirstName().toLowerCase())
        .lastName(employee.getLastName().toLowerCase())
        .email(employee.getEmail().toLowerCase())
        .department(employee.getDepartment().getName().toLowerCase())
        .dateOfBirth(formatDate(employee.getDateOfBirth()))
        .build();
  }

  public Employee toEmployee(EmployeeDTO employeeDTO, Department department) {
    return Employee.builder()
        .firstName(employeeDTO.getFirstName().toLowerCase())
        .lastName(employeeDTO.getLastName().toLowerCase())
        .email(employeeDTO.getEmail().toLowerCase())
        .dateOfBirth(LocalDate.parse(employeeDTO.getDateOfBirth()))
        .department(department)
        .build();
  }

  private String formatDate(LocalDate date) {
    return DateTimeFormatter.ofPattern(FORMAT).format(date);
  }
}
