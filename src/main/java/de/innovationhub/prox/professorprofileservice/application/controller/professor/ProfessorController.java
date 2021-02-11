package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("professors")
public interface ProfessorController {

  @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<PagedModel<EntityModel<Professor>>> getAllProfessors(
      @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
          @RequestParam(value = "sort", defaultValue = "", required = false)
          String[] sort,
      @RequestParam(value = "page", defaultValue = "0", required = false) int page,
      @RequestParam(value = "size", defaultValue = "10", required = false) int size);

  @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Professor>> getProfessor(@PathVariable UUID id);

  @GetMapping(value = "/{id}/faculty", produces = MediaTypes.HAL_JSON_VALUE)
  ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable UUID id);

  @PutMapping(
      value = "/{id}/faculty",
      consumes = MediaType.TEXT_PLAIN_VALUE,
      produces = MediaTypes.HAL_JSON_VALUE)
  @PreAuthorize(
      "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(#request, #id)")
  ResponseEntity<EntityModel<Faculty>> saveFaculty(
      @PathVariable UUID id, @RequestBody String facultyId, HttpServletRequest request);

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaTypes.HAL_JSON_VALUE)
  @PreAuthorize("hasRole('professor')")
  ResponseEntity<EntityModel<Professor>> saveProfessor(
      @RequestBody Professor professor, HttpServletRequest request);

  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaTypes.HAL_JSON_VALUE)
  @PreAuthorize(
      "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(#request, #id)")
  ResponseEntity<EntityModel<Professor>> updateProfessor(
      @PathVariable UUID id, @RequestBody Professor professor, HttpServletRequest request);

  @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
  ResponseEntity<byte[]> getProfessorImage(@PathVariable UUID id);

  @PostMapping(
      value = "/{id}/image",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize(
      "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(#request, #id)")
  ResponseEntity postProfessorImage(
      @PathVariable UUID id,
      @RequestParam("image") MultipartFile image,
      HttpServletRequest request);

  @DeleteMapping(value = "/{id}/image")
  @PreAuthorize(
      "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(#request, #id)")
  ResponseEntity deleteProfessorImage(@PathVariable UUID id, HttpServletRequest request);
}
