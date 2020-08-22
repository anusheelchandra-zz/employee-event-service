package employeeservice.service;

import employeeservice.domain.DepartmentDTO;
import employeeservice.entity.Department;
import employeeservice.exception.EntityAlreadyExistsException;
import employeeservice.mapper.DepartmentMapper;
import employeeservice.repository.DepartmentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository repository;

  public DepartmentDTO createDepartment(String departmentName) {
    var departmentByName = getDepartmentByName(departmentName);
    departmentByName.ifPresent(
        department -> {
          throw new EntityAlreadyExistsException(
              String.format("Department already exists with name: %s", department.getName()));
        });
    return createAndGetDepartment(departmentName);
  }

  public Optional<Department> getDepartmentByName(String departmentName) {
    return repository.findByName(departmentName.toLowerCase());
  }

  private DepartmentDTO createAndGetDepartment(String departmentName) {
    var department =
        repository.save(Department.builder().name(departmentName.toLowerCase()).build());
    return DepartmentMapper.toDepartmentDTO(department);
  }
}
