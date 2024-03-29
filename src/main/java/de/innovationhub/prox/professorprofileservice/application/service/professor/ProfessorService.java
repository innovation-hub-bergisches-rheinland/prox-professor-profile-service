package de.innovationhub.prox.professorprofileservice.application.service.professor;


import de.innovationhub.prox.professorprofileservice.application.exception.professor.ProfessorNotFoundException;
import de.innovationhub.prox.professorprofileservice.domain.professor.Professor;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImage;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImageRepository;
import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    typeMap.addMappings(
        mapper ->
            mapper
                .when(Objects::nonNull)
                .skip(Professor::getProfessorImage, Professor::setProfessorImage));

    // Configure TypeMapping for mapping ProfessorImage->ProfessorImage
    var typeMap2 = mapper1.typeMap(ProfessorImage.class, ProfessorImage.class);
    // Skip filename when already set
    typeMap2.addMappings(
        mapper ->
            mapper
                .when(Objects::nonNull)
                .skip(ProfessorImage::getFilename, ProfessorImage::setFilename));

    return mapper1;
  }

  public Iterable<Professor> getAllProfessors(Sort sort) {
    return this.professorRepository.findAll(sort);
  }

  public Page<Professor> getAllProfessors(Pageable pageable) {
    return this.professorRepository.findAll(pageable);
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

  public boolean hasImage(UUID id) {
    var optProf = this.getProfessor(id);
    return optProf
        .map(
            p ->
                p.getProfessorImage() != null
                    && p.getProfessorImage().getFilename() != null
                    && !p.getProfessorImage().getFilename().isBlank())
        .orElseGet(() -> false);
  }

  public Page<Professor> findProfessorsByFacultyId(UUID id, Pageable pageable) {
    return this.professorRepository.findAllByFaculty_Id(id, pageable);
  }

  public Page<Professor> findProfessorByFacultyIdAndName(UUID id, String name, Pageable pageable) {
    return this.professorRepository.findAllByFaculty_IdAndNameIgnoreCase(id, name, pageable);
  }

  public Page<Professor> findProfessoryByName(String name, Pageable pageable) {
    return this.professorRepository.findAllByNameContainingIgnoreCase(name, pageable);
  }

  public Map<String, UUID> findFirstIdByFuzzyNames(String[] names) {
    Map<String, UUID> map = new HashMap<>();
    for (String name : names) {
      var prof = this.professorRepository.findFirstIdByNameContainsIgnoreCase(name);
      if (prof.isPresent()) {
        map.put(name, prof.get());
      } else {
        map.put(name, null);
      }
    }
    return map;
  }
}
