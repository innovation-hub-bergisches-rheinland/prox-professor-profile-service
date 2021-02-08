package de.innovationhub.prox.professorprofileservice.application.service.professor;

import de.innovationhub.prox.professorprofileservice.application.exception.professor.ProfessorNotFoundException;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImage;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImageRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorRepository;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProfessorService {

  private final ProfessorRepository professorRepository;
  private final ProfessorImageRepository professorImageRepository;
  private final ModelMapper modelMapper;

  public ProfessorService(
      ProfessorRepository professorRepository, ProfessorImageRepository professorImageRepository) {
    this.professorRepository = professorRepository;
    this.professorImageRepository = professorImageRepository;
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
      prof.getPublications().clear();
      prof.getResearchSubjects().clear();
      this.modelMapper.map(professor, prof);
      return Optional.of(this.professorRepository.save(prof));
    }
    return Optional.empty();
  }

  public Optional<byte[]> getProfessorImage(UUID uuid) {
    var optProfessor = this.getProfessor(uuid);
    if (optProfessor.isPresent()) {
      var professor = optProfessor.get();
      return Optional.ofNullable(professor.getProfessorImage())
          .flatMap(pi -> this.professorImageRepository.getProfessorImage(pi.getFilename()))
          .or(this.professorImageRepository::getDefaultImage);
    }
    return Optional.empty();
  }

  public void saveProfessorImage(UUID uuid, byte[] imageData) throws IOException {
    var optProfessor = this.getProfessor(uuid);
    if (optProfessor.isEmpty()) {
      throw new ProfessorNotFoundException();
    }
    var professor = optProfessor.get();
    var filepath = this.professorImageRepository.saveProfessorImage(imageData);
    this.deleteProfessorImage(professor.getId());
    professor.setProfessorImage(new ProfessorImage(filepath));
    this.professorRepository.save(professor);
  }

  public void deleteProfessorImage(UUID id) {
    var optProfessor = this.getProfessor(id);
    if (optProfessor.isEmpty()) {
      throw new ProfessorNotFoundException();
    }

    var professor = optProfessor.get();
    var filename =
        Optional.ofNullable(professor.getProfessorImage()).map(ProfessorImage::getFilename);

    if (filename.isPresent()) {
      try {
        var result = this.professorImageRepository.deleteProfessorImage(filename.get());
        if (result) {
          professor.setProfessorImage(null);
          this.saveProfessor(professor);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public boolean existsById(UUID id) {
    return this.professorRepository.existsById(id);
  }

  public Iterable<Professor> findProfessorsByFacultyId(UUID id) {
    return this.professorRepository.findAllByFaculty_Id(id);
  }
}
