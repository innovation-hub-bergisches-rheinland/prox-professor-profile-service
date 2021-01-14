package de.innovationhub.prox.professorprofileservice.domain.professor;

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
public class ProfessorImageFileRepositoryImpl implements ProfessorImageFileRepository {

  private final String imageDirectory;

  public ProfessorImageFileRepositoryImpl(
      @Value("${profile.image.directory:./data/img}") String imageDirectory) throws IOException {
    this.imageDirectory = imageDirectory;
    var directory = Paths.get(imageDirectory);
    if (Files.notExists(directory)) {
      Files.createDirectories(directory);
    }
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
    var file =
        Paths.get(imageDirectory, UUID.randomUUID().toString() + "." + detectFileEnding(data));
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

  private String detectFileEnding(byte[] data) {
    try {
      ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));

      Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

      if (imageReaders.hasNext()) {
        return imageReaders.next().getFormatName();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  @Override
  public boolean deleteProfessorImage(String filename) throws IOException {
    var file = Paths.get(imageDirectory, filename);
    return Files.deleteIfExists(file);
  }
}
