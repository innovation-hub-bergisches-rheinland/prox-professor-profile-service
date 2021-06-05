package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.application.exception.ApiError;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Professor API", description = "APIs for professors")
@RequestMapping("professors")
public interface ProfessorController {

  @Operation(summary = "get all professors")
  @ApiResponse(responseCode = "200", description = "OK")
  @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<PagedModel<EntityModel<Professor>>> getAllProfessors(
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
          @RequestParam(value = "sort", defaultValue = "", required = false)
          String[] sort,
      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
      @RequestParam(value = "size", defaultValue = "10", required = false) int size);

  @Operation(summary = "get professor")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid UUID",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "404",
      description = "No professor with the given ID found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Professor>> getProfessor(@PathVariable UUID id);

  @Operation(summary = "get professors faculty")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid UUID",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "404",
      description = "No professor with the given ID found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @GetMapping(value = "/{id}/faculty", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable UUID id);

  @Operation(summary = "set professors faculty", security = @SecurityRequirement(name = "Bearer"))
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid UUID",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "403",
      description = "Forbidden",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "404",
      description = "No professor with the given ID found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @PutMapping(
      value = "/{id}/faculty",
      consumes = MediaType.TEXT_PLAIN_VALUE,
      produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Faculty>> saveFaculty(
      @PathVariable UUID id, @RequestBody String facultyId, HttpServletRequest request);

  @ApiResponse(responseCode = "201", description = "Created")
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "403",
      description = "Forbidden",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @Operation(summary = "save professor", security = @SecurityRequirement(name = "Bearer"))
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Professor>> saveProfessor(
      @RequestBody Professor professor, HttpServletRequest request);

  @Operation(summary = "update professor", security = @SecurityRequirement(name = "Bearer"))
  @ApiResponse(responseCode = "200", description = "Updated")
  @ApiResponse(responseCode = "201", description = "Created if professor not exists")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid UUID",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "403",
      description = "Forbidden",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "404",
      description = "No professor with the given ID found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Professor>> updateProfessor(
      @PathVariable UUID id, @RequestBody Professor professor, HttpServletRequest request);

  @Operation(summary = "get professor image")
  @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
  ResponseEntity<byte[]> getProfessorImage(@PathVariable UUID id);

  @Operation(summary = "uploads profile picture", security = @SecurityRequirement(name = "Bearer"))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid UUID",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "403",
      description = "Forbidden",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "404",
      description = "No professor with the given ID found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @PostMapping(
      value = "/{id}/image",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  ResponseEntity<Void> postProfessorImage(
      @Parameter(required = true, description = "UUID of Professor") @PathVariable UUID id,
      @Parameter(required = true, description = "GIF / PNG / JPG Image")
          @RequestPart(value = "image", required = true)
          MultipartFile image,
      HttpServletRequest request);

  @Operation(summary = "deletes profile picture", security = @SecurityRequirement(name = "Bearer"))
  @ApiResponse(responseCode = "204", description = "Deleted successfully")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid UUID",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "403",
      description = "Forbidden",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "404",
      description = "No professor with the given ID found",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))
  @DeleteMapping(value = "/{id}/image")
  ResponseEntity<Void> deleteProfessorImage(@PathVariable UUID id, HttpServletRequest request);

  @Operation(summary = "find professor with name like")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(
      responseCode = "400",
      description = "Invalid Parameter",
      content =
      @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ApiError.class)))
  @GetMapping(value = "/search/findProfessorIdsByNames", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Map<String, UUID>> findProfessorWithNameLike(@RequestParam(required = true, name = "names") String[] names);

}
