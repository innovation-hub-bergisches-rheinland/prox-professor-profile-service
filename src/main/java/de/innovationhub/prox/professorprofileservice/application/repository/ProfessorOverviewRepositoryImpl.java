package de.innovationhub.prox.professorprofileservice.application.repository;


import de.innovationhub.prox.professorprofileservice.application.net.client.ProjectServiceClient;
import de.innovationhub.prox.professorprofileservice.application.net.schema.ProjectStats;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ResearchSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfessorOverviewRepositoryImpl implements ProfessorOverviewRepository {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final ProfessorService professorService;
  private final ProjectServiceClient projectServiceClient;

  @Autowired
  public ProfessorOverviewRepositoryImpl(
      final ProfessorService professorService, ProjectServiceClient projectServiceClient) {
    this.professorService = professorService;
    this.projectServiceClient = projectServiceClient;
  }

  @Override
  public Iterable<ProfessorOverviewDto> getOverviewFromAllProfessors() {
    var profList =
        StreamSupport.stream(
                professorService.getAllProfessors(Sort.unsorted()).spliterator(), false)
            .collect(Collectors.toList());

    List<ProfessorOverviewDto> overviewDtoList = new ArrayList<>();

    for (Professor prof : profList) {
      ResponseEntity<ProjectStats> response = null;
      try {
        response = projectServiceClient.findProjectStatsOfCreator(prof.getId());
      } catch (Exception e) {
        logger.error("Could not retrive stats from service", e);
      }

      var id = prof.getId();
      UUID facultyId = null;
      String facultyName = null;
      if (prof.getFaculty() != null) {
        facultyId = prof.getFaculty().getId();
        facultyName = prof.getFaculty().getName();
      }
      var name = prof.getName();
      var mainSubject = prof.getMainSubject();
      String[] researchSubjects =
          prof.getResearchSubjects().stream()
              .map(ResearchSubject::getSubject)
              .collect(Collectors.toList())
              .toArray(String[]::new);

      var sumRunningProjects = 0;
      var sumAvailableProjects = 0;
      var sumFinishedProjects = 0;
      if (response != null && response.getStatusCodeValue() == 200 && response.getBody() != null) {
        var stat = response.getBody();
        sumRunningProjects = stat.getSumRunningProjects();
        sumAvailableProjects = stat.getSumAvailableProjects();
        sumFinishedProjects = stat.getSumFinishedProjects();
      }

      var professorOverviewDto =
          new ProfessorOverviewDto(
              id,
              facultyId,
              name,
              facultyName,
              mainSubject,
              researchSubjects,
              sumRunningProjects,
              sumFinishedProjects,
              sumAvailableProjects);

      overviewDtoList.add(professorOverviewDto);
    }

    return overviewDtoList;
  }
}
