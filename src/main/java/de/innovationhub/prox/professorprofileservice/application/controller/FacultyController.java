package de.innovationhub.prox.professorprofileservice.application.controller;

import de.innovationhub.prox.professorprofileservice.application.hatoeas.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class FacultyController {

  private final FacultyService facultyService;
  private final FacultyRepresentationModelAssembler facultyRepresentationModelAssembler;

  @Autowired
  public FacultyController(
      FacultyService facultyService,
      FacultyRepresentationModelAssembler facultyRepresentationModelAssembler) {
    this.facultyService = facultyService;
    this.facultyRepresentationModelAssembler = facultyRepresentationModelAssembler;
  }

  @GetMapping(value = "/faculties", produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<CollectionModel<EntityModel<Faculty>>> getALlFaculties(Sort sort) {
    var collectionModel =
        facultyRepresentationModelAssembler.toCollectionModel(facultyService.getAllFaculties());
    return ResponseEntity.ok(collectionModel);
  }

  @GetMapping(value = "faculties/{id}", produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable("id") UUID id) {
    var faculty =
        facultyService
            .getFaculty(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found"));
    return ResponseEntity.ok(facultyRepresentationModelAssembler.toModel(faculty));
  }
}
