package de.innovationhub.prox.professorprofileservice.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.repository.FacultyRepository;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FacultyController {

  private final FacultyRepository facultyRepository;

  @Autowired
  public FacultyController(FacultyRepository facultyRepository) {
    this.facultyRepository = facultyRepository;
  }

  @GetMapping(value = "/faculties", produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<CollectionModel<EntityModel<Faculty>>> getALlFaculties(Sort sort) {
    var faculties =
        StreamSupport.stream(facultyRepository.findAll(sort).spliterator(), false)
            .map(
                (faculty -> {
                  var entityModel = EntityModel.of(faculty);
                  try {
                    entityModel.add(
                        linkTo(methodOn(FacultyController.class).getFaculty(faculty.getId()))
                            .withSelfRel());
                    entityModel.add(
                        linkTo(methodOn(FacultyController.class).getFaculty(faculty.getId()))
                            .withRel("faculty"));
                  } catch (NotFoundException e) {
                    e.printStackTrace();
                  }
                  return entityModel;
                }))
            .collect(Collectors.toList());

    CollectionModel<EntityModel<Faculty>> collectionModel = CollectionModel.of(faculties);
    collectionModel.add(
        linkTo(methodOn(FacultyController.class).getALlFaculties(sort)).withSelfRel());

    return ResponseEntity.ok(collectionModel);
  }

  @GetMapping(value = "faculties/{id}", produces = MediaTypes.HAL_JSON_VALUE)
  public ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable("id") UUID id)
      throws NotFoundException {
    var faculty =
        EntityModel.of(facultyRepository.findById(id).orElseThrow(NotFoundException::new));
    faculty.add(linkTo(methodOn(FacultyController.class).getFaculty(id)).withSelfRel());
    faculty.add(linkTo(methodOn(FacultyController.class).getFaculty(id)).withRel("faculty"));
    return ResponseEntity.ok(faculty);
  }
}
