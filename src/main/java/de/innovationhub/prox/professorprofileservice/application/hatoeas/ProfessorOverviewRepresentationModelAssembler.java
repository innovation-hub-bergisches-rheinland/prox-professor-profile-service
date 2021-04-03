package de.innovationhub.prox.professorprofileservice.application.hatoeas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import de.innovationhub.prox.professorprofileservice.application.controller.professor.ProfessorController;
import de.innovationhub.prox.professorprofileservice.application.controller.professor.ProfessorOverviewController;
import de.innovationhub.prox.professorprofileservice.application.service.professor.ProfessorService;
import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProfessorOverviewRepresentationModelAssembler
    implements SimpleRepresentationModelAssembler<ProfessorOverviewDto> {

  private final ProfessorService professorService;

  public ProfessorOverviewRepresentationModelAssembler(ProfessorService professorService) {
    this.professorService = professorService;
  }

  @Override
  public void addLinks(EntityModel<ProfessorOverviewDto> resource) {
    var professor = resource.getContent();
    if (professor != null) {
      var id = professor.getId();
      resource.add(linkTo(methodOn(ProfessorController.class).getProfessor(id)).withSelfRel());
      resource.add(
          linkTo(methodOn(ProfessorController.class).getProfessor(id)).withRel("professor"));
      if (this.professorService.hasImage(id)) {
        resource.add(
            linkTo(methodOn(ProfessorController.class).getProfessorImage(id)).withRel("image"));
      }
      if (professor.getFaculty() != null) {
        resource.add(linkTo(methodOn(ProfessorController.class).getFaculty(id)).withRel("faculty"));
      }
    }
  }

  @Override
  public void addLinks(CollectionModel<EntityModel<ProfessorOverviewDto>> resources) {
    resources.add(
        linkTo(methodOn(ProfessorOverviewController.class).getProfessorOverview()).withSelfRel());
  }
}
