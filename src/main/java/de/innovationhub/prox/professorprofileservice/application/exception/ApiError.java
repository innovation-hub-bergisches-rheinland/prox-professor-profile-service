package de.innovationhub.prox.professorprofileservice.application.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/** API Exception schema */
@Data
@AllArgsConstructor
public class ApiError {
  private int code;
  private String error;
  private String message;
}
