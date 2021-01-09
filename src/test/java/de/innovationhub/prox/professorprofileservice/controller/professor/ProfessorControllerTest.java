package de.innovationhub.prox.professorprofileservice.controller.professor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.innovationhub.prox.professorprofileservice.config.KeycloakConfig;
import de.innovationhub.prox.professorprofileservice.config.SecurityConfig;
import de.innovationhub.prox.professorprofileservice.controller.ProfessorController;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.ContactInformation;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfileImage;
import de.innovationhub.prox.professorprofileservice.domain.professor.Publication;
import de.innovationhub.prox.professorprofileservice.domain.professor.ResearchSubject;
import de.innovationhub.prox.professorprofileservice.repository.FacultyRepository;
import de.innovationhub.prox.professorprofileservice.repository.ProfessorRepository;
import de.innovationhub.prox.professorprofileservice.util.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.util.ProfessorRepresentationModelAssembler;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.keycloak.common.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProfessorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
  SecurityConfig.class,
  KeycloakAutoConfiguration.class,
  KeycloakConfig.class,
  ProfessorRepresentationModelAssembler.class,
  FacultyRepresentationModelAssembler.class
})
class ProfessorControllerTest {

  private static final String PROFESSORS_URL = "/professors";
  private static final String PROFESSORS_ID_URL = "/professors/{id}";
  private static final String PROFESSORS_ID_IMAGE_URL = "/professors/{id}/image";
  private static final String PROFESSOR_ID_FACULTY_URL = "/professors/{id}/faculty";

  @Autowired MockMvc mockMvc;

  @Autowired ResourceLoader resourceLoader;

  @MockBean FacultyRepository facultyRepository;

  @MockBean ProfessorRepository professorRepository;

  @Test
  void getAllProfessors() throws Exception {
    var professor =
        new Professor(
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            new ProfileImage(),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    when(professorRepository.findAll()).thenReturn(Collections.singletonList(professor));

    mockMvc
        .perform(get(PROFESSORS_URL).accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.professorList.length()", Matchers.is(1)))
        .andExpect(
            jsonPath(
                "$._embedded.professorList[0].id",
                Matchers.is(
                    professor
                        .getId()
                        .toString()))) // Just match ID, this is not a serialization test
        .andExpect(
            jsonPath(
                "$._embedded.professorList[0]._links.professor.href",
                Matchers.any(String.class))) // Check whether a link is present, this is not a
        // resourceassembler test
        .andExpect(
            jsonPath("$._embedded.professorList[0]._links.self.href", Matchers.any(String.class)))
        .andExpect(
            jsonPath("$._embedded.professorList[0]._links.image.href", Matchers.any(String.class)))
        .andExpect(
            jsonPath(
                "$._embedded.professorList[0]._links.faculty.href", Matchers.any(String.class)))
        .andExpect(jsonPath("$._links.self.href", Matchers.any(String.class)));
  }

  @Test
  void getProfessor() throws Exception {
    var professor =
        new Professor(
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            new ProfileImage(),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    when(professorRepository.findById(any(UUID.class))).thenReturn(Optional.of(professor));

    mockMvc
        .perform(get(PROFESSORS_ID_URL, professor.getId()).accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath(
                "$.id",
                Matchers.is(
                    professor
                        .getId()
                        .toString()))) // Just match ID, this is not a serialization test
        .andExpect(
            jsonPath(
                "$._links.self.href",
                Matchers.any(String.class))) // Check whether a link is present, this is not a
        // resourceassembler test
        .andExpect(jsonPath("$._links.image.href", Matchers.any(String.class)))
        .andExpect(jsonPath("$._links.faculty.href", Matchers.any(String.class)));
  }

  @Test
  void getProfessorImage() throws Exception {
    var professor =
        new Professor(
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            new ProfileImage(
                Base64.decode(
                    "iVBORw0KGgoAAAANSUhEUgAAAAYAAAAGCAIAAABvrngfAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAeSURBVBhXYwCC////o5DofAhAF0XnQwC6KDqfgQEA+xE1y82ydeIAAAAASUVORK5CYII=")),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    when(professorRepository.findById(any(UUID.class))).thenReturn(Optional.of(professor));

    mockMvc
        .perform(get(PROFESSORS_ID_IMAGE_URL, professor.getId()).accept(MediaType.IMAGE_PNG_VALUE))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void postProfessorImage() throws Exception {
    var professor =
        new Professor(
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            new ProfileImage(),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    when(professorRepository.findById(any(UUID.class))).thenReturn(Optional.of(professor));

    mockMvc
        .perform(
            multipart(PROFESSORS_ID_IMAGE_URL, professor.getId())
                .file(
                    new MockMultipartFile(
                        "image",
                        resourceLoader
                            .getResource("classpath:/img/blank-profile-picture.png")
                            .getInputStream())))
        .andDo(print())
        .andExpect(status().isOk());

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    var image =
        ImageIO.read(
            resourceLoader
                .getResource("classpath:/img/blank-profile-picture.png")
                .getInputStream());
    ImageIO.write(image, "png", byteArrayOutputStream);

    assertArrayEquals(
        professorRepository.findById(professor.getId()).get().getProfileImage().getData(),
        byteArrayOutputStream.toByteArray());
  }

  @Test
  void getProfessorFaculty() throws Exception {
    var professor =
        new Professor(
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            new ProfileImage(),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    when(professorRepository.findById(any(UUID.class))).thenReturn(Optional.of(professor));

    Faculty faculty = professor.getFaculty();

    mockMvc
        .perform(get(PROFESSOR_ID_FACULTY_URL, professor.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", Matchers.is(faculty.getId().toString())))
        .andExpect(jsonPath("$.abbreviation", Matchers.is(faculty.getAbbreviation())))
        .andExpect(jsonPath("$.name", Matchers.is(faculty.getName())));
  }

  @Test
  void setProfessorFaculty() throws Exception {
    var professor =
        new Professor(
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            new ProfileImage(),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    Faculty faculty = new Faculty("F11", "Fakultät für Angewandte Naturwissenschaften");

    when(professorRepository.findById(any(UUID.class))).thenReturn(Optional.of(professor));
    when(facultyRepository.findById(any(UUID.class))).thenReturn(Optional.of(faculty));

    mockMvc
        .perform(
            put(PROFESSOR_ID_FACULTY_URL, professor.getId())
                .contentType("text/plain")
                .content(faculty.getId().toString())
                .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", Matchers.is(faculty.getId().toString())))
        .andExpect(jsonPath("$.abbreviation", Matchers.is(faculty.getAbbreviation())))
        .andExpect(jsonPath("$.name", Matchers.is(faculty.getName())));
  }
}
