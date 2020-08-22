package employeeservice.mapper;

import employeeservice.domain.DepartmentDTO;
import employeeservice.entity.Department;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DepartmentMapper {

  public DepartmentDTO toDepartmentDTO(Department department) {
    return DepartmentDTO.builder().name(department.getName().toLowerCase()).build();
  }
}
