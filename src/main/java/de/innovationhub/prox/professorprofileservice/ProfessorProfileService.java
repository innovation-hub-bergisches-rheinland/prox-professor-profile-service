package de.innovationhub.prox.professorprofileservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@EnableFeignClients
public class ProfessorProfileService {

  public static void main(String[] args) {
    SpringApplication.run(ProfessorProfileService.class, args);
  }
}
