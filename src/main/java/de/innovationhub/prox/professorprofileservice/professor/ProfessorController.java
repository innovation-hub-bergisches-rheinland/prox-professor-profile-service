package de.innovationhub.prox.professorprofileservice.professor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProfessorController {

  ProfessorRepository professorRepository;
  ResourceLoader resourceLoader;

  @Autowired
  public ProfessorController(
      ProfessorRepository professorRepository,
      FacultyRepository facultyRepository,
      ResourceLoader resourceLoader) {
    this.professorRepository = professorRepository;
    this.resourceLoader = resourceLoader;
  }

  @GetMapping("/professors")
  public ResponseEntity<CollectionModel<EntityModel<Professor>>> getAllProfessors(Sort sort) {
    var professors =
        StreamSupport.stream(professorRepository.findAll().spliterator(), false)
            .map(
                professor -> {
                  var entityModel = EntityModel.of(professor);
                  try {
                    entityModel.add(
                        linkTo(methodOn(ProfessorController.class).getProfessor(professor.getId()))
                            .withSelfRel());
                    entityModel.add(
                        linkTo(methodOn(ProfessorController.class).getProfessor(professor.getId()))
                            .withRel("professor"));
                    entityModel.add(
                        linkTo(
                                methodOn(ProfessorController.class)
                                    .getProfessorImage(professor.getId()))
                            .withRel("image"));
                    var faculty = Optional.ofNullable(professor.getFaculty());
                    if (faculty.isPresent()) {
                      entityModel.add(
                          linkTo(
                                  methodOn(FacultyController.class)
                                      .getFaculty(faculty.get().getId()))
                              .withRel("faculty"));
                    }
                  } catch (NotFoundException | IOException e) {
                    e.printStackTrace();
                  }
                  return entityModel;
                })
            .collect(Collectors.toList());

    var collectionModel = CollectionModel.of(professors);
    collectionModel.add(
        linkTo(methodOn(ProfessorController.class).getAllProfessors(sort)).withSelfRel());

    return ResponseEntity.ok(collectionModel);
  }

  @GetMapping("/professors/{id}")
  public ResponseEntity<EntityModel<Professor>> getProfessor(@PathVariable UUID id)
      throws NotFoundException, IOException {
    var professor =
        EntityModel.of(professorRepository.findById(id).orElseThrow(NotFoundException::new));
    professor.add(linkTo(methodOn(ProfessorController.class).getProfessor(id)).withSelfRel());
    professor.add(
        linkTo(methodOn(ProfessorController.class).getProfessor(id)).withRel("professor"));
    professor.add(
        linkTo(methodOn(ProfessorController.class).getProfessorImage(id)).withRel("image"));
    var content = Optional.ofNullable(professor.getContent());
    if (content.isPresent()) {
      var faculty = Optional.ofNullable(content.get().getFaculty());
      if (faculty.isPresent()) {
        professor.add(
            linkTo(methodOn(FacultyController.class).getFaculty(faculty.get().getId()))
                .withRel("faculty"));
      }
    }
    return ResponseEntity.ok(professor);
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
