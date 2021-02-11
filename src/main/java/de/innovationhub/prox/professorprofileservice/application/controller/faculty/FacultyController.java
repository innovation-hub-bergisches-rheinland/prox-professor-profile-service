package de.innovationhub.prox.professorprofileservice.application.controller.faculty;

import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("faculties")
public interface FacultyController {
  @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<CollectionModel<EntityModel<Faculty>>> getAllFaculties(
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string"))) Sort sort);

  @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable("id") UUID id);

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Faculty>> saveFaculty(@RequestBody Faculty faculty);
}
