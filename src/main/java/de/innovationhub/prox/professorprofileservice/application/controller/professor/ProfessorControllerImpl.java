package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.application.exception.faculty.FacultyNotFoundException;
import de.innovationhub.prox.professorprofileservice.application.exception.integrety.PathIdNotMatchingEntityIdException;
import de.innovationhub.prox.professorprofileservice.application.exception.professor.ProfessorNotFoundException;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyService;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProfessorControllerImpl implements ProfessorController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ProfessorService professorService;
  private final FacultyService facultyService;
  private final ProfessorRepresentationModelAssembler professorRepresentationModelAssembler;
  private final FacultyRepresentationModelAssembler facultyRepresentationModelAssembler;
  private final PagedResourcesAssembler<Professor> pagedResourcesAssembler;

  @Autowired
  public ProfessorControllerImpl(
      ProfessorService professorService,
      FacultyService facultyService,
      ProfessorRepresentationModelAssembler professorRepresentationModelAssembler,
      FacultyRepresentationModelAssembler facultyRepresentationModelAssembler,
      PagedResourcesAssembler<Professor> pagedResourcesAssembler) {
    this.professorService = professorService;
    this.facultyService = facultyService;
    this.professorRepresentationModelAssembler = professorRepresentationModelAssembler;
    this.facultyRepresentationModelAssembler = facultyRepresentationModelAssembler;
    this.pagedResourcesAssembler = pagedResourcesAssembler;
  }

  public ResponseEntity<PagedModel<EntityModel<Professor>>> getAllProfessors(
      String[] sort, int page, int size) {

    var pageRequest = PageRequest.of(page, size, Sort.by(sort));

    var pagedResult = this.professorService.getAllProfessors(pageRequest);

    var model = pagedResourcesAssembler.toModel(pagedResult, professorRepresentationModelAssembler);

    return ResponseEntity.ok(model);
  }

  public ResponseEntity<EntityModel<Professor>> getProfessor(UUID id) {
    var professor =
        this.professorService.getProfessor(id).orElseThrow(ProfessorNotFoundException::new);
    return ResponseEntity.ok(this.professorRepresentationModelAssembler.toModel(professor));
  }

  public ResponseEntity<EntityModel<Faculty>> getFaculty(UUID id) {
    var professor =
        this.professorService.getProfessor(id).orElseThrow(ProfessorNotFoundException::new);

    var optionalFaculty = Optional.ofNullable(professor.getFaculty());
    var faculty = optionalFaculty.orElseThrow(FacultyNotFoundException::new);

    return ResponseEntity.ok(this.facultyRepresentationModelAssembler.toModel(faculty));
  }

  public ResponseEntity<EntityModel<Faculty>> saveFaculty(
      UUID id, String facultyId, HttpServletRequest request) {

    var optProfessor = this.professorService.getProfessor(id);
    var optFaculty = this.facultyService.getFaculty(UUID.fromString(facultyId));

    var professor = optProfessor.orElseThrow(ProfessorNotFoundException::new);
    var faculty = optFaculty.orElseThrow(FacultyNotFoundException::new);

    professor.setFaculty(faculty);

    this.professorService.saveProfessor(professor);

    return ResponseEntity.ok(this.facultyRepresentationModelAssembler.toModel(faculty));
  }

  public ResponseEntity<EntityModel<Professor>> saveProfessor(
      Professor professor, HttpServletRequest request) {
    var entityModel =
        this.professorRepresentationModelAssembler.toModel(
            this.professorService.saveProfessor(professor));
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  public ResponseEntity<EntityModel<Professor>> updateProfessor(
      UUID id, @RequestBody Professor professor, HttpServletRequest request) {
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

  public ResponseEntity<byte[]> getProfessorImage(UUID id) {
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

  public ResponseEntity<Void> postProfessorImage(
      UUID id, MultipartFile image, HttpServletRequest request) {
    try {
      this.professorService.saveProfessorImage(id, image.getBytes());
      return ResponseEntity.ok().build();
    } catch (IOException e) {
      logger.error(
          MessageFormat.format("Could not save profile image with Professor ID {0}", id), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  public ResponseEntity<Void> deleteProfessorImage(UUID id, HttpServletRequest request) {
    this.professorService.deleteProfessorImage(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
