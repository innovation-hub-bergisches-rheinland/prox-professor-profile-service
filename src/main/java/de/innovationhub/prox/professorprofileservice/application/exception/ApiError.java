package de.innovationhub.prox.professorprofileservice.application.exception;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/** API Exception schema */
@Data
@AllArgsConstructor
public class ApiError {
  @NotNull private int code;
  @NotNull private String error;
  @NotNull private String message;
}
