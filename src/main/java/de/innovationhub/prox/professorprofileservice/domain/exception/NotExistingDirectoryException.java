package de.innovationhub.prox.professorprofileservice.domain.exception;

import java.io.IOException;

public class NotExistingDirectoryException extends IOException {
  public NotExistingDirectoryException(String message) {
    super(message);
  }
}
