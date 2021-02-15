package de.innovationhub.prox.professorprofileservice.application.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.innovationhub.prox.professorprofileservice.application.net.client.ProjectServiceClient;
import de.innovationhub.prox.professorprofileservice.application.net.schema.ProjectStats;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.ContactInformation;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImage;
import de.innovationhub.prox.professorprofileservice.domain.professor.Publication;
import de.innovationhub.prox.professorprofileservice.domain.professor.ResearchSubject;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {ProfessorOverviewRepositoryImpl.class})
public class ProfessorOverviewRepositoryImplTest {

  @MockBean ProfessorService professorService;

  @MockBean ProjectServiceClient projectServiceClient;

  @Autowired ProfessorOverviewRepositoryImpl professorOverviewRepository;

  ProjectStats projectStats;
  Professor professor;

  @BeforeEach
  void setUp() {
    this.projectStats = new ProjectStats();
    this.projectStats.setSumAvailableProjects(12);
    this.projectStats.setSumFinishedProjects(24);
    this.projectStats.setSumRunningProjects(36);
    this.professor =
        new Professor(
            UUID.randomUUID(),
            "Prof. Dr. Xavier Tester",
            "2010",
            "IoT",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(
                "2.100",
                "Mo. 18-20 Uhr",
                "example@example.org",
                "+12 3456 789",
                "http://example.org",
                "http://example.org"),
            new ProfessorImage("test.123"),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
  }

  @Test
  void shouldReturnOverview() {
    when(projectServiceClient.findProjectStatsOfCreator(any(UUID.class)))
        .thenReturn(ResponseEntity.ok(this.projectStats));
    when(professorService.getAllProfessors(any(Sort.class)))
        .thenReturn(Collections.singletonList(professor));

    var overviewIterable = professorOverviewRepository.getOverviewFromAllProfessors();

    var overviewList =
        StreamSupport.stream(overviewIterable.spliterator(), false).collect(Collectors.toList());

    assertEquals(1, overviewList.size());
    var overview = overviewList.get(0);
    assertEquals(projectStats.getSumRunningProjects(), overview.getSumRunningProjects());
    assertEquals(projectStats.getSumFinishedProjects(), overview.getSumFinishedProjects());
    assertEquals(projectStats.getSumAvailableProjects(), overview.getSumAvailableProjects());
    assertArrayEquals(
        professor.getResearchSubjects().stream()
            .map(ResearchSubject::getSubject)
            .toArray(String[]::new),
        overview.getResearchSubjects());
    assertEquals(professor.getFaculty().getId(), overview.getFacultyId());
    assertEquals(professor.getFaculty().getName(), overview.getFaculty());
    assertEquals(professor.getId(), overview.getId());
    assertEquals(professor.getName(), overview.getName());
  }

  @Test
  void whenClientThrowsExceptionShouldReturnDefaultSum() {
    when(projectServiceClient.findProjectStatsOfCreator(any(UUID.class)))
        .thenThrow(RuntimeException.class);
    when(professorService.getAllProfessors(any(Sort.class)))
        .thenReturn(Collections.singletonList(professor));

    var overviewIterable = professorOverviewRepository.getOverviewFromAllProfessors();

    var overviewList =
        StreamSupport.stream(overviewIterable.spliterator(), false).collect(Collectors.toList());

    assertEquals(1, overviewList.size());
    var overview = overviewList.get(0);
    assertEquals(0, overview.getSumRunningProjects());
    assertEquals(0, overview.getSumFinishedProjects());
    assertEquals(0, overview.getSumAvailableProjects());
  }

  @Test
  void whenClientReturnsInvalidReturnDefaultSum() {
    when(projectServiceClient.findProjectStatsOfCreator(any(UUID.class)))
        .thenReturn(ResponseEntity.badRequest().body(null));
    when(professorService.getAllProfessors(any(Sort.class)))
        .thenReturn(Collections.singletonList(professor));

    var overviewIterable = professorOverviewRepository.getOverviewFromAllProfessors();

    var overviewList =
        StreamSupport.stream(overviewIterable.spliterator(), false).collect(Collectors.toList());

    assertEquals(1, overviewList.size());
    var overview = overviewList.get(0);
    assertEquals(0, overview.getSumRunningProjects());
    assertEquals(0, overview.getSumFinishedProjects());
    assertEquals(0, overview.getSumAvailableProjects());
  }
}
