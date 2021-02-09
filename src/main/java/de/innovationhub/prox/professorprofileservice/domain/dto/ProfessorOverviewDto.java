package de.innovationhub.prox.professorprofileservice.domain.dto;

import java.util.UUID;
import lombok.Value;

@Value
public class ProfessorOverviewDto {
  UUID id, facultyId;
  String name, faculty, mainSubject;
  String[] researchSubjects;
  int sumRunningProjects, sumFinishedProjects, sumAvailableProjects;
}
