package employeeservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/")
@ApiIgnore
public class SwaggerController {

  @GetMapping
  public ModelAndView viewSwaggerDocumentation() {
    return new ModelAndView("redirect:/swagger-ui.html");
  }
}
