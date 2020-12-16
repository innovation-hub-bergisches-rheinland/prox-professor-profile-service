package de.innovationhub.prox.professorprofileservice.professor;

import java.util.UUID;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface FacultyRepository extends PagingAndSortingRepository<Faculty, UUID> {
  boolean existsByAbbreviationEqualsAndNameEquals(String abbreviation, String name);
}
