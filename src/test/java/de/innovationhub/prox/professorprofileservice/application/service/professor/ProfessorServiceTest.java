package de.innovationhub.prox.professorprofileservice.application.service.professor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.professorprofileservice.application.exception.professor.ProfessorNotFoundException;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.faculty.FacultyRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.ContactInformation;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImage;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImageRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.Publication;
import de.innovationhub.prox.professorprofileservice.domain.professor.ResearchSubject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ProfessorService.class})
class ProfessorServiceTest {

  @MockBean ProfessorImageRepository professorImageRepository;

  @Autowired ProfessorService professorService;

  @Autowired ProfessorRepository professorRepository;

  @Autowired FacultyRepository facultyRepository;

  private Professor professor = null;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this.getClass());
    this.professor =
        new Professor(
            UUID.randomUUID(),
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            new ProfessorImage("test123.png"),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    facultyRepository.save(professor.getFaculty());
  }

  @Test
  void when_get_all_professors_then_get_all_professors() {
    this.professorRepository.save(this.professor);

    Iterable<Professor> iterable = this.professorService.getAllProfessors();
    List<Professor> list =
        StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());

    assertFalse(list.isEmpty());
    assertEquals(this.professor, list.get(0));
  }

  @Test
  void when_get_professor_then_get_professor() {
    this.professorRepository.save(this.professor);

    Optional<Professor> optionalProfessor =
        this.professorService.getProfessor(this.professor.getId());

    assertTrue(optionalProfessor.isPresent());
    assertEquals(this.professor, optionalProfessor.get());
  }

  @Test
  void when_save_professor_then_save_professors() {
    this.professorService.saveProfessor(this.professor);

    Optional<Professor> optionalProfessor =
        this.professorRepository.findById(this.professor.getId());

    assertTrue(optionalProfessor.isPresent());
    assertEquals(this.professor, optionalProfessor.get());
  }

  @Test
  void when_update_professor_and_exists_should_update_but_not_update_image() {
    this.professorRepository.save(this.professor);

    this.professor.setName("Test 123");
    var tmpImage = this.professor.getProfessorImage();
    this.professor.setProfessorImage(new ProfessorImage(null));

    this.professorService.updateProfessor(this.professor.getId(), this.professor);

    this.professor.setProfessorImage(tmpImage);

    Optional<Professor> optionalProfessor =
        this.professorRepository.findById(this.professor.getId());
    assertTrue(optionalProfessor.isPresent());
    assertEquals(this.professor, optionalProfessor.get());
  }

  @Test
  void when_update_professor_not_exists_should_return_empty() {
    this.professor.setName("Test 123");
    this.professor.setProfessorImage(new ProfessorImage(null));

    Optional<Professor> optionalProfessor =
        this.professorService.updateProfessor(this.professor.getId(), this.professor);

    assertTrue(optionalProfessor.isEmpty());
  }

  @Test
  void when_professor_image_present_then_return_image() {
    when(this.professorImageRepository.getProfessorImage(anyString()))
        .thenReturn(
            Optional.of(
                new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A}));
    this.professorRepository.save(this.professor);

    this.professorService.getProfessorImage(this.professor.getId());

    verify(this.professorImageRepository, times(1))
        .getProfessorImage(this.professor.getProfessorImage().getFilename());
  }

  @Test
  void when_professor_image_empty_then_return_default_image() {
    when(this.professorImageRepository.getProfessorImage(anyString())).thenReturn(Optional.empty());
    this.professorRepository.save(this.professor);

    this.professorService.getProfessorImage(this.professor.getId());

    verify(this.professorImageRepository, times(1)).getDefaultImage();
  }

  @Test
  void when_professor_not_exist_should_return_empty() {
    var result = this.professorService.getProfessorImage(this.professor.getId());
    assertTrue(result.isEmpty());
  }

  @Test
  void when_save_professor_image_should_be_saved() throws IOException {
    String filename = UUID.randomUUID() + ".png";
    when(this.professorImageRepository.saveProfessorImage(any(byte[].class))).thenReturn(filename);
    this.professorRepository.save(this.professor);

    var data = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A};

    this.professorService.saveProfessorImage(professor.getId(), data);

    verify(this.professorImageRepository, times(1)).saveProfessorImage(data);
    verify(this.professorImageRepository, times(1))
        .deleteProfessorImage(this.professor.getProfessorImage().getFilename());
    Optional<Professor> optionalProfessor =
        this.professorRepository.findById(this.professor.getId());
    assertTrue(optionalProfessor.isPresent());
    assertEquals(filename, optionalProfessor.get().getProfessorImage().getFilename());
  }

  @SuppressWarnings("java:S5778")
  @Test
  void when_save_professor_image_professor_not_exists_should_throw_exception() {
    var data = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A};

    assertThrows(
        ProfessorNotFoundException.class,
        () -> this.professorService.saveProfessorImage(professor.getId(), data));
  }

  @Test
  void when_delete_professor_image_professor_should_call_delete() throws IOException {
    this.professorRepository.save(this.professor);

    this.professorService.deleteProfessorImage(this.professor.getId());

    verify(this.professorImageRepository, times(1))
        .deleteProfessorImage(this.professor.getProfessorImage().getFilename());
  }

  @SuppressWarnings("java:S5778")
  @Test
  void when_delete_professor_image_professor_not_exists_should_throw_exception() {
    assertThrows(
        ProfessorNotFoundException.class,
        () -> this.professorService.deleteProfessorImage(this.professor.getId()));
  }

  @Test
  void when_exists_then_true() {
    this.professorRepository.save(this.professor);

    assertTrue(this.professorService.existsById(this.professor.getId()));
  }

  @Test
  void when_not_exists_then_false() {
    assertFalse(this.professorService.existsById(this.professor.getId()));
  }
}
