package de.innovationhub.prox.professorprofileservice.application.config;

import de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class FacultyImport {

  private final FacultyService facultyService;
  private final Resource resource;

  @Autowired
  public FacultyImport(
      FacultyService facultyService, @Value("classpath:/assets/faculties.yml") Resource resource) {
    this.resource = resource;
    this.facultyService = facultyService;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void importFaculties() {
    Yaml yaml = new Yaml();
    try {
      List<LinkedHashMap<String, String>> facultyList = yaml.load(resource.getInputStream());
      facultyList.forEach(
          faculty ->
              this.facultyService.saveFacultyIfNotExists(
                  new Faculty(faculty.get("abbreviation"), faculty.get("name"))));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
