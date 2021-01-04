package de.innovationhub.prox.professorprofileservice.professor;

import de.innovationhub.prox.professorprofileservice.util.FacultyRepresentationModelAssembler;
import de.innovationhub.prox.professorprofileservice.util.ProfessorRepresentationModelAssembler;
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
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

@RestController
public class ProfessorController {

  ProfessorRepository professorRepository;
  ResourceLoader resourceLoader;
  ProfessorRepresentationModelAssembler professorRepresentationModelAssembler;
  FacultyRepresentationModelAssembler facultyRepresentationModelAssembler;

  @Autowired
  public ProfessorController(
      ProfessorRepository professorRepository,
      FacultyRepository facultyRepository,
      ResourceLoader resourceLoader,
      ProfessorRepresentationModelAssembler professorRepresentationModelAssembler,
      FacultyRepresentationModelAssembler facultyRepresentationModelAssembler) {
    this.professorRepository = professorRepository;
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
  public ResponseEntity<EntityModel<Professor>> getProfessor(@PathVariable UUID id)
      throws NotFoundException, IOException {
    var professor = professorRepository.findById(id).orElseThrow(NotFoundException::new);
    return ResponseEntity.ok(professorRepresentationModelAssembler.toModel(professor));
  }

  @GetMapping(value = "/professors/{id}/faculty")
  public ResponseEntity<EntityModel<Faculty>> getFaculty(@PathVariable UUID id)
      throws NotFoundException {
    var professor = professorRepository.findById(id).orElseThrow(NotFoundException::new);
    var faculty = professor.getFaculty();
    if (faculty != null) {
      return ResponseEntity.ok(facultyRepresentationModelAssembler.toModel(faculty));
    }
    throw new NotFoundException();
  }

  // TODO Link instead of full entity
  /*@PostMapping(value = "/professors/{id}/faculty", consumes = "text/uri-list")
  public ResponseEntity<EntityModel<Faculty>> saveFaculty(@PathVariable UUID id, @RequestBody Resource)
      throws NotFoundException {
    var optProfessor = professorRepository.findById(id);
    if(optProfessor.isPresent()) {
      var professor = optProfessor.get();
      professor.setFaculty(faculty);
      professorRepository.save(professor);
      var entityModel = EntityModel.of(faculty);
      entityModel.add(linkTo(methodOn(ProfessorController.class).getFaculty(id)).withSelfRel());
      entityModel.add(linkTo(methodOn(FacultyController.class).getFaculty(faculty.getId())).withRel("faculty"));
      return ResponseEntity.ok(entityModel);
    }
    throw new NotFoundException();
  }*/

  @PostMapping(value = "/professors/{id}")
  public ResponseEntity<EntityModel<Professor>> saveProfessor(
      @PathVariable UUID id, @RequestBody Professor professor) {
    if (professor.getId() != id) {
      throw new NotImplementedException();
    }
    var entityModel =
        professorRepresentationModelAssembler.toModel(professorRepository.save(professor));
    return ResponseEntity.ok(entityModel);
  }

  @PutMapping(value = "/professors/{id}")
  public ResponseEntity<EntityModel<Professor>> updateProfessor(
      @PathVariable UUID id, @RequestBody Professor professor)
      throws NotFoundException, IOException {
    // TODO
    return saveProfessor(id, professor);
  }

  @GetMapping(value = "/professors/{id}/image")
  public ResponseEntity<byte[]> getProfessorImage(@PathVariable UUID id)
      throws IOException, NotFoundException {
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
    throw new NotFoundException();
  }

  @PostMapping(value = "/professors/{id}/image")
  public ResponseEntity<?> postProfessorImage(
      @PathVariable UUID id, @RequestParam("image") MultipartFile image)
      throws IOException, NotFoundException {
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
          return ResponseEntity.ok().build();
        }
      }
    }
    throw new NotFoundException();
  }
}
