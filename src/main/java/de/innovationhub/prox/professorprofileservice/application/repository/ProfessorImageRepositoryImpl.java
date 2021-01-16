package de.innovationhub.prox.professorprofileservice.application.repository;

import de.innovationhub.prox.professorprofileservice.domain.professor.ProfessorImageRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProfessorImageRepositoryImpl implements ProfessorImageRepository {

  /**
   * Directory where the images are stored Can be configured via {@code profile.image.directory}
   * property Defaults to {@code ./data/img}
   */
  private final String imageDirectory;

  public ProfessorImageRepositoryImpl(
      @Value("${profile.image.directory:./data/img}") String imageDirectory) throws IOException {
    this.imageDirectory = imageDirectory;
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
  public byte[] getProfessorImage(String filename) throws IOException {
    var file = Paths.get(imageDirectory, filename);
    return Files.readAllBytes(file);
  }

  @Override
  public String saveProfessorImage(byte[] data) throws IOException {
    var file = Paths.get(imageDirectory, UUID.randomUUID().toString() + detectFileEnding(data));
    return Files.write(file, data).getFileName().toString();
  }

  @Override
  public boolean imageExists(String filename) {
    if (filename != null && !filename.isBlank()) {
      var file = Paths.get(imageDirectory, filename);
      return Files.exists(file);
    }
    return false;
  }

  /**
   * Detects the file ending of image data
   *
   * @param data image data
   * @return file ending of image data or an empty string when the type somehow can not detecte
   * @throws IOException on I/O error
   */
  private String detectFileEnding(byte[] data) throws IOException {
    ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));

    Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

    if (imageReaders.hasNext()) {
      return "." + imageReaders.next().getFormatName();
    }
    return "";
  }

  @Override
  public boolean deleteProfessorImage(String filename) throws IOException {
    var file = Paths.get(imageDirectory, filename);
    return Files.deleteIfExists(file);
  }
}
