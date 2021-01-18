package de.innovationhub.prox.professorprofileservice.domain.faculty;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyRepository extends PagingAndSortingRepository<Faculty, UUID> {
  boolean existsByAbbreviationEqualsAndNameEquals(String abbreviation, String name);

  boolean existsByAbbreviationEquals(String abbreviation);
}
