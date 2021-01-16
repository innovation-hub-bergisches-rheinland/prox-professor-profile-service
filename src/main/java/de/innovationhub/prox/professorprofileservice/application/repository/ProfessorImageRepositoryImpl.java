package de.innovationhub.prox.professorprofileservice.application.repository;

import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImageRepository;
import de.innovationhub.prox.professorprofileservice.util.ImageUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class ProfessorImageRepositoryImpl implements ProfessorImageRepository {

  /**
   * Directory where the images are stored Can be configured via {@code profile.image.directory}
   * property Defaults to {@code ./data/img}
   */
  private final String imageDirectory;

  private final ResourceLoader resourceLoader;
  private final ImageUtils imageUtils;

  public ProfessorImageRepositoryImpl(
      @Value("${profile.image.directory:./data/img}") String imageDirectory,
      ResourceLoader resourceLoader,
      ImageUtils imageUtils)
      throws IOException {
    this.imageDirectory = imageDirectory;
    this.resourceLoader = resourceLoader;
    this.imageUtils = imageUtils;
    var directory = Paths.get(imageDirectory); // Open image directory
    if (Files.notExists(directory)) {
      // Create if it not exists
      Files.createDirectories(directory);
    }
    // Throw error when provided directory is not a directory or is not writeable
    if (!Files.isDirectory(directory) || !Files.isWritable(directory)) {
      throw new IOException(directory + " is not a directory or not writable");
    }
  }

  @Override
  public Optional<byte[]> getProfessorImage(String filename) {
    if (filename != null && !filename.isBlank()) {
      var file = Paths.get(imageDirectory, filename);
      try {
        return Optional.of(Files.readAllBytes(file));
      } catch (IOException e) {
        // Exception Handling can be done here
      }
    }
    return Optional.empty();
  }

  @Override
  public String saveProfessorImage(byte[] data) throws IOException {
    var pngData = imageUtils.convertToPng(data);
    var file = Paths.get(imageDirectory, UUID.randomUUID().toString() + ".png");
    return Files.write(file, pngData).getFileName().toString();
  }

  @Override
  public boolean imageExists(String filename) {
    if (filename != null && !filename.isBlank()) {
      var file = Paths.get(imageDirectory, filename);
      return Files.exists(file);
    }
    return false;
  }

  @Override
  public Optional<byte[]> getDefaultImage() {
    try {
      var inputStream =
          this.resourceLoader
              .getResource("classpath:/img/blank-profile-picture.png")
              .getInputStream();
      return Optional.of(IOUtils.toByteArray(inputStream));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public boolean deleteProfessorImage(String filename) throws IOException {
    var file = Paths.get(imageDirectory, filename);
    return Files.deleteIfExists(file);
  }
}
