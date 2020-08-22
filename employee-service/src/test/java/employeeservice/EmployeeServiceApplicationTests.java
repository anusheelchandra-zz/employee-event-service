package employeeservice;

import employeeservice.controller.DepartmentController;
import employeeservice.controller.EmployeeController;
import employeeservice.repository.EmployeeRepository;
import employeeservice.service.EmployeeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class EmployeeServiceApplicationTests {

  @Autowired private EmployeeController employeeController;
  @Autowired private DepartmentController departmentController;
  @Autowired private EmployeeService employeeService;
  @Autowired private EmployeeRepository employeeRepository;

  @Test
  public void shouldLoadContexts() {
    Assertions.assertThat(employeeController).isNotNull();
    Assertions.assertThat(departmentController).isNotNull();
    Assertions.assertThat(employeeService).isNotNull();
    Assertions.assertThat(employeeRepository).isNotNull();
  }
}
