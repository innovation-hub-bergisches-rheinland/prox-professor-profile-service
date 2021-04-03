package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Professor API")
@RequestMapping("professors")
public interface ProfessorOverviewController {

  @ApiResponse(responseCode = "200", description = "OK")
  @Operation(
      summary = "Responds with a compact overview",
      description =
          "This API gives the basic information summary over all professors and a small project statistic")
  @GetMapping(value = "/overview", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<CollectionModel<EntityModel<ProfessorOverviewDto>>> getProfessorOverview();
}
