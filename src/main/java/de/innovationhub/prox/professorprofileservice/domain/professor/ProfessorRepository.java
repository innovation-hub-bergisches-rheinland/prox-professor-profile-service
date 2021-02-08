package de.innovationhub.prox.professorprofileservice.domain.professor;

import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends PagingAndSortingRepository<Professor, UUID> {
  @Transactional
  Set<Professor> findAllByFaculty_Id(UUID facultyId);
}
