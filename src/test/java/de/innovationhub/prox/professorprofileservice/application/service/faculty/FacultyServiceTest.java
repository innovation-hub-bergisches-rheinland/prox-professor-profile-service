package de.innovationhub.prox.professorprofileservice.application.service.faculty;

import static org.junit.jupiter.api.Assertions.*;

import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.faculty.FacultyRepository;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(FacultyService.class)
class FacultyServiceTest {

  @Autowired FacultyService facultyService;
  @Autowired FacultyRepository facultyRepository;

  @Test
  void should_retrieve_faculties_when_exists() {
    var faculty = new Faculty("F98", "Fakultät für Tests und Qualitätssicherung");
    var faculty2 = new Faculty("F99", "Fakultät für Tests und Qualitätssicherung");
    facultyRepository.save(faculty);
    facultyRepository.save(faculty2);

    var result = facultyService.getAllFaculties();
    var list = StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());
    assertEquals(2, list.size());
    assertTrue(list.containsAll(Arrays.asList(faculty, faculty2)));
  }

  @Test
  void should_retrieve_faculty_when_exists() {
    var faculty = new Faculty("F99", "Fakultät für Tests und Qualitätssicherung");
    facultyRepository.save(faculty);

    var result = facultyService.getFaculty(faculty.getId());
    assertTrue(result.isPresent());
    assertEquals(faculty, result.get());
  }

  @Test
  void should_save_faculty_when_not_exists() {
    var faculty = new Faculty("F99", "Fakultät für Tests und Qualitätssicherung");
    var optFaculty = facultyService.saveFacultyIfNotExists(faculty);
    assertTrue(optFaculty.isPresent());
    assertEquals(faculty, optFaculty.get());

    var foundFaculty = facultyRepository.findById(faculty.getId());
    assertTrue(foundFaculty.isPresent());
    assertEquals(faculty, foundFaculty.get());
  }

  @Test
  void should_not_save_faculty_when_exists() {
    var faculty = new Faculty("F99", "Fakultät für Tests und Qualitätssicherung");
    facultyRepository.save(faculty);
    var optFaculty = facultyService.saveFacultyIfNotExists(faculty);
    assertTrue(optFaculty.isEmpty());
  }
}
