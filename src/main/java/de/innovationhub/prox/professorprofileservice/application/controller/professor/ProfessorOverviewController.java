package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Professor Overview API", description = "API to retrieve projections of professors")
@RequestMapping("professors")
public interface ProfessorOverviewController {

  @Operation(
      description =
          "Respond with a projection of all professors including the basic personal informations and information about their current faculty as well as stats about their project history")
  @GetMapping(value = "/overview", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<CollectionModel<EntityModel<ProfessorOverviewDto>>> getProfessorOverview();
}
