package de.innovationhub.prox.professorprofileservice.application.service.faculty;

import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.faculty.FacultyRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class FacultyService {

  private final FacultyRepository facultyRepository;

  public FacultyService(FacultyRepository facultyRepository) {
    this.facultyRepository = facultyRepository;
  }

  public Iterable<Faculty> getAllFaculties(Sort sort) {
    return this.facultyRepository.findAll(sort);
  }

  public Optional<Faculty> getFaculty(UUID uuid) {
    return this.facultyRepository.findById(uuid);
  }

  public Optional<Faculty> saveFacultyIfNotExists(Faculty faculty) {
    if (!facultyRepository.existsByAbbreviationEqualsAndNameEquals(
        faculty.getAbbreviation(), faculty.getName())) {
      return Optional.of(facultyRepository.save(faculty));
    }
    return Optional.empty();
  }
}
