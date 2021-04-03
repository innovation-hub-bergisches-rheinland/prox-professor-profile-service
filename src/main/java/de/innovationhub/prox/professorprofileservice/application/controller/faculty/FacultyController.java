package de.innovationhub.prox.professorprofileservice.application.controller.faculty;

import de.innovationhub.prox.professorprofileservice.application.exception.ApiError;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Faculty API", description = "APIs for faculties")
@RequestMapping("faculties")
public interface FacultyController {

  @Operation(summary = "Get all faculties")
  @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<CollectionModel<EntityModel<Faculty>>> getAllFaculties(
      @Parameter(
              array = @ArraySchema(schema = @Schema(type = "string"), uniqueItems = true),
              description = "Comma-separated property names to sort by (e.g. `name,abbreviation`)")
          @RequestParam(required = false, defaultValue = "")
          String[] sort);

  @ApiResponse(
      responseCode = "400",
      description = "Invalid UUID",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "404",
      description = "No faculty with the given ID found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(responseCode = "200", description = "OK")
  @Operation(summary = "Get faculty")
  @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Faculty>> getFaculty(
      @PathVariable("id") @Parameter(description = "UUID of faculty") UUID id);
}
