package de.innovationhub.prox.professorprofileservice.application.service.faculty;

import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

/**
 * Service which handles importing faculties into Database Currently the service on startup loads an
 * YAML-File providing the faculty data located under {@code /assets/faculties.yml} and stores the
 * faculties which do not exist yet in the database
 */
@Service
public class FacultyImportService {

  private final FacultyService facultyService;
  private final Resource resource;

  @Autowired
  public FacultyImportService(
      FacultyService facultyService, @Value("classpath:/assets/faculties.yml") Resource resource) {
    this.resource = resource;
    this.facultyService = facultyService;
  }

  /**
   * The importFaculties Method will run at application startup after the components are
   * instantiated and imports the Faculties as described in {@link
   * de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyImportService}
   */
  // TODO refactor
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
