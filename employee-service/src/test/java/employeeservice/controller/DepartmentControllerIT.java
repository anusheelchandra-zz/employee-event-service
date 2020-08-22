package employeeservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import employeeservice.domain.DepartmentDTO;
import employeeservice.domain.DepartmentRequestDTO;
import employeeservice.entity.Department;
import employeeservice.exception.EntityAlreadyExistsException;
import employeeservice.exception.handler.ErrorResponse;
import employeeservice.repository.DepartmentRepository;
import employeeservice.service.DepartmentService;
import java.io.UnsupportedEncodingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class DepartmentControllerIT {

  @Autowired private MockMvc mockMvc;
  @Autowired private DepartmentService departmentService;
  @Autowired private DepartmentRepository departmentRepository;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    departmentRepository.save(Department.builder().name("test").build());
  }

  @Test
  public void shouldCreateDepartment() throws Exception {
    var requestDTO = DepartmentRequestDTO.builder().name("finance").build();
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/department")
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn();
    var departmentDTO =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DepartmentDTO.class);
    Assertions.assertThat(departmentDTO.getName()).isEqualTo("finance");
  }

  @Test
  public void shouldThrowExceptionWhileCreatingAlreadyExistingDepartment() throws Exception {
    var requestDTO = DepartmentRequestDTO.builder().name("test").build();
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/department")
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    commonMvcResultAsserts(
        mvcResult, EntityAlreadyExistsException.class, "Department already exists with name: test");
  }

  @ParameterizedTest
  @ValueSource(strings = {"Finance123", "Finance-", "345", "#@#@#", " ", ""})
  public void shouldThrowExceptionWhenDepartmentIsInvalid(String department) throws Exception {
    var requestDTO = DepartmentRequestDTO.builder().name(department).build();
    var mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/department")
                    .content(objectMapper.writeValueAsString(requestDTO))
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    commonMvcResultAsserts(mvcResult, MethodArgumentNotValidException.class, "Wrong input data");
    Assertions.assertThat(mvcResult.getResolvedException()).isNotNull();
  }

  @AfterEach
  public void tearDown() {
    departmentRepository.deleteAll();
  }

  private void commonMvcResultAsserts(
      MvcResult mvcResult, Class<? extends Exception> exceptionClass, String message)
      throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
    Assertions.assertThat(mvcResult.getResponse()).isNotNull();
    var errorResponse =
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
    Assertions.assertThat(errorResponse.getMessage()).isEqualTo(message);
    Assertions.assertThat(errorResponse.getTimestamp()).isNotNull();
    Assertions.assertThat(errorResponse.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(exceptionClass);
  }
}
