package de.innovationhub.prox.professorprofileservice.config;

import de.innovationhub.prox.professorprofileservice.professor.Faculty;
import de.innovationhub.prox.professorprofileservice.professor.FacultyRepository;
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

  private final FacultyRepository facultyRepository;
  private final Resource resource;

  @Autowired
  public FacultyImport(
      FacultyRepository facultyRepository,
      @Value("classpath:/assets/faculties.yml") Resource resource) {
    this.facultyRepository = facultyRepository;
    this.resource = resource;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void importFaculties() {
    Yaml yaml = new Yaml();
    try {
      List<LinkedHashMap<String, String>> facultyList = yaml.load(resource.getInputStream());
      facultyList.forEach(
          faculty -> {
            if (!facultyRepository.existsByAbbreviationEqualsAndNameEquals(
                faculty.get("abbreviation"), faculty.get("name"))) {
              facultyRepository.save(new Faculty(faculty.get("abbreviation"), faculty.get("name")));
            }
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(facultyRepository.findAll());
  }
}
