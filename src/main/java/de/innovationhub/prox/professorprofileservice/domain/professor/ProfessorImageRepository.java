package de.innovationhub.prox.professorprofileservice.domain.professor;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * A repository which is capable of resolving a ProfessorImage into image data The default
 * implementation is found in {@link
 * de.innovationhub.prox.professorprofileservice.application.repository.ProfessorImageRepositoryImpl}
 */
public interface ProfessorImageRepository {

  /**
   * Load the image data from filename
   *
   * @param filepath filename in which the image is stored
   * @return image data
   */
  Optional<byte[]> getProfessorImage(Path filepath);

  default Optional<byte[]> getProfessorImage(String filepath) {
    return getProfessorImage(Paths.get(filepath));
  }

  /**
   * Save the image data in a file and return the filename
   *
   * @param data image data
   * @return filename (generated)
   * @throws IOException on I/O error
   */
  String saveProfessorImage(byte[] data) throws IOException;

  /**
   * Deletes the image file at provided filename
   *
   * @param filepath filename to delete
   * @return true if deletion was successful, otherwise false
   * @throws IOException on I/O error
   */
  boolean deleteProfessorImage(Path filepath) throws IOException;

  default boolean deleteProfessorImage(String filepath) throws IOException {
    return deleteProfessorImage(Paths.get(filepath));
  }

  /**
   * Checks if the image file exists
   *
   * @param filepath filename to check
   * @return true if it exists otherwise false
   */
  boolean imageExists(Path filepath);

  default boolean imageExists(String filepath) {
    return imageExists(Paths.get(filepath));
  }

  Optional<byte[]> getDefaultImage();
}
