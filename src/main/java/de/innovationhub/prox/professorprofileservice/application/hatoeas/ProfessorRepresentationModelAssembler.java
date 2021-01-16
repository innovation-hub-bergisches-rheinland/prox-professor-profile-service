package de.innovationhub.prox.professorprofileservice.application.hatoeas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import de.innovationhub.prox.professorprofileservice.application.controller.faculty.FacultyController;
import de.innovationhub.prox.professorprofileservice.application.controller.professor.ProfessorController;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProfessorRepresentationModelAssembler
    implements SimpleRepresentationModelAssembler<Professor> {

  @Override
  public void addLinks(EntityModel<Professor> resource) {
    var professor = resource.getContent();
    if (professor != null) {
      var id = professor.getId();
      resource.add(linkTo(methodOn(ProfessorController.class).getProfessor(id)).withSelfRel());
      resource.add(
          linkTo(methodOn(ProfessorController.class).getProfessor(id)).withRel("professor"));
      resource.add(
          linkTo(methodOn(ProfessorController.class).getProfessorImage(id)).withRel("image"));
      if (professor.getFaculty() != null) {
        resource.add(
            linkTo(methodOn(FacultyController.class).getFaculty(professor.getFaculty().getId()))
                .withRel("faculty"));
      }
    }
  }

  @Override
  public void addLinks(CollectionModel<EntityModel<Professor>> resources) {
    resources.add(
        linkTo(methodOn(ProfessorController.class).getAllProfessors(Sort.unsorted()))
            .withSelfRel());
  }
}
