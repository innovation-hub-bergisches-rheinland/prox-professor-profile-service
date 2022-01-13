package de.innovationhub.prox.professorprofileservice.application.net.schema;


import lombok.Data;

@Data
public class ProjectStats {
  int sumRunningProjects;
  int sumFinishedProjects;
  int sumAvailableProjects;
}
