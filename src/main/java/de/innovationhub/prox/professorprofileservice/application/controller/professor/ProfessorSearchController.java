package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("professors/search")
public class ProfessorSearchController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ProfessorService professorService;
  private final ProfessorRepresentationModelAssembler professorRepresentationModelAssembler;

  @Autowired
  public ProfessorSearchController(
      ProfessorService professorService,
      ProfessorRepresentationModelAssembler professorRepresentationModelAssembler) {
    this.professorService = professorService;
    this.professorRepresentationModelAssembler = professorRepresentationModelAssembler;
  }

  @GetMapping("/findProfessorsByFacultyId")
  public ResponseEntity<CollectionModel<EntityModel<Professor>>> findProfessorsByFacultyId(
      @RequestParam UUID id) {
    var collectionModel =
        this.professorRepresentationModelAssembler.toCollectionModel(
            this.professorService.findProfessorsByFacultyId(id));
    return ResponseEntity.ok(collectionModel);
  }
}
