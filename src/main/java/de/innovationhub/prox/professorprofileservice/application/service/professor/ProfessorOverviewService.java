package de.innovationhub.prox.professorprofileservice.application.service.professor;


import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfessorOverviewService {

  private final ProfessorOverviewRepository professorOverviewRepository;

  public ProfessorOverviewService(ProfessorOverviewRepository professorOverviewRepository) {
    this.professorOverviewRepository = professorOverviewRepository;
  }

  public Iterable<ProfessorOverviewDto> getProfessorOverview() {
    return this.professorOverviewRepository.getOverviewFromAllProfessors();
  }
}
