package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.innovationhub.prox.professorprofileservice.application.config.KeycloakConfig;
import de.innovationhub.prox.professorprofileservice.application.config.SecurityConfig;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyService;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.ContactInformation;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImage;
import de.innovationhub.prox.professorprofileservice.domain.professor.Publication;
import de.innovationhub.prox.professorprofileservice.domain.professor.ResearchSubject;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
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

  @MockBean FacultyService facultyService;

  @MockBean ProfessorService professorService;

  Professor professor;

  @BeforeEach
  void setUp() {
    this.professor =
        new Professor(
            UUID.randomUUID(),
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(),
            new ProfessorImage("123.png"),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
  }

  @Test
  @DisplayName("CollectionModel should contain self link")
  void collectionModelShouldContainSelfLink() throws Exception {
    when(this.professorService.getAllProfessors(any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.singletonList(professor)));
    when(this.professorService.getAllProfessors(Sort.unsorted()))
        .thenReturn(Collections.singletonList(professor));

    mockMvc
        .perform(get(PROFESSORS_URL).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.self.href").exists());
  }

  @Test
  @DisplayName("EntityModel collection should contain self link")
  void entityModelCollectionShouldContainSelfLink() throws Exception {
    when(this.professorService.getAllProfessors(any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.singletonList(professor)));
    when(this.professorService.getAllProfessors(Sort.unsorted()))
        .thenReturn(Collections.singletonList(professor));

    mockMvc
        .perform(get(PROFESSORS_URL).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.professorList[0]._links.self.href").exists());
  }

  @Test
  @DisplayName("EntityModel should contain self link")
  void entityModelShouldContainSelfLink() throws Exception {
    when(this.professorService.getProfessor(professor.getId())).thenReturn(Optional.of(professor));

    mockMvc
        .perform(get(PROFESSORS_ID_URL, this.professor.getId()).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.self.href").exists());
  }

  @Test
  @DisplayName("When Professor not found should return 404")
  void whenProfessorNotFoundShouldReturn404() throws Exception {
    mockMvc.perform(get(PROFESSORS_ID_URL, UUID.randomUUID())).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("When invalid UUID should return error")
  void whenInvalidUUIDShouldReturnError() throws Exception {
    mockMvc
        .perform(get(PROFESSORS_ID_URL, "abcdefghijklmno"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
        .andExpect(jsonPath("$.error").isNotEmpty())
        .andExpect(jsonPath("$.message").isNotEmpty());
  }

  @Test
  @DisplayName("Professor with faculty should contain faculty link")
  void shouldContainFacultyLink() throws Exception {
    when(this.professorService.getProfessor(this.professor.getId()))
        .thenReturn(Optional.of(this.professor));

    mockMvc
        .perform(get(PROFESSORS_ID_URL, this.professor.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.faculty.href").isNotEmpty());
  }

  @Test
  @DisplayName("Professor without faculty should not contain faculty link")
  void shouldNotContainFacultyLink() throws Exception {
    this.professor.setFaculty(null);
    when(this.professorService.getProfessor(this.professor.getId()))
        .thenReturn(Optional.of(this.professor));

    mockMvc
        .perform(get(PROFESSORS_ID_URL, this.professor.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.faculty").doesNotExist());
  }

  @Test
  @DisplayName("Professor with profile image should contain image link")
  void shouldContainImageLink() throws Exception {
    when(this.professorService.getProfessor(this.professor.getId()))
        .thenReturn(Optional.of(this.professor));

    mockMvc
        .perform(get(PROFESSORS_ID_URL, this.professor.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.image.href").isNotEmpty());
  }

  @Test
  @DisplayName("Professor without profile image should not contain image link")
  void shouldNotContainImageLink() throws Exception {
    this.professor.setProfessorImage(null);
    when(this.professorService.getProfessor(this.professor.getId()))
        .thenReturn(Optional.of(this.professor));

    mockMvc
        .perform(get(PROFESSORS_ID_URL, this.professor.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.image").doesNotExist());
  }

  @Test
  @DisplayName("Professor from collection should equal expected professor")
  void getAllProfessors() throws Exception {
    when(this.professorService.getAllProfessors(any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.singletonList(professor)));
    when(this.professorService.getAllProfessors(Sort.unsorted()))
        .thenReturn(Collections.singletonList(professor));

    mockMvc
        .perform(get(PROFESSORS_URL).accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.professorList.length()", is(1)))
        .andExpect(
            jsonPath(
                "$._embedded.professorList[0].id",
                is(
                    professor
                        .getId()
                        .toString()))) // Just match ID, this is not a serialization test
        .andExpect(
            jsonPath(
                "$._embedded.professorList[0]._links.professor.href",
                Matchers.any(String.class))) // Check whether a link is present, this is not a
        .andExpect(
            jsonPath("$._embedded.professorList[0]._links.image.href", Matchers.any(String.class)))
        .andExpect(
            jsonPath(
                "$._embedded.professorList[0]._links.faculty.href", Matchers.any(String.class)))
        .andExpect(jsonPath("$._links.self.href", Matchers.any(String.class)));
  }

  @Test
  @DisplayName("Professor should equal expected professor")
  void getProfessor() throws Exception {
    when(this.professorService.getProfessor(professor.getId())).thenReturn(Optional.of(professor));

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
                        .toString()))); // Just match ID, this is not a serialization test
  }

  @Test
  @DisplayName("Professor image should equal expected image")
  @SuppressWarnings("java:S2970")
  void getProfessorImage() throws Exception {
    var imageData =
        resourceLoader
            .getResource("classpath:/img/blank-profile-picture.png")
            .getInputStream()
            .readAllBytes();

    when(this.professorService.existsById(professor.getId())).thenReturn(true);
    when(this.professorService.getProfessorImage(professor.getId()))
        .thenReturn(Optional.of(imageData));

    mockMvc
        .perform(get(PROFESSORS_ID_IMAGE_URL, professor.getId()).accept(MediaType.IMAGE_PNG_VALUE))
        .andDo(print())
        .andExpect(status().isOk());

    verify(this.professorService).getProfessorImage(professor.getId());
  }

  @Test
  @DisplayName("Should save professor image")
  @SuppressWarnings("java:S2970")
  void postProfessorImage() throws Exception {
    var data =
        resourceLoader
            .getResource("classpath:/img/blank-profile-picture.png")
            .getInputStream()
            .readAllBytes();

    mockMvc
        .perform(
            multipart(PROFESSORS_ID_IMAGE_URL, professor.getId())
                .file(new MockMultipartFile("image", data)))
        .andDo(print())
        .andExpect(status().isOk());

    verify(this.professorService).saveProfessorImage(professor.getId(), data);
  }

  @Test
  @DisplayName("Should return professor faculty")
  void getProfessorFaculty() throws Exception {
    when(professorService.getProfessor(professor.getId())).thenReturn(Optional.of(professor));

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
  @DisplayName("Should set professor faculty")
  void setProfessorFaculty() throws Exception {
    Faculty faculty = new Faculty("F11", "Fakultät für Angewandte Naturwissenschaften");

    when(professorService.getProfessor(professor.getId())).thenReturn(Optional.of(professor));
    when(facultyService.getFaculty(faculty.getId())).thenReturn(Optional.of(faculty));

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

  @Test
  @DisplayName("Should delete professor image")
  void deleteProfessorImage() throws Exception {
    mockMvc
        .perform(delete(PROFESSORS_ID_IMAGE_URL, professor.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());
  }
}
