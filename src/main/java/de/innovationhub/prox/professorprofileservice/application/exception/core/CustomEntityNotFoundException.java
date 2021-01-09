package de.innovationhub.prox.professorprofileservice.application.exception.core;

/**
 * Custom unchecked implementation of an EntityNotFoundException which can safely be thrown inside
 * controllers and therefore handled
 */
public abstract class CustomEntityNotFoundException extends CustomRuntimeException {

  protected CustomEntityNotFoundException(String message) {
    super(message);
  }
}
