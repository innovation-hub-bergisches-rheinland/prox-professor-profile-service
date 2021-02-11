package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("professors/search")
public interface ProfessorSearchController {

  @GetMapping("/findProfessorsByFacultyId")
  ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByFacultyId(
      @RequestParam UUID id,
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
          @RequestParam(value = "sort", defaultValue = "", required = false)
          String[] sort,
      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
      @RequestParam(value = "size", defaultValue = "10", required = false) int size);

  @GetMapping("/findProfessorsByFacultyIdAndName")
  ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByFacultyIdAndName(
      @RequestParam UUID id,
      @RequestParam String name,
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
          @RequestParam(value = "sort", defaultValue = "", required = false)
          String[] sort,
      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
      @RequestParam(value = "size", defaultValue = "10", required = false) int size);

  @GetMapping("/findProfessorsByName")
  ResponseEntity<PagedModel<EntityModel<Professor>>> findProfessorsByName(
      @RequestParam String name,
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
          @RequestParam(value = "sort", defaultValue = "", required = false)
          String[] sort,
      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
      @RequestParam(value = "size", defaultValue = "10", required = false) int size);
}
