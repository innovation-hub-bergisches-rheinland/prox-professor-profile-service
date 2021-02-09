package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
  private final PagedResourcesAssembler<Professor> professorPagedResourcesAssembler;

  @Autowired
  public ProfessorSearchController(
      ProfessorService professorService,
      ProfessorRepresentationModelAssembler professorRepresentationModelAssembler,
      PagedResourcesAssembler<Professor> professorPagedResourcesAssembler) {
    this.professorService = professorService;
    this.professorRepresentationModelAssembler = professorRepresentationModelAssembler;
    this.professorPagedResourcesAssembler = professorPagedResourcesAssembler;
  }

  @GetMapping("/findProfessorsByFacultyId")
  public ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByFacultyId(
      @RequestParam UUID id,
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
          @RequestParam(value = "sort", defaultValue = "", required = false)
          String[] sort,
      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
      @RequestParam(value = "size", defaultValue = "10", required = false) int size) {

    var pageRequest = PageRequest.of(page, size, Sort.by(sort));

    var pagedResult = this.professorService.findProfessorsByFacultyId(id, pageRequest);

    var model =
        professorPagedResourcesAssembler.toModel(
            pagedResult, professorRepresentationModelAssembler);

    return ResponseEntity.ok(model);
  }

  @GetMapping("/findProfessorsByFacultyIdAndName")
  public ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByFacultyIdAndName(
      @RequestParam UUID id,
      @RequestParam String name,
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
          @RequestParam(value = "sort", defaultValue = "", required = false)
          String[] sort,
      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
      @RequestParam(value = "size", defaultValue = "10", required = false) int size) {

    var pageRequest = PageRequest.of(page, size, Sort.by(sort));

    var pagedResult = this.professorService.findProfessorByFacultyIdAndName(id, name, pageRequest);

    var model =
        professorPagedResourcesAssembler.toModel(
            pagedResult, professorRepresentationModelAssembler);

    return ResponseEntity.ok(model);
  }

  @GetMapping("/findProfessorsByName")
  public ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByName(
      @RequestParam String name,
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
          @RequestParam(value = "sort", defaultValue = "", required = false)
          String[] sort,
      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
      @RequestParam(value = "size", defaultValue = "10", required = false) int size) {

    var pageRequest = PageRequest.of(page, size, Sort.by(sort));

    var pagedResult = this.professorService.findProfessoryByName(name, pageRequest);

    var model =
        professorPagedResourcesAssembler.toModel(
            pagedResult, professorRepresentationModelAssembler);

    return ResponseEntity.ok(model);
  }
}
