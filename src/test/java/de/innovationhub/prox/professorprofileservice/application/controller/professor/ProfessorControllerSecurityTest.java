package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innovationhub.prox.professorprofileservice.application.config.KeycloakConfig;
import de.innovationhub.prox.professorprofileservice.application.config.SecurityConfig;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.security.AuthenticationUtils;
import de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyService;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.ContactInformation;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImage;
import de.innovationhub.prox.professorprofileservice.domain.professor.Publication;
import de.innovationhub.prox.professorprofileservice.domain.professor.ResearchSubject;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProfessorController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({
  ProfessorRepresentationModelAssembler.class,
  FacultyRepresentationModelAssembler.class,
  SecurityConfig.class,
  KeycloakConfig.class,
  KeycloakAutoConfiguration.class,
})
class ProfessorControllerSecurityTest {
  private static final String PROFESSORS_URL = "/professors";
  private static final String PROFESSORS_ID_URL = "/professors/{id}";
  private static final String PROFESSORS_ID_IMAGE_URL = "/professors/{id}/image";
  private static final String PROFESSOR_ID_FACULTY_URL = "/professors/{id}/faculty";

  @Autowired MockMvc mockMvc;

  @MockBean FacultyService facultyService;

  @MockBean ProfessorService professorService;
  @MockBean AuthenticationUtils authenticationUtils;

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
  @WithMockUser(roles = {})
  void shouldNotAllowPostProfessor() throws Exception {
    mockMvc
        .perform(
            post(PROFESSORS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(this.professor)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "professor")
  void shouldAllowPostProfessor() throws Exception {
    when(professorService.saveProfessor(any())).thenReturn(this.professor);
    String json = new ObjectMapper().writeValueAsString(this.professor);
    mockMvc
        .perform(
            post(PROFESSORS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser(roles = "professor")
  void shouldNotAllowUpdateProfessor() throws Exception {
    when(professorService.updateProfessor(any(), any())).thenReturn(Optional.of(this.professor));
    when(authenticationUtils.compareUserIdAndRequestId(any(), any())).thenReturn(false);

    String json = new ObjectMapper().writeValueAsString(this.professor);
    mockMvc
        .perform(
            put(PROFESSORS_ID_URL, this.professor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "professor")
  void shouldAllowUpdateProfessor() throws Exception {
    when(professorService.updateProfessor(any(), any())).thenReturn(Optional.of(this.professor));
    when(authenticationUtils.compareUserIdAndRequestId(any(), any())).thenReturn(true);

    String json = new ObjectMapper().writeValueAsString(this.professor);
    mockMvc
        .perform(
            put(PROFESSORS_ID_URL, this.professor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .characterEncoding("utf-8"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "professor")
  void shouldNotAllowUpdateFaculty() throws Exception {
    when(professorService.getProfessor(any())).thenReturn(Optional.of(this.professor));
    when(facultyService.getFaculty(any())).thenReturn(Optional.of(new Faculty()));
    when(professorService.saveProfessor(any())).thenReturn(this.professor);
    when(authenticationUtils.compareUserIdAndRequestId(any(), any())).thenReturn(false);

    mockMvc
        .perform(
            put(PROFESSOR_ID_FACULTY_URL, professor.getId())
                .contentType(MediaType.TEXT_PLAIN)
                .content(UUID.randomUUID().toString())
                .characterEncoding("utf-8"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "professor")
  void shouldAllowUpdateFaculty() throws Exception {
    when(professorService.getProfessor(any())).thenReturn(Optional.of(this.professor));
    when(facultyService.getFaculty(any())).thenReturn(Optional.of(new Faculty()));
    when(professorService.saveProfessor(any())).thenReturn(this.professor);
    when(authenticationUtils.compareUserIdAndRequestId(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            put(PROFESSOR_ID_FACULTY_URL, professor.getId())
                .contentType(MediaType.TEXT_PLAIN)
                .content(UUID.randomUUID().toString())
                .characterEncoding("utf-8"))
        .andExpect(status().isOk());
  }

  // TODO not working
  /*@Test
  @WithMockUser(roles = "professor")
  void shouldNotAllowUpdateImage() throws Exception {
    when(authenticationUtils.compareUserIdAndRequestId(any(), any())).thenReturn(false);

    mockMvc.perform(multipart(PROFESSORS_ID_IMAGE_URL, professor.getId())
        .file(new MockMultipartFile("image", new byte[]{})))
        .andExpect(status().isForbidden());
  }*/

  @Test
  @WithMockUser(roles = "professor")
  void shouldAllowUpdateImage() throws Exception {
    when(authenticationUtils.compareUserIdAndRequestId(any(), any())).thenReturn(true);

    mockMvc
        .perform(
            multipart(PROFESSORS_ID_IMAGE_URL, professor.getId())
                .file(new MockMultipartFile("image", new byte[] {})))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "professor")
  void shouldNotAllowDeleteImage() throws Exception {
    when(authenticationUtils.compareUserIdAndRequestId(any(), any())).thenReturn(false);

    mockMvc
        .perform(delete(PROFESSORS_ID_IMAGE_URL, professor.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "professor")
  void shouldAllowDeleteImage() throws Exception {
    when(authenticationUtils.compareUserIdAndRequestId(any(), any())).thenReturn(true);

    mockMvc
        .perform(delete(PROFESSORS_ID_IMAGE_URL, professor.getId()))
        .andExpect(status().isNoContent());
  }
}
