package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorOverviewRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorOverviewService;
import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfessorOverviewControllerImpl implements ProfessorOverviewController {

  private final ProfessorOverviewService professorOverviewService;
  private final ProfessorOverviewRepresentationModelAssembler representationModelAssembler;

  @Autowired
  public ProfessorOverviewControllerImpl(
      ProfessorOverviewService professorOverviewService,
      ProfessorOverviewRepresentationModelAssembler representationModelAssembler) {
    this.professorOverviewService = professorOverviewService;
    this.representationModelAssembler = representationModelAssembler;
  }

  // @GetMapping("/overview")
  public ResponseEntity<CollectionModel<EntityModel<ProfessorOverviewDto>>> getProfessorOverview() {
    return ResponseEntity.ok(
        representationModelAssembler.toCollectionModel(
            this.professorOverviewService.getProfessorOverview()));
  }
}
