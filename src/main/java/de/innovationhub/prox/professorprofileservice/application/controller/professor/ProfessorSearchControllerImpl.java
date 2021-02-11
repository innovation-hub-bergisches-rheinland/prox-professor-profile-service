package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfessorSearchControllerImpl implements ProfessorSearchController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ProfessorService professorService;
  private final ProfessorRepresentationModelAssembler professorRepresentationModelAssembler;
  private final PagedResourcesAssembler<Professor> professorPagedResourcesAssembler;

  @Autowired
  public ProfessorSearchControllerImpl(
      ProfessorService professorService,
      ProfessorRepresentationModelAssembler professorRepresentationModelAssembler,
      PagedResourcesAssembler<Professor> professorPagedResourcesAssembler) {
    this.professorService = professorService;
    this.professorRepresentationModelAssembler = professorRepresentationModelAssembler;
    this.professorPagedResourcesAssembler = professorPagedResourcesAssembler;
  }

  public ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByFacultyId(
      UUID id, String[] sort, int page, int size) {

    var pageRequest = PageRequest.of(page, size, Sort.by(sort));

    var pagedResult = this.professorService.findProfessorsByFacultyId(id, pageRequest);

    var model =
        professorPagedResourcesAssembler.toModel(
            pagedResult, professorRepresentationModelAssembler);

    return ResponseEntity.ok(model);
  }

  public ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByFacultyIdAndName(
      UUID id, String name, String[] sort, int page, int size) {

    var pageRequest = PageRequest.of(page, size, Sort.by(sort));

    var pagedResult = this.professorService.findProfessorByFacultyIdAndName(id, name, pageRequest);

    var model =
        professorPagedResourcesAssembler.toModel(
            pagedResult, professorRepresentationModelAssembler);

    return ResponseEntity.ok(model);
  }

  public ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByName(
      String name, String[] sort, int page, int size) {

    var pageRequest = PageRequest.of(page, size, Sort.by(sort));

    var pagedResult = this.professorService.findProfessoryByName(name, pageRequest);

    var model =
        professorPagedResourcesAssembler.toModel(
            pagedResult, professorRepresentationModelAssembler);

    return ResponseEntity.ok(model);
  }
}
