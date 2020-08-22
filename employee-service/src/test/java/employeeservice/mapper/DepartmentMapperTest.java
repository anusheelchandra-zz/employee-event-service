package employeeservice.mapper;

import employeeservice.entity.Department;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DepartmentMapperTest {

  @Test
  public void ShouldMapToDepartmentDTO() {
    var departmentDTO = DepartmentMapper.toDepartmentDTO(Department.builder().name("test").build());
    Assertions.assertThat(departmentDTO).isNotNull();
    Assertions.assertThat(departmentDTO.getName()).isEqualTo("test");
  }
}
