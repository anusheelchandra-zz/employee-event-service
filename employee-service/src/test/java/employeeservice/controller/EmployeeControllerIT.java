package employeeservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import employeeservice.domain.EmployeeDTO;
import employeeservice.entity.Department;
import employeeservice.entity.Employee;
import employeeservice.exception.EntityAlreadyExistsException;
import employeeservice.exception.EntityNotFoundException;
import employeeservice.exception.UpdateNotPossibleException;
import employeeservice.exception.handler.ErrorResponse;
import employeeservice.mapper.EmployeeMapper;
import employeeservice.repository.DepartmentRepository;
import employeeservice.repository.EmployeeRepository;
import employeeservice.service.EmployeeService;
import employeeservice.util.TestUtil;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Disabled(
    "Manually execute with RabbitMQ running or started using : docker run -d --hostname my-rabbit --name my-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management")
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private DepartmentRepository departmentRepository;
  @Autowired private EmployeeRepository employeeRepository;
  @Autowired private EmployeeService employeeService;
  @Autowired private ObjectMapper objectMapper;

  private Employee persistedEmployee;
  private Department persistedDepartment;

  @BeforeEach
  public void setup() {
    persistedDepartment = departmentRepository.save(Department.builder().name("finance").build());
    var employee = TestUtil.buildEmployee();
    employee.setDepartment(persistedDepartment);
    persistedEmployee = employeeRepository.save(employee);
  }

  @Test
  public void shouldGetEmployeeByUuid() throws Exception {
    var uuid = persistedEmployee.getId();
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/employee/{uuid}", uuid)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    Assertions.assertThat(mvcResult.getResponse()).isNotNull();
    var employeeDTO =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDTO.class);
    Assertions.assertThat(employeeDTO.getUuid()).isEqualTo(uuid);
    Assertions.assertThat(employeeDTO.getDepartment()).isEqualTo(persistedDepartment.getName());
    Assertions.assertThat(employeeDTO.getEmail()).isEqualTo("first.last@mail.com");
    Assertions.assertThat(employeeDTO.getFirstName()).isEqualTo("first");
    Assertions.assertThat(employeeDTO.getLastName()).isEqualTo("last");
    Assertions.assertThat(employeeDTO.getDateOfBirth()).isEqualTo("2000-12-01");
  }

  @Test
  public void shouldThrowExceptionWhileGettingEmployeeWithUnknownUuid() throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/employee/{uuid}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult, EntityNotFoundException.class, HttpStatus.NOT_FOUND, "Entity not found");
  }

  @Test
  public void shouldThrowExceptionWhileTryingToUpdateEmployeeWithoutAuthentication()
      throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/employee/{uuid}", persistedEmployee.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    Assertions.assertThat(mvcResult.getResponse()).isNotNull();
    Assertions.assertThat(mvcResult.getResponse().getErrorMessage()).isNotNull();
    Assertions.assertThat(mvcResult.getResponse().getErrorMessage()).isEqualTo("Unauthorized");
  }

  @Test
  public void shouldThrowExceptionWhileTryingToDeleteEmployeeWithoutAuthentication()
      throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.delete("/employee/{uuid}", persistedEmployee.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    Assertions.assertThat(mvcResult.getResponse()).isNotNull();
    Assertions.assertThat(mvcResult.getResponse().getErrorMessage()).isNotNull();
    Assertions.assertThat(mvcResult.getResponse().getErrorMessage()).isEqualTo("Unauthorized");
  }

  @ParameterizedTest
  @ValueSource(strings = {"d881e278-9148", "123", " "})
  public void shouldThrowExceptionWhileGettingEmployeeWithInvalidUuid(String uuid)
      throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/employee/{uuid}", uuid)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult, ConstraintViolationException.class, HttpStatus.BAD_REQUEST, "Wrong input data");
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldDeleteEmployeeByUuid() throws Exception {
    var uuid = persistedEmployee.getId();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/employee/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
    Assertions.assertThat(employeeRepository.findById(uuid)).isNotPresent();
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileDeletingEmployeeByUuidWhenUuidIsUnknown() throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.delete("/employee/{uuid}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult, EntityNotFoundException.class, HttpStatus.NOT_FOUND, "Entity not found");
  }

  @ParameterizedTest
  @ValueSource(strings = {"d881e278-9148", "123", " "})
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileDeletingEmployeeByUuidWhenUuidIsInvalid(String uuid)
      throws Exception {
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.delete("/employee/{uuid}", uuid)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult, ConstraintViolationException.class, HttpStatus.BAD_REQUEST, "Wrong input data");
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldCreateNewEmployee() throws Exception {
    var employeeDTO =
        TestUtil.buildEmployeeDTO(
            "John", "Doe", "2000-12-01", "John.Doe@gmail.com", persistedDepartment.getName());
    var requestPayload = objectMapper.writeValueAsString(employeeDTO);
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/employee")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestPayload))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn();
    Assertions.assertThat(mvcResult.getResponse()).isNotNull();
    var response =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDTO.class);
    Assertions.assertThat(response.getUuid()).isNotNull();
    Assertions.assertThat(response.getDepartment()).isEqualTo(persistedDepartment.getName());
    Assertions.assertThat(response.getEmail()).isEqualTo("john.doe@gmail.com");
    Assertions.assertThat(response.getFirstName()).isEqualTo("john");
    Assertions.assertThat(response.getLastName()).isEqualTo("doe");
    Assertions.assertThat(response.getDateOfBirth()).isEqualTo("2000-12-01");
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileCreateNewEmployeeWithExistingEmail() throws Exception {
    var employeeDTO =
        TestUtil.buildEmployeeDTO(
            "John",
            "Doe",
            "2000-12-01",
            persistedEmployee.getEmail(),
            persistedDepartment.getName());
    var requestPayload = objectMapper.writeValueAsString(employeeDTO);
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/employee")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestPayload))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult,
        EntityAlreadyExistsException.class,
        HttpStatus.BAD_REQUEST,
        "Employee already exists with email: first.last@mail.com");
  }

  @ParameterizedTest
  @ValueSource(strings = {"John123", "John-", "123", "#@#@#", " "})
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileCreatingEmployeeWithInvalidFirstName(String firstName)
      throws Exception {
    var employeeDTO =
        TestUtil.buildEmployeeDTO(
            firstName, "Doe", "2000-12-01", "John.Doe@gmail.com", persistedDepartment.getName());
    testForInvalidInputEmployeeDTOAttribute(employeeDTO, "first name must only have alphabets");
  }

  @ParameterizedTest
  @ValueSource(strings = {"John123", "John-", "123", "#@#@#", " "})
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileCreatingEmployeeWithInvalidLastName(String lastName)
      throws Exception {
    var employeeDTO =
        TestUtil.buildEmployeeDTO(
            "John", lastName, "2000-12-01", "John.Doe@gmail.com", persistedDepartment.getName());
    testForInvalidInputEmployeeDTOAttribute(employeeDTO, "last name must only have alphabets");
  }

  @ParameterizedTest
  @ValueSource(strings = {"Finance123", "Finance-", "345", "#@#@#", " "})
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileCreatingEmployeeWithInvalidDepartment(String department)
      throws Exception {
    var employeeDTO =
        TestUtil.buildEmployeeDTO("John", "Doe", "2000-12-01", "John.Doe@gmail.com", department);
    testForInvalidInputEmployeeDTOAttribute(employeeDTO, "department must only have alphabets");
  }

  @ParameterizedTest
  @ValueSource(strings = {"2000-12-32", "20000-12-31", "2000-13-31", "date", "#@#@#", " "})
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileCreatingEmployeeWithInvalidDateOfBirth(String dateOfBirth)
      throws Exception {
    var employeeDTO =
        TestUtil.buildEmployeeDTO(
            "John", "Doe", dateOfBirth, "John.Doe@gmail.com", persistedDepartment.getName());
    testForInvalidInputEmployeeDTOAttribute(employeeDTO, "date must in yyyy-MM-dd format");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "email",
        "@example.com",
        "Joe Smith <email@example.com>",
        "email.example.com",
        "email@example@example.com",
        "email@example.com (Joe Smith)",
        "email@111.222.333.44444",
        " "
      })
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileCreatingEmployeeWithInvalidEmail(String email)
      throws Exception {
    var employeeDTO =
        TestUtil.buildEmployeeDTO(
            "John", "Doe", "2000-12-01", email, persistedDepartment.getName());
    testForInvalidInputEmployeeDTOAttribute(employeeDTO, "email must be valid as per regex");
  }

  @ParameterizedTest
  @ValueSource(strings = {"d881e278-9148", "123", " "})
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileUpdatingEmployeeWhenUuidIsInvalid(String uuid)
      throws Exception {
    var employeeDTO = EmployeeMapper.toEmployeeDTO(persistedEmployee);
    var requestPayload = objectMapper.writeValueAsString(employeeDTO);
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/employee/{uuid}", uuid)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestPayload))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult, ConstraintViolationException.class, HttpStatus.BAD_REQUEST, "Wrong input data");
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldUpdateExistingEmployeeForFirstName() throws Exception {
    var employeeDTO = EmployeeMapper.toEmployeeDTO(persistedEmployee);
    employeeDTO.setFirstName("newJohn");
    commonUpdateEmployeeDTOAsserts(employeeDTO);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldUpdateExistingEmployeeForLastName() throws Exception {
    var employeeDTO = EmployeeMapper.toEmployeeDTO(persistedEmployee);
    employeeDTO.setLastName("newLast");
    commonUpdateEmployeeDTOAsserts(employeeDTO);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldUpdateExistingEmployeeForDateOfBirth() throws Exception {
    var employeeDTO = EmployeeMapper.toEmployeeDTO(persistedEmployee);
    employeeDTO.setDateOfBirth("2000-12-12");
    commonUpdateEmployeeDTOAsserts(employeeDTO);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldUpdateExistingEmployeeForDepartment() throws Exception {
    var newDepartment = departmentRepository.save(Department.builder().name("marketing").build());
    var employeeDTO = EmployeeMapper.toEmployeeDTO(persistedEmployee);
    employeeDTO.setDepartment(newDepartment.getName());
    commonUpdateEmployeeDTOAsserts(employeeDTO);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
  public void shouldThrowExceptionWhileUpdateEmployeeForEmail() throws Exception {
    var employeeDTO = EmployeeMapper.toEmployeeDTO(persistedEmployee);
    employeeDTO.setEmail("new.mail@mail.com");
    var requestPayload = objectMapper.writeValueAsString(employeeDTO);
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/employee/{uuid}", persistedEmployee.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestPayload))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult,
        UpdateNotPossibleException.class,
        HttpStatus.BAD_REQUEST,
        "Update not possible for email");
  }

  @AfterEach
  public void tearDown() {
    employeeRepository.deleteAll();
    departmentRepository.deleteAll();
  }

  private void commonUpdateEmployeeDTOAsserts(EmployeeDTO employeeDTO) throws Exception {
    var requestPayload = objectMapper.writeValueAsString(employeeDTO);
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.put("/employee/{uuid}", persistedEmployee.getId())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestPayload))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    Assertions.assertThat(mvcResult.getResponse()).isNotNull();
    var response =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDTO.class);
    Assertions.assertThat(response.getUuid()).isEqualTo(persistedEmployee.getId());
    Assertions.assertThat(response.getDepartment()).isEqualTo(employeeDTO.getDepartment());
    Assertions.assertThat(response.getEmail()).isEqualTo(employeeDTO.getEmail().toLowerCase());
    Assertions.assertThat(response.getFirstName())
        .isEqualTo(employeeDTO.getFirstName().toLowerCase());
    Assertions.assertThat(response.getLastName())
        .isEqualTo(employeeDTO.getLastName().toLowerCase());
    Assertions.assertThat(response.getDateOfBirth()).isEqualTo(employeeDTO.getDateOfBirth());
  }

  private void testForInvalidInputEmployeeDTOAttribute(
      EmployeeDTO employeeDTO, String validationMessage) throws Exception {
    var requestPayload = objectMapper.writeValueAsString(employeeDTO);
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/employee")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestPayload))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult,
        MethodArgumentNotValidException.class,
        HttpStatus.BAD_REQUEST,
        "Wrong input data");
    Assertions.assertThat(mvcResult.getResolvedException()).isNotNull();
    Assertions.assertThat(mvcResult.getResolvedException().getMessage())
        .contains(validationMessage);
  }

  private void commonMvcResultAsserts(
      MvcResult mvcResult,
      Class<? extends Exception> exceptionClass,
      HttpStatus httpStatus,
      String message)
      throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
    Assertions.assertThat(mvcResult.getResponse()).isNotNull();
    var errorResponse =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
    Assertions.assertThat(errorResponse.getMessage()).isEqualTo(message);
    Assertions.assertThat(errorResponse.getTimestamp()).isNotNull();
    Assertions.assertThat(errorResponse.getErrorCode()).isEqualTo(httpStatus.value());
    Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(exceptionClass);
  }
}
