package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.application.exception.ApiError;
import de.innovationhub.prox.professorprofileservice.application.exception.faculty.FacultyNotFoundException;
import de.innovationhub.prox.professorprofileservice.application.exception.integrety.PathIdNotMatchingEntityIdException;
import de.innovationhub.prox.professorprofileservice.application.exception.professor.ProfessorNotFoundException;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyService;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProfessorController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ProfessorService professorService;
  private final FacultyService facultyService;
  private final ProfessorRepresentationModelAssembler professorRepresentationModelAssembler;
  private final FacultyRepresentationModelAssembler facultyRepresentationModelAssembler;

  @Autowired
  public ProfessorController(
      ProfessorService professorService,
      FacultyService facultyService,
      ProfessorRepresentationModelAssembler professorRepresentationModelAssembler,
      FacultyRepresentationModelAssembler facultyRepresentationModelAssembler) {
    this.professorService = professorService;
    this.facultyService = facultyService;
    this.professorRepresentationModelAssembler = professorRepresentationModelAssembler;
    this.facultyRepresentationModelAssembler = facultyRepresentationModelAssembler;
  }

  @ExceptionHandler({IllegalArgumentException.class, NumberFormatException.class})
  public ResponseEntity<ApiError> numberFormatException(Exception e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ApiError(
                HttpStatus.BAD_REQUEST.value(), "Invalid Professor ID", "Received invalid UUID"));
  }

  @GetMapping("/professors")
  public ResponseEntity<CollectionModel<EntityModel<Professor>>> getAllProfessors(
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string"))) Sort sort) {
    var collectionModel =
        this.professorRepresentationModelAssembler.toCollectionModel(
            this.professorService.getAllProfessors(sort));
    return ResponseEntity.ok(collectionModel);
  }

  @GetMapping("/professors/{id}")
  public ResponseEntity<EntityModel<Professor>> getProfessor(@PathVariable UUID id) {
    var professor =
        this.professorService.getProfessor(id).orElseThrow(ProfessorNotFoundException::new);
    return ResponseEntity.ok(this.professorRepresentationModelAssembler.toModel(professor));
  }

  @GetMapping(value = "/professors/{id}/faculty")
  public ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable UUID id) {
    var professor =
        this.professorService.getProfessor(id).orElseThrow(ProfessorNotFoundException::new);

    var optionalFaculty = Optional.ofNullable(professor.getFaculty());
    var faculty = optionalFaculty.orElseThrow(FacultyNotFoundException::new);

    return ResponseEntity.ok(this.facultyRepresentationModelAssembler.toModel(faculty));
  }

  @PutMapping(value = "/professors/{id}/faculty", consumes = MediaType.TEXT_PLAIN_VALUE)
  @PreAuthorize(
      "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(#request, #id)")
  public ResponseEntity<EntityModel<Faculty>> saveFaculty(
      @PathVariable UUID id, @RequestBody String facultyId, HttpServletRequest request) {

    var optProfessor = this.professorService.getProfessor(id);
    var optFaculty = this.facultyService.getFaculty(UUID.fromString(facultyId));

    var professor = optProfessor.orElseThrow(ProfessorNotFoundException::new);
    var faculty = optFaculty.orElseThrow(FacultyNotFoundException::new);

    professor.setFaculty(faculty);

    this.professorService.saveProfessor(professor);

    return ResponseEntity.ok(this.facultyRepresentationModelAssembler.toModel(faculty));
  }

  @PostMapping(value = "/professors")
  @PreAuthorize("hasRole('professor')")
  public ResponseEntity<EntityModel<Professor>> saveProfessor(
      @RequestBody Professor professor, HttpServletRequest request) {
    var entityModel =
        this.professorRepresentationModelAssembler.toModel(
            this.professorService.saveProfessor(professor));
    return ResponseEntity.ok(entityModel);
  }

  @PutMapping(value = "/professors/{id}")
  @PreAuthorize(
      "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(#request, #id)")
  public ResponseEntity<EntityModel<Professor>> updateProfessor(
      @PathVariable UUID id, @RequestBody Professor professor, HttpServletRequest request) {
    if (!professor.getId().equals(id)) {
      throw new PathIdNotMatchingEntityIdException();
    }

    var savedProfessor = this.professorService.updateProfessor(id, professor);

    // Try to update professor, otherwise create the professor
    // If professor was updated return 200 OK, if created 201 CREATED
    return savedProfessor
        .map(value -> ResponseEntity.ok(this.professorRepresentationModelAssembler.toModel(value)))
        .orElseGet(
            () ->
                ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                        this.professorRepresentationModelAssembler.toModel(
                            this.professorService.saveProfessor(professor))));
  }

  @GetMapping(value = "/professors/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> getProfessorImage(@PathVariable UUID id) {
    if (!this.professorService.existsById(id)) {
      throw new ProfessorNotFoundException();
    }

    var optProfileImage = this.professorService.getProfessorImage(id);

    if (optProfileImage.isPresent()) {
      var profileImage = optProfileImage.get();
      try {
        String contentType =
            URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(profileImage));
        if (contentType != null) {
          return ResponseEntity.ok()
              .contentType(MediaType.parseMediaType(contentType))
              .body(profileImage);
        }
      } catch (IOException e) {
        logger.error(
            MessageFormat.format("Could not load profile image with Professor ID {0}", id), e);
      }
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @PostMapping(
      value = "/professors/{id}/image",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize(
      "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(#request, #id)")
  public ResponseEntity postProfessorImage(
      @PathVariable UUID id,
      @RequestParam("image") MultipartFile image,
      HttpServletRequest request) {
    try {
      this.professorService.saveProfessorImage(id, image.getBytes());
      return ResponseEntity.ok().build();
    } catch (IOException e) {
      logger.error(
          MessageFormat.format("Could not save profile image with Professor ID {0}", id), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping(value = "/professors/{id}/image")
  @PreAuthorize(
      "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(#request, #id)")
  public ResponseEntity deleteProfessorImage(@PathVariable UUID id, HttpServletRequest request) {
    this.professorService.deleteProfessorImage(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
