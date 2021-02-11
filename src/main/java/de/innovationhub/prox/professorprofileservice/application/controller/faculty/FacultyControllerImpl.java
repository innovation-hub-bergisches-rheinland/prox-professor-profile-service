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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FacultyControllerImpl implements FacultyController {

  private final FacultyService facultyService;
  private final FacultyRepresentationModelAssembler facultyRepresentationModelAssembler;

  @Autowired
  public FacultyControllerImpl(
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

  public ResponseEntity<CollectionModel<EntityModel<Faculty>>> getAllFaculties(Sort sort) {
    var collectionModel =
        facultyRepresentationModelAssembler.toCollectionModel(facultyService.getAllFaculties(sort));
    return ResponseEntity.ok(collectionModel);
  }

  public ResponseEntity<EntityModel<Faculty>> getFaculty(UUID id) {
    var faculty = facultyService.getFaculty(id).orElseThrow(FacultyNotFoundException::new);
    return ResponseEntity.ok(facultyRepresentationModelAssembler.toModel(faculty));
  }
}
