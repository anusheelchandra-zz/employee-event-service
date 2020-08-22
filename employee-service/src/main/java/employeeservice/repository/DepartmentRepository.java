package employeeservice.repository;

import employeeservice.entity.Department;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends CrudRepository<Department, Long> {

  Optional<Department> findByName(String name);
}
