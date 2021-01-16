package de.innovationhub.prox.professorprofileservice.domain.professor;

import java.io.IOException;
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
   * @param filename filename in which the image is stored
   * @return image data
   * @throws IOException on I/O error
   */
  Optional<byte[]> getProfessorImage(String filename);

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
   * @param filename filename to delete
   * @return true if deletion was successful, otherwise false
   * @throws IOException on I/O error
   */
  boolean deleteProfessorImage(String filename) throws IOException;

  /**
   * Checks if the image file exists
   *
   * @param filename filename to check
   * @return true if it exists otherwise false
   */
  boolean imageExists(String filename);

  Optional<byte[]> getDefaultImage();
}
