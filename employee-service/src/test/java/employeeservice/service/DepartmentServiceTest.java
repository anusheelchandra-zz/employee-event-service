package employeeservice.service;

import employeeservice.entity.Department;
import employeeservice.exception.EntityAlreadyExistsException;
import employeeservice.repository.DepartmentRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class DepartmentServiceTest {

  @Mock private DepartmentRepository departmentRepository;
  private DepartmentService departmentService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    departmentService = new DepartmentService(departmentRepository);

    Mockito.when(departmentRepository.save(ArgumentMatchers.any(Department.class)))
        .thenReturn(Department.builder().id(1L).name("finance").build());
  }

  @Test
  public void shouldGetDepartmentByName() {
    Mockito.when(departmentRepository.findByName(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(Department.builder().id(1L).name("finance").build()));
    var department = departmentService.getDepartmentByName("finance");
    Assertions.assertThat(department).isPresent();
    Assertions.assertThat(department.get().getId()).isNotNull();
    Assertions.assertThat(department.get().getName()).isEqualTo("finance");
  }

  @Test
  public void shouldNotGetDepartmentByName() {
    Mockito.when(departmentRepository.findByName(ArgumentMatchers.anyString()))
        .thenReturn(Optional.empty());
    var department = departmentService.getDepartmentByName("test");
    Assertions.assertThat(department).isNotPresent();
  }

  @Test
  public void shouldCreateNewDepartment() {
    var departmentDTO = departmentService.createDepartment("finance");
    Assertions.assertThat(departmentDTO).isNotNull();
    Assertions.assertThat(departmentDTO.getName()).isEqualTo("finance");
  }

  @Test
  public void shouldThrowExceptionWhileCreatingNewDepartment() {
    Mockito.when(departmentRepository.findByName(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(Department.builder().id(1L).name("finance").build()));
    Assertions.assertThatThrownBy(() -> departmentService.createDepartment("finance"))
        .isInstanceOf(EntityAlreadyExistsException.class)
        .hasMessage("Department already exists with name: finance");
  }
}
