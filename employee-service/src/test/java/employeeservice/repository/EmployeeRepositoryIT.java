package employeeservice.repository;

import employeeservice.entity.Department;
import employeeservice.entity.Employee;
import employeeservice.util.TestUtil;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class EmployeeRepositoryIT {

  private static final String EMP_UUID = "d881e278-9148-4ea2-b4fc-1735978ac4a";

  @Autowired private EmployeeRepository employeeRepository;
  @Autowired private DepartmentRepository departmentRepository;

  private Department department;

  @BeforeEach
  public void setup() {
    department = departmentRepository.save(Department.builder().name("finance").build());
    var employee = TestUtil.buildEmployee();
    employee.setDepartment(department);
    employeeRepository.save(employee);
  }

  @Test
  public void shouldFindEmployeeByEmail() {
    var employee = employeeRepository.findByEmail("first.last@mail.com");
    Assertions.assertThat(employee).isPresent();
    commonAsserts(employee.get(), "first.last@mail.com");
  }

  @Test
  public void shouldNotFindEmployeeByEmail() {
    var employee = employeeRepository.findByEmail("first@mail.com");
    Assertions.assertThat(employee).isNotPresent();
  }

  @Test
  public void shouldSaveNewEmployee() {
    var employee = TestUtil.buildEmployee();
    employee.setDepartment(department);
    employee.setEmail("test.test@mail.com");
    var persistedEmployee = employeeRepository.save(employee);
    commonAsserts(persistedEmployee, "test.test@mail.com");
  }

  private void commonAsserts(Employee employee, String email) {
    Assertions.assertThat(employee).isNotNull();
    Assertions.assertThat(employee.getId()).isNotNull();
    Assertions.assertThat(employee.getEmail()).isEqualTo(email);
    Assertions.assertThat(employee.getFirstName()).isEqualTo("first");
    Assertions.assertThat(employee.getLastName()).isEqualTo("last");
    Assertions.assertThat(employee.getDepartment().getName()).isEqualTo("finance");
    Assertions.assertThat(employee.getDateOfBirth()).isEqualTo(LocalDate.parse("2000-12-01"));
  }

  @AfterEach
  public void tearDown() {
    employeeRepository.deleteAll();
    departmentRepository.deleteAll();
  }
}
