package de.innovationhub.prox.professorprofileservice.application.service.professor;

import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfileImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
  private final ModelMapper modelMapper;
  private final ResourceLoader resourceLoader;

  public ProfessorService(
      ProfessorRepository professorRepository,
      ModelMapper modelMapper,
      ResourceLoader resourceLoader) {
    this.professorRepository = professorRepository;
    this.modelMapper = modelMapper;
    this.resourceLoader = resourceLoader;
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
      if (prof.getProfileImage() != null) {
        professor.setProfileImage(new ProfileImage(prof.getProfileImage().getData()));
      }
      this.modelMapper.map(professor, prof);
      return Optional.of(this.professorRepository.save(prof));
    }
    return Optional.empty();
  }

  public Optional<byte[]> getProfessorImage(UUID uuid) throws IOException {
    var optProfessor = this.getProfessor(uuid);
    if (optProfessor.isPresent()) {
      var professor = optProfessor.get();
      if (professor.getProfileImage() == null
          || professor.getProfileImage().getData().length == 0) {
        var inputStream =
            this.resourceLoader
                .getResource("classpath:/img/blank-profile-picture.png")
                .getInputStream();
        return Optional.of(IOUtils.toByteArray(inputStream));
      } else {
        var data = professor.getProfileImage().getData();
        return Optional.of(data);
      }
    }
    return Optional.empty();
  }

  public Optional<byte[]> saveProfessorImage(UUID uuid, byte[] imageData) throws IOException {
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
          professor.setProfileImage(new ProfileImage(byteArrayOutputStream.toByteArray()));
          this.saveProfessor(professor);
          return Optional.of(imageData);
        }
      }
    }

    return Optional.empty();
  }
}
