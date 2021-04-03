package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.innovationhub.prox.professorprofileservice.application.config.KeycloakConfig;
import de.innovationhub.prox.professorprofileservice.application.config.SecurityConfig;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorOverviewRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorOverviewService;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProfessorOverviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
  SecurityConfig.class,
  KeycloakAutoConfiguration.class,
  KeycloakConfig.class,
  ProfessorOverviewRepresentationModelAssembler.class
})
class ProfessorOverviewControllerTest {

  private static final String PROFESSOR_OVERVIEW_URL = "/professors/overview";

  @Autowired MockMvc mockMvc;

  @MockBean ProfessorOverviewService professorOverviewService;

  @MockBean ProfessorService professorService;

  ProfessorOverviewDto professorOverview;

  @BeforeEach
  void setUp() {
    this.professorOverview =
        new ProfessorOverviewDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Prof. Dr. Xavier Tester",
            "Fakultät für Informatik und Ingenieurwissenschaften",
            "IoT",
            new String[] {"IoT", "Mobile"},
            44,
            22,
            11);
  }

  @Test
  @DisplayName("CollectionModel should contain self link")
  void collectionModelShouldContainSelfLink() throws Exception {
    when(this.professorOverviewService.getProfessorOverview())
        .thenReturn(Collections.singletonList(this.professorOverview));

    mockMvc
        .perform(get(PROFESSOR_OVERVIEW_URL).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._links.self.href").exists());
  }

  @Test
  @DisplayName("EntityModel collection should contain self link")
  void entityModelCollectionShouldContainSelfLink() throws Exception {
    when(this.professorOverviewService.getProfessorOverview())
        .thenReturn(Collections.singletonList(this.professorOverview));

    mockMvc
        .perform(get(PROFESSOR_OVERVIEW_URL).accept(MediaTypes.HAL_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.professorOverviewDtoList[0]._links.self.href").exists());
  }

  @Test
  @DisplayName("Should return professorOverview")
  void shouldReturnProfessorOverview() throws Exception {
    when(this.professorOverviewService.getProfessorOverview())
        .thenReturn(Collections.singletonList(this.professorOverview));

    mockMvc
        .perform(get(PROFESSOR_OVERVIEW_URL))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath(
                "$._embedded.professorOverviewDtoList[0].id",
                is(this.professorOverview.getId().toString())))
        .andExpect(
            jsonPath(
                "$._embedded.professorOverviewDtoList[0].facultyId",
                is(this.professorOverview.getFacultyId().toString())))
        .andExpect(
            jsonPath(
                "$._embedded.professorOverviewDtoList[0].name",
                is(this.professorOverview.getName())))
        .andExpect(
            jsonPath(
                "$._embedded.professorOverviewDtoList[0].mainSubject",
                is(this.professorOverview.getMainSubject())))
        .andExpect(
            jsonPath(
                "$._embedded.professorOverviewDtoList[0].researchSubjects",
                containsInAnyOrder("IoT", "Mobile")))
        .andExpect(
            jsonPath(
                "$._embedded.professorOverviewDtoList[0].sumRunningProjects",
                is(this.professorOverview.getSumRunningProjects())))
        .andExpect(
            jsonPath(
                "$._embedded.professorOverviewDtoList[0].sumAvailableProjects",
                is(this.professorOverview.getSumAvailableProjects())))
        .andExpect(
            jsonPath(
                "$._embedded.professorOverviewDtoList[0].sumFinishedProjects",
                is(this.professorOverview.getSumFinishedProjects())));
  }
}
