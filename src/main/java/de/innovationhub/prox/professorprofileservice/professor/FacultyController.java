package de.innovationhub.prox.professorprofileservice.professor;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FacultyController {

  private final FacultyRepository facultyRepository;

  @Autowired
  public FacultyController(FacultyRepository facultyRepository) {
    this.facultyRepository = facultyRepository;
  }

  @GetMapping(value = "/faculties", produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<CollectionModel<Faculty>> getALlFaculties(Sort sort) {
    return ResponseEntity.ok(CollectionModel.of(facultyRepository.findAll(sort)));
  }

  @GetMapping(value = "faculties/{id}", produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable("id") UUID id)
      throws NotFoundException {
    return ResponseEntity.ok(
        EntityModel.of(facultyRepository.findById(id).orElseThrow(NotFoundException::new)));
  }
}
