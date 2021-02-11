package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("professors")
public interface ProfessorOverviewController {

  @GetMapping(value = "/overview", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<CollectionModel<EntityModel<ProfessorOverviewDto>>> getProfessorOverview();
}
