package de.innovationhub.prox.professorprofileservice.domain.dto;

public interface ProfessorOverviewRepository {
  Iterable<ProfessorOverviewDto> getOverviewFromAllProfessors();
}
