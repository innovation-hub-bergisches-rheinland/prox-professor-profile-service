package de.innovationhub.prox.professorprofileservice.repository;

import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends PagingAndSortingRepository<Professor, UUID> {}
