package de.innovationhub.prox.professorprofileservice.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import de.innovationhub.prox.professorprofileservice.controller.FacultyController;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class FacultyRepresentationModelAssembler
    implements SimpleRepresentationModelAssembler<Faculty> {

  @Override
  public void addLinks(EntityModel<Faculty> resource) {
    var faculty = resource.getContent();
    if (faculty != null) {
      try {
        resource.add(
            linkTo(methodOn(FacultyController.class).getFaculty(faculty.getId())).withSelfRel());
        resource.add(
            linkTo(methodOn(FacultyController.class).getFaculty(faculty.getId()))
                .withRel("faculty"));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void addLinks(CollectionModel<EntityModel<Faculty>> resources) {
    resources.add(
        linkTo(methodOn(FacultyController.class).getALlFaculties(Sort.unsorted())).withSelfRel());
  }
}
