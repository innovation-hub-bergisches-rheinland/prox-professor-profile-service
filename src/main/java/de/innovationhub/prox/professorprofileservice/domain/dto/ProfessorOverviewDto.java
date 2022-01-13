package de.innovationhub.prox.professorprofileservice.domain.dto;


import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class ProfessorOverviewDto {
  @NotNull UUID id;
  UUID facultyId;
  @NotBlank String name;
  String faculty;
  String mainSubject;
  String[] researchSubjects;

  @NotNull
  @Min(0)
  int sumRunningProjects;

  @NotNull
  @Min(0)
  int sumFinishedProjects;

  @NotNull
  @Min(0)
  int sumAvailableProjects;
}
