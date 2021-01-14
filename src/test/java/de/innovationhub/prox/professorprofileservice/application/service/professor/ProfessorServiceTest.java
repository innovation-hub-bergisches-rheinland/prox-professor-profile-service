package de.innovationhub.prox.professorprofileservice.application.service.professor;

import static org.junit.jupiter.api.Assertions.*;

import de.innovationhub.prox.professorprofileservice.application.config.ModelMapperConfig;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.faculty.FacultyRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.ContactInformation;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.Publication;
import de.innovationhub.prox.professorprofileservice.domain.professor.ResearchSubject;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ProfessorService.class, ModelMapperConfig.class})
class ProfessorServiceTest {

  @Autowired ProfessorService professorService;

  @Autowired ProfessorRepository professorRepository;

  @Autowired FacultyRepository facultyRepository;

  private Professor professor = null;

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.initMocks(this.getClass());
    this.professor =
        new Professor(
            UUID.randomUUID(),
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            "test123.png",
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    facultyRepository.save(professor.getFaculty());
  }

  @Test
  void should_get_all_professors() throws Exception {
    professorRepository.save(professor);

    var professorIterable = professorService.getAllProfessors();
    var list =
        StreamSupport.stream(professorIterable.spliterator(), false).collect(Collectors.toList());
    assertFalse(list.isEmpty());
    assertTrue(list.contains(professor));
  }

  @Test
  void should_get_professor() throws Exception {
    professorRepository.save(professor);

    var optProfessor = professorService.getProfessor(professor.getId());
    assertTrue(optProfessor.isPresent());
    assertEquals(professor, optProfessor.get());
  }

  @Test
  void should_save_professor() throws Exception {
    professorService.saveProfessor(professor);

    var optProfessor = professorRepository.findById(professor.getId());
    assertTrue(optProfessor.isPresent());
    assertEquals(professor, optProfessor.get());
  }

  @Test
  void should_update_professor_when_exists() throws Exception {
    professorRepository.save(professor);

    professor.setName("New Name");

    var optProfessor = professorService.updateProfessor(professor.getId(), professor);
    assertTrue(optProfessor.isPresent());
    assertEquals(professor, optProfessor.get());
  }

  @Test
  void should_not_update_professor_when_not_exists() throws Exception {
    professor.setName("New Name");

    var optProfessor = professorService.updateProfessor(professor.getId(), professor);
    assertTrue(optProfessor.isEmpty());
  }

  /*@Test
  void should_get_profile_image() throws Exception {
    professorRepository.save(professor);

    var optImage = professorService.getProfessorImage(professor.getId());
    assertTrue(optImage.isPresent());
    assertTrue(Arrays.equals(professor.getProfileImage().getData(), optImage.get()));
  }*/

  /*@Test
  void should_get_default_image_when_image_not_exists() throws Exception {
    professor.setFilename(null);
    professorRepository.save(professor);

    var inputStream = this.getClass().getResourceAsStream("/img/blank-profile-picture.png");

    var optImage = professorService.getProfessorImage(professor.getId());
    assertTrue(optImage.isPresent());
    assertTrue(Arrays.equals(IOUtils.toByteArray(inputStream), optImage.get()));
  }*/

  @Test
  void should_get_no_image_when_professor_not_exists() throws Exception {
    var optImage = professorService.getProfessorImage(UUID.randomUUID());
    assertTrue(optImage.isEmpty());
  }

  /*@Test
  void should_save_image() throws Exception {
    professorRepository.save(professor);

    var inputStream = this.getClass().getResourceAsStream("/img/blank-profile-picture.png");

    var optImage =
        professorService.saveProfessorImage(professor.getId(), IOUtils.toByteArray(inputStream));

    assertTrue(optImage.isPresent());
    var savedProf = professorRepository.findById(professor.getId());
    assertTrue(savedProf.isPresent());
    assertNotNull(savedProf.get().getProfileImage());
    assertTrue(savedProf.get().getProfileImage().getData().length > 0);
  }*/

  @Test
  void should_not_save_image_when_professor_not_exists() throws Exception {
    var inputStream = this.getClass().getResourceAsStream("/img/blank-profile-picture.png");

    var optImage =
        professorService.saveProfessorImage(professor.getId(), IOUtils.toByteArray(inputStream));

    assertTrue(optImage.isEmpty());
    assertEquals(0, professorRepository.count());
  }
}
