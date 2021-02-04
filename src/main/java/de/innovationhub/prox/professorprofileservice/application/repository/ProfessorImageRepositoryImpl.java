package de.innovationhub.prox.professorprofileservice.application.repository;

import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImageRepository;
import de.innovationhub.prox.professorprofileservice.util.ImageUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class ProfessorImageRepositoryImpl implements ProfessorImageRepository {

  private static final Logger logger = LoggerFactory.getLogger(ProfessorImageRepositoryImpl.class);

  /**
   * Directory where the images are stored Can be configured via {@code profile.image.directory}
   * property Defaults to {@code ./data/img}
   */
  private final Path imageDirectory;

  private final ResourceLoader resourceLoader;
  private final ImageUtils imageUtils;

  public ProfessorImageRepositoryImpl(
      @Value("${profile.image.directory:./data/img}") String imageDirectory,
      ResourceLoader resourceLoader,
      ImageUtils imageUtils)
      throws IOException {
    this.resourceLoader = resourceLoader;
    this.imageUtils = imageUtils;
    var directory = Paths.get(imageDirectory); // Open image directory
    if (Files.notExists(directory)) {
      logger.info("Directory {} does not exist, creating directory structure", directory);
      Files.createDirectories(directory);
    }
    // Throw error when provided directory is not a directory or is not writeable
    if (!Files.isDirectory(directory) || !Files.isWritable(directory)) {
      logger.error("{} is not a directory or is not writable", directory);
      throw new IOException(directory + " is not a directory or not writable");
    }
    this.imageDirectory = directory;
  }

  @Override
  public Optional<byte[]> getProfessorImage(Path filepath) {
    var path = imageDirectory.resolve(filepath);
    if (Files.exists(path)) {
      try {
        return Optional.of(Files.readAllBytes(path));
      } catch (IOException e) {
        e.printStackTrace();
        // Exception Handling can be done here
      }
    }
    return Optional.empty();
  }

  @Override
  public String saveProfessorImage(byte[] data) throws IOException {
    var pngData = imageUtils.convertAndResizeToPng(data);
    var file = imageDirectory.resolve(UUID.randomUUID().toString() + ".png");
    return Files.write(file, pngData).getFileName().toString();
  }

  @Override
  public boolean imageExists(Path filepath) {
    return filepath != null && Files.exists(imageDirectory.resolve(filepath));
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
  public boolean deleteProfessorImage(Path filepath) throws IOException {
    var file = imageDirectory.resolve(filepath);
    return Files.deleteIfExists(file);
  }
}
