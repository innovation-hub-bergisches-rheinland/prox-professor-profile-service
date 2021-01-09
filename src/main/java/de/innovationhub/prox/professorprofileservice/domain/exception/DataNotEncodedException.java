package de.innovationhub.prox.professorprofileservice.domain.exception;

/**
 * Unchecked exception thrown when the getter of the profile image is called and the data is somehow
 * not Base64 encoded
 */
public class DataNotEncodedException extends RuntimeException {
  public DataNotEncodedException(String message) {
    super(message);
  }
}
