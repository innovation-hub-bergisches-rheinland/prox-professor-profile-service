package de.innovationhub.prox.professorprofileservice.application.controller;

import de.innovationhub.prox.professorprofileservice.application.hatoeas.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.hatoeas.ProfessorRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.application.service.faculty.FacultyService;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfileImage;
import io.swagger.annotations.ApiOperation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ProfessorController {

  ProfessorRepository professorRepository;
  FacultyService facultyService;
  ResourceLoader resourceLoader;
  ProfessorRepresentationModelAssembler professorRepresentationModelAssembler;
  FacultyRepresentationModelAssembler facultyRepresentationModelAssembler;

  @Autowired
  public ProfessorController(
      ProfessorRepository professorRepository,
      FacultyService facultyService,
      ResourceLoader resourceLoader,
      ProfessorRepresentationModelAssembler professorRepresentationModelAssembler,
      FacultyRepresentationModelAssembler facultyRepresentationModelAssembler) {
    this.professorRepository = professorRepository;
    this.facultyService = facultyService;
    this.resourceLoader = resourceLoader;
    this.professorRepresentationModelAssembler = professorRepresentationModelAssembler;
    this.facultyRepresentationModelAssembler = facultyRepresentationModelAssembler;
  }

  @GetMapping("/professors")
  public ResponseEntity<CollectionModel<EntityModel<Professor>>> getAllProfessors(Sort sort) {
    var collectionModel =
        professorRepresentationModelAssembler.toCollectionModel(professorRepository.findAll());
    return ResponseEntity.ok(collectionModel);
  }

  @GetMapping("/professors/{id}")
  public ResponseEntity<EntityModel<Professor>> getProfessor(@PathVariable UUID id) {
    var professor =
        professorRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find professor"));
    return ResponseEntity.ok(professorRepresentationModelAssembler.toModel(professor));
  }

  @GetMapping(value = "/professors/{id}/faculty")
  public ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable UUID id) {
    var professor =
        professorRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find professor"));
    var faculty = professor.getFaculty();
    if (faculty != null) {
      return ResponseEntity.ok(facultyRepresentationModelAssembler.toModel(faculty));
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find faculty");
  }

  @PutMapping(value = "/professors/{id}/faculty", consumes = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<EntityModel<Faculty>> saveFaculty(
      @PathVariable UUID id, @RequestBody String facultyId) {
    var optProfessor = professorRepository.findById(id);
    try {
      var faculty = facultyService.getFaculty(UUID.fromString(facultyId));
      if (optProfessor.isPresent() && faculty.isPresent()) {
        var professor = optProfessor.get();
        professor.setFaculty(faculty.get());
        professorRepository.save(professor);
        return ResponseEntity.ok(facultyRepresentationModelAssembler.toModel(faculty.get()));
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find professor");
  }

  @PostMapping(value = "/professors")
  public ResponseEntity<EntityModel<Professor>> saveProfessor(@RequestBody Professor professor) {
    var entityModel =
        professorRepresentationModelAssembler.toModel(professorRepository.save(professor));
    return ResponseEntity.ok(entityModel);
  }

  @PutMapping(value = "/professors/{id}")
  public ResponseEntity<EntityModel<Professor>> updateProfessor(
      @PathVariable UUID id, @RequestBody Professor professor) {
    if (!professor.getId().equals(id)) {
      throw new NotImplementedException();
    }
    if (professorRepository.existsById(professor.getId())) {
      return ResponseEntity.ok(
          professorRepresentationModelAssembler.toModel(professorRepository.save(professor)));
    } else {
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(professorRepresentationModelAssembler.toModel(professorRepository.save(professor)));
    }
  }

  @GetMapping(value = "/professors/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> getProfessorImage(@PathVariable UUID id) throws IOException {
    var optProfessor = professorRepository.findById(id);
    if (optProfessor.isPresent()) {
      var professor = optProfessor.get();
      if (professor.getProfileImage() == null || professor.getProfileImage().getData() == null) {
        var inputStream =
            resourceLoader.getResource("classpath:/img/blank-profile-picture.png").getInputStream();
        String contentType = URLConnection.guessContentTypeFromStream(inputStream);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(IOUtils.toByteArray(inputStream));
      } else {
        var data = professor.getProfileImage().getData();
        String contentType =
            URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(data));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(data);
      }
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find professor");
  }

  @PostMapping(value = "/professors/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiOperation(value = "Daa", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<byte[]> postProfessorImage(
      @PathVariable UUID id, @RequestParam("image") MultipartFile image) throws IOException {
    var optProfessor = professorRepository.findById(id);
    if (optProfessor.isPresent()) {
      var professor = optProfessor.get();
      try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image.getBytes());
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
        var bufferedImage = ImageIO.read(byteArrayInputStream);
        if (bufferedImage != null) {
          if (!ImageIO.write(bufferedImage, "png", byteArrayOutputStream)) {
            throw new IOException();
          }
          professor.setProfileImage(new ProfileImage(byteArrayOutputStream.toByteArray()));
          professorRepository.save(professor);
          return ResponseEntity.ok().build();
        }
      }
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find professor");
  }
}
