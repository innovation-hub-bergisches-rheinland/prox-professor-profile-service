package de.innovationhub.prox.professorprofileservice.application.controller.faculty;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
    MockitoAnnotations.initMocks(FacultyControllerTest.class);
    when(facultyService.getFaculty(faculty.getId())).thenReturn(Optional.of(faculty));
    when(facultyService.getAllFaculties()).thenReturn(Collections.singletonList(faculty));
  }

  @Test
  void getAllFaculties() throws Exception {
    mockMvc
        .perform(get(FACULTY_PATH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$._embedded.facultyList[0].id", Matchers.is(faculty.getId().toString())))
        .andExpect(
            jsonPath(
                "$._embedded.facultyList[0].abbreviation", Matchers.is(faculty.getAbbreviation())))
        .andExpect(jsonPath("$._embedded.facultyList[0].name", Matchers.is(faculty.getName())))
        .andExpect(jsonPath("$._embedded.facultyList[0]._links.self").exists())
        .andExpect(jsonPath("$._embedded.facultyList[0]._links.self.href").exists())
        .andExpect(jsonPath("$._embedded.facultyList[0]._links.faculty").exists())
        .andExpect(jsonPath("$._embedded.facultyList[0]._links.faculty.href").exists())
        .andExpect(jsonPath("$._links.self").exists())
        .andExpect(jsonPath("$._links.self.href").exists());
  }

  @Test
  void getFaculty() throws Exception {
    mockMvc
        .perform(get(FACULTY_ID_PATH, faculty.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", Matchers.is(faculty.getId().toString())))
        .andExpect(jsonPath("$.abbreviation", Matchers.is(faculty.getAbbreviation())))
        .andExpect(jsonPath("$.name", Matchers.is(faculty.getName())))
        .andExpect(jsonPath("$._links.self").exists())
        .andExpect(jsonPath("$._links.self.href").exists())
        .andExpect(jsonPath("$._links.faculty").exists())
        .andExpect(jsonPath("$._links.faculty.href").exists());
  }

  @Test
  void getNotExistentFaculty() throws Exception {
    mockMvc
        .perform(get(FACULTY_ID_PATH, UUID.randomUUID()))
        .andDo(print())
        .andExpect(status().isNotFound());
  }
}
