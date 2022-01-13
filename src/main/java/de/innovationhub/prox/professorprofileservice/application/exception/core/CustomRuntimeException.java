package de.innovationhub.prox.professorprofileservice.application.exception.core;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public abstract class CustomRuntimeException extends RuntimeException {
  private final String type;
  private final String message;

  public CustomRuntimeException(String message) {
    this.type = this.getClass().getSimpleName();
    this.message = message;
  }
}
