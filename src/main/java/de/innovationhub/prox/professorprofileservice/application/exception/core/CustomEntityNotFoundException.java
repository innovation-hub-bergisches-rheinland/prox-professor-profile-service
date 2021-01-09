package de.innovationhub.prox.professorprofileservice.application.exception.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Custom unchecked implementation of an EntityNotFoundException which can safely be thrown inside
 * controllers and therefore handled
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class CustomEntityNotFoundException extends RuntimeException {

  private final String type;
  private final String message;

  protected CustomEntityNotFoundException(String type, String message) {
    this.type = type;
    this.message = message;
  }

  protected CustomEntityNotFoundException(String message) {
    this.type = this.getClass().getSimpleName();
    this.message = message;
  }
}
