package employeeservice.repository;

import employeeservice.entity.Department;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class DepartmentRepositoryIT {

  private static final String DEPARTMENT_NAME = "finance";

  @Autowired private DepartmentRepository repository;

  @BeforeEach
  public void setup() {
    repository.save(Department.builder().name(DEPARTMENT_NAME).build());
  }

  @Test
  public void shouldFindDepartmentByName() {
    var department = repository.findByName(DEPARTMENT_NAME);
    Assertions.assertThat(department).isPresent();
    Assertions.assertThat(department.get().getName()).isEqualTo(DEPARTMENT_NAME);
  }

  @Test
  public void shouldNotFindDepartmentByName() {
    var department = repository.findByName("anyDepartment");
    Assertions.assertThat(department).isNotPresent();
  }

  @Test
  public void shouldSaveNewDepartment() {
    var persistedDepartment = repository.save(Department.builder().name("newDepartment").build());
    Assertions.assertThat(persistedDepartment).isNotNull();
    Assertions.assertThat(persistedDepartment.getId()).isNotNull();
    Assertions.assertThat(persistedDepartment.getName()).isEqualTo("newDepartment");
  }

  @AfterEach
  public void tearDown() {
    repository.deleteAll();
  }
}
