package de.innovationhub.prox.professorprofileservice.application.controller.faculty;

import de.innovationhub.prox.professorprofileservice.application.exception.ApiError;
import de.innovationhub.prox.professorprofileservice.application.exception.faculty.FacultyNotFoundException;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

  @ExceptionHandler({IllegalArgumentException.class, NumberFormatException.class})
  public ResponseEntity<ApiError> numberFormatException() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ApiError(
                HttpStatus.BAD_REQUEST.value(), "Invalid Faculty ID", "Received invalid UUID"));
  }

  @GetMapping(value = "/faculties", produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<CollectionModel<EntityModel<Faculty>>> getAllFaculties(Sort sort) {
    var collectionModel =
        facultyRepresentationModelAssembler.toCollectionModel(facultyService.getAllFaculties());
    return ResponseEntity.ok(collectionModel);
  }

  @GetMapping(value = "faculties/{id}", produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable("id") UUID id) {
    var faculty = facultyService.getFaculty(id).orElseThrow(FacultyNotFoundException::new);
    return ResponseEntity.ok(facultyRepresentationModelAssembler.toModel(faculty));
  }

  @PostMapping(
      value = "faculties",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<EntityModel<Faculty>> saveFaculty(@RequestBody Faculty faculty) {
    var savedFaculty = facultyService.saveFacultyIfNotExists(faculty);
    return savedFaculty
        .map(value -> ResponseEntity.ok(facultyRepresentationModelAssembler.toModel(value)))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }
}
