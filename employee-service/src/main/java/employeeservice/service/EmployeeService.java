package employeeservice.service;

import employeeservice.domain.Action;
import employeeservice.domain.EmployeeDTO;
import employeeservice.entity.Department;
import employeeservice.entity.Employee;
import employeeservice.exception.EntityAlreadyExistsException;
import employeeservice.exception.EntityNotFoundException;
import employeeservice.exception.UpdateNotPossibleException;
import employeeservice.mapper.EmployeeMapper;
import employeeservice.publisher.EventPublisher;
import employeeservice.repository.EmployeeRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentService departmentService;
  private final EventPublisher eventPublisher;

  public EmployeeDTO getEmployeeById(String uuid) {
    return EmployeeMapper.toEmployeeDTO(
        employeeRepository
            .findById(uuid)
            .orElseThrow(() -> buildEntityNotFoundException("Employee", uuid)));
  }

  public EmployeeDTO updateEmployee(String uuid, EmployeeDTO employeeDTO) {
    var employeeById = employeeRepository.findById(uuid);
    return employeeById
        .map(persistedEmployee -> updateExistingEmployee(employeeDTO, persistedEmployee))
        .orElseThrow(() -> buildEntityNotFoundException("Employee", uuid));
  }

  public void deleteEmployeeById(String uuid) {
    if (employeeRepository.existsById(uuid)) {
      employeeRepository.deleteById(uuid);
      eventPublisher.publishEvent(uuid, Action.DELETE);
    } else throw buildEntityNotFoundException("Employee", uuid);
  }

  public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
    var employeeByEmail = employeeRepository.findByEmail(employeeDTO.getEmail());
    employeeByEmail.ifPresent(
        employee -> {
          throw new EntityAlreadyExistsException(
              String.format("Employee already exists with email: %s", employee.getEmail()));
        });
    return createNewEmployee(employeeDTO);
  }

  private EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO) {
    var departmentByName = departmentService.getDepartmentByName(employeeDTO.getDepartment());
    return departmentByName
        .map(department -> createNewEmployee(employeeDTO, department))
        .orElseThrow(() -> buildEntityNotFoundException("Department", employeeDTO.getDepartment()));
  }

  private EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO, Department department) {
    var employee = EmployeeMapper.toEmployee(employeeDTO, department);
    var savedEmployeeDTO = EmployeeMapper.toEmployeeDTO(employeeRepository.save(employee));
    eventPublisher.publishEvent(savedEmployeeDTO.getUuid(), Action.CREATE);
    return savedEmployeeDTO;
  }

  private EmployeeDTO updateExistingEmployee(EmployeeDTO employeeDTO, Employee persistedEmployee) {
    validateEmailConstraint(persistedEmployee.getEmail(), employeeDTO.getEmail());
    var departmentByName = departmentService.getDepartmentByName(employeeDTO.getDepartment());
    return departmentByName
        .map(department -> updateEmployee(employeeDTO, persistedEmployee, department))
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format(
                        "Department not found with name: %s", employeeDTO.getDepartment())));
  }

  private void validateEmailConstraint(String existingEmail, String newEmail) {
    if (!existingEmail.equalsIgnoreCase(newEmail))
      throw new UpdateNotPossibleException(
          String.format(
              "Update not possible on email from: %s to new email: %s", existingEmail, newEmail));
  }

  private EmployeeDTO updateEmployee(
      EmployeeDTO employeeDTO, Employee persistedEmployee, Department department) {
    persistedEmployee.setFirstName(employeeDTO.getFirstName().toLowerCase());
    persistedEmployee.setLastName(employeeDTO.getLastName().toLowerCase());
    persistedEmployee.setDateOfBirth(LocalDate.parse(employeeDTO.getDateOfBirth()));
    persistedEmployee.setDepartment(department);

    var updatedEmployeeDTO =
        EmployeeMapper.toEmployeeDTO(employeeRepository.save(persistedEmployee));
    eventPublisher.publishEvent(updatedEmployeeDTO.getUuid(), Action.UPDATE);
    return updatedEmployeeDTO;
  }

  private EntityNotFoundException buildEntityNotFoundException(
      String entityName, String identifier) {
    return new EntityNotFoundException(
        String.format("%s not found with identifier : %s", entityName, identifier));
  }
}
