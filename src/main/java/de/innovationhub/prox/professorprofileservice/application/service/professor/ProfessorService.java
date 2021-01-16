package de.innovationhub.prox.professorprofileservice.application.service.professor;

import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImage;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImageRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class ProfessorService {

  private final ProfessorRepository professorRepository;
  private final ProfessorImageRepository professorImageRepository;
  private final ModelMapper modelMapper;
  private final ResourceLoader resourceLoader;

  public ProfessorService(
      ProfessorRepository professorRepository,
      ProfessorImageRepository professorImageRepository,
      ResourceLoader resourceLoader) {
    this.professorRepository = professorRepository;
    this.professorImageRepository = professorImageRepository;
    this.resourceLoader = resourceLoader;
    this.modelMapper = configureMapper();
  }

  private ModelMapper configureMapper() {
    var mapper1 = new ModelMapper();

    // Configure TypeMapping for mapping Professor->Professor
    var typeMap = mapper1.typeMap(Professor.class, Professor.class);
    // Skip ProfessorImage when image is already set
    typeMap.addMappings(mapper -> mapper.when(Objects::nonNull).skip(Professor::setProfessorImage));

    // Configure TypeMapping for mapping ProfessorImage->ProfessorImage
    var typeMap2 = mapper1.typeMap(ProfessorImage.class, ProfessorImage.class);
    // Skip filename when already set
    typeMap2.addMappings(mapper -> mapper.when(Objects::nonNull).skip(ProfessorImage::setFilename));

    return mapper1;
  }

  public Iterable<Professor> getAllProfessors() {
    return this.professorRepository.findAll();
  }

  public Optional<Professor> getProfessor(UUID uuid) {
    return this.professorRepository.findById(uuid);
  }

  public Professor saveProfessor(Professor professor) {
    return this.professorRepository.save(professor);
  }

  public Optional<Professor> updateProfessor(UUID uuid, Professor professor) {
    var optProfessor = this.professorRepository.findById(uuid);
    if (optProfessor.isPresent()) {
      var prof = optProfessor.get();
      this.modelMapper.map(professor, prof);
      return Optional.of(this.professorRepository.save(prof));
    }
    return Optional.empty();
  }

  // TODO Refactor
  public Optional<byte[]> getProfessorImage(UUID uuid) throws IOException {
    var optProfessor = this.getProfessor(uuid);
    if (optProfessor.isPresent()) {
      var professor = optProfessor.get();
      if (professor.getProfessorImage() != null
          && professor.getProfessorImage().getFilename() != null
          && !professor.getProfessorImage().getFilename().isBlank()) {
        try {
          var result =
              this.professorImageRepository.getProfessorImage(
                  professor.getProfessorImage().getFilename());
          if (result.length > 0 && ImageIO.read(new ByteArrayInputStream(result)) != null) {
            return Optional.of(result);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      var inputStream =
          this.resourceLoader
              .getResource("classpath:/img/blank-profile-picture.png")
              .getInputStream();
      return Optional.of(IOUtils.toByteArray(inputStream));
    }
    return Optional.empty();
  }

  // TODO Refactor
  public Optional<Boolean> saveProfessorImage(UUID uuid, byte[] imageData) throws IOException {
    var optProfessor = this.getProfessor(uuid);
    if (optProfessor.isPresent()) {
      var professor = optProfessor.get();
      try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
        var bufferedImage = ImageIO.read(byteArrayInputStream);
        if (bufferedImage != null) {
          if (!ImageIO.write(bufferedImage, "png", byteArrayOutputStream)) {
            throw new IOException();
          }
          var filename =
              this.professorImageRepository.saveProfessorImage(byteArrayOutputStream.toByteArray());
          if (professor.getProfessorImage() != null
              && professor.getProfessorImage().getFilename() != null
              && this.professorImageRepository.imageExists(
                  professor.getProfessorImage().getFilename())) {
            this.professorImageRepository.deleteProfessorImage(
                professor.getProfessorImage().getFilename());
          }
          professor.setProfessorImage(new ProfessorImage(filename));
          this.saveProfessor(professor);
          return Optional.of(true);
        }
      }
    }

    return Optional.empty();
  }

  // TODO Refactor
  public Optional<Boolean> deleteProfessorImage(UUID id) {
    var optProfessor = this.getProfessor(id);
    if (optProfessor.isPresent()) {
      var professor = optProfessor.get();
      if (professor.getProfessorImage() != null
          && professor.getProfessorImage().getFilename() != null
          && !professor.getProfessorImage().getFilename().isBlank()) {
        try {
          var result =
              this.professorImageRepository.deleteProfessorImage(
                  professor.getProfessorImage().getFilename());
          if (result) {
            professor.setProfessorImage(null);
            this.saveProfessor(professor);
          }
          return Optional.of(result);
        } catch (Exception e) {
          e.printStackTrace();
          return Optional.of(false);
        }
      }
    }
    return Optional.empty();
  }
}
