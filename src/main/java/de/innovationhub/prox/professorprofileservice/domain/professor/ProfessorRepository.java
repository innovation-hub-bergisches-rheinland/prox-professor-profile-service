package de.innovationhub.prox.professorprofileservice.domain.professor;

import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends PagingAndSortingRepository<Professor, UUID> {
  @Transactional
  Page<Professor> findAllByFaculty_Id(UUID facultyId, Pageable pageable);

  @Transactional
  Page<Professor> findAllByFaculty_IdAndNameIgnoreCase(
      UUID facultyId, String search, Pageable pageable);

  @Transactional
  Page<Professor> findAllByNameContainingIgnoreCase(String search, Pageable pageable);

  @Query("select p.id from Professor p where upper(p.name) like upper(concat('%', ?1, '%'))")
  Optional<UUID> findFirstIdByNameContainsIgnoreCase(String name);
}
