package de.innovationhub.prox.professorprofileservice.application.config;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Temporary Fix for <a href="https://www.cyberkendra.com/2022/03/springshell-rce-0-day-vulnerability.html">https://www.cyberkendra.com/2022/03/springshell-rce-0-day-vulnerability.html</a>}
 */
@ControllerAdvice
@Order(10000)
public class GlobalControllerAdvice {

  @InitBinder
  public void setAllowedFields(WebDataBinder dataBinder) {
    var clazzNames = new String[]{"class.*", "Class.*", "*.class.*", "*.Class.*"};

    dataBinder.setDisallowedFields(clazzNames);
  }
}
