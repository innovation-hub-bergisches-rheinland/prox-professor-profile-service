package de.innovationhub.prox.professorprofileservice.application.controller.faculty;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.innovationhub.prox.professorprofileservice.application.config.KeycloakConfig;
import de.innovationhub.prox.professorprofileservice.application.config.SecurityConfig;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
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
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FacultyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
  SecurityConfig.class,
  KeycloakAutoConfiguration.class,
  KeycloakConfig.class,
  FacultyRepresentationModelAssembler.class
})
class FacultyControllerTest {

  private static final String FACULTY_PATH = "/faculties";
  private static final String FACULTY_ID_PATH = "/faculties/{id}";

  @Autowired MockMvc mockMvc;

  @MockBean FacultyService facultyService;

  Faculty faculty = new Faculty("F01", "Fakultaet für Tests und Qualitaetssicherung");

  @BeforeEach
  void setUp() {
    when(facultyService.getFaculty(faculty.getId())).thenReturn(Optional.of(faculty));
    when(facultyService.getAllFaculties(Sort.unsorted()))
        .thenReturn(Collections.singletonList(faculty));
  }

  @Test
  @DisplayName("CollectionModel should contain self link")
  void collectionModelShouldContainSelfLink() throws Exception {
    mockMvc
        .perform(get(FACULTY_PATH).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.self.href").exists());
  }

  @Test
  @DisplayName("EntityModel collection should contain self link")
  void entityModelCollectionShouldContainSelfLink() throws Exception {
    mockMvc
        .perform(get(FACULTY_PATH).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.facultyList[0]._links.self.href").exists());
  }

  @Test
  @DisplayName("EntityModel should contain self link")
  void entityModelShouldContainSelfLink() throws Exception {
    mockMvc
        .perform(get(FACULTY_ID_PATH, faculty.getId()).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.self.href").exists());
  }

  @Test
  @DisplayName("When ID not found then should return 404")
  void whenIdNotFoundThenShouldReturn404() throws Exception {
    mockMvc.perform(get(FACULTY_ID_PATH, UUID.randomUUID())).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("When ID invalid then should return error")
  void whenIdInvalidThenShouldReturnError() throws Exception {
    mockMvc
        .perform(get(FACULTY_ID_PATH, "abcdefghijklmnopqrstuvwxyz0123456789"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
        .andExpect(jsonPath("$.error").isNotEmpty())
        .andExpect(jsonPath("$.message").isNotEmpty());
  }

  @Test
  @DisplayName("Faculty should equal expected Faculty")
  void facultyResponseShouldEqualsExpectedFaculty() throws Exception {
    mockMvc
        .perform(get(FACULTY_ID_PATH, faculty.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", Matchers.is(faculty.getId().toString())))
        .andExpect(jsonPath("$.abbreviation", Matchers.is(faculty.getAbbreviation())))
        .andExpect(jsonPath("$.name", Matchers.is(faculty.getName())));
  }

  @Test
  @DisplayName("Faculty from collection should equals expected Faculty")
  void facultyResponseFromCollectionShouldEqualsExpectedFaculty() throws Exception {
    mockMvc
        .perform(get(FACULTY_PATH).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$._embedded.facultyList[0].id", Matchers.is(faculty.getId().toString())))
        .andExpect(
            jsonPath(
                "$._embedded.facultyList[0].abbreviation", Matchers.is(faculty.getAbbreviation())))
        .andExpect(jsonPath("$._embedded.facultyList[0].name", Matchers.is(faculty.getName())));
  }
}
