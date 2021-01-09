package de.innovationhub.prox.professorprofileservice.application.exception.security;

import de.innovationhub.prox.professorprofileservice.application.exception.core.CustomRuntimeException;

public class SecurityException extends CustomRuntimeException {
  public SecurityException(String message) {
    super(message);
  }
}
