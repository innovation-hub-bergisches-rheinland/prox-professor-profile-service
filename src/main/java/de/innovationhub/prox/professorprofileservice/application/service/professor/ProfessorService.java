package de.innovationhub.prox.professorprofileservice.application.service.professor;

import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProfessorService {

  private final ProfessorRepository professorRepository;

  public ProfessorService(ProfessorRepository professorRepository) {
    this.professorRepository = professorRepository;
  }

  public Iterable<Professor> getAllProfessors() {
    return this.professorRepository.findAll();
  }

  public Optional<Professor> getProfessor(UUID uuid) {
    return this.professorRepository.findById(uuid);
  }

  public Professor saveProfessor(Professor professor) {
    return this.professorRepository.save(professor);
  }

  public Professor updateProfessor(Professor professor) {
    return this.professorRepository.save(professor);
  }

  public boolean professorExists(Professor professor) {
    return this.professorRepository.existsById(professor.getId());
  }
}
