package employeeservice.repository;

import employeeservice.entity.Employee;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, String> {

  Optional<Employee> findByEmail(String email);
}
