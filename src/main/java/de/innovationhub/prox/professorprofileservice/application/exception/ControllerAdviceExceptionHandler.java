package de.innovationhub.prox.professorprofileservice.application.exception;

import de.innovationhub.prox.professorprofileservice.application.exception.core.CustomEntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** General purpose exception handler which takes care of exceptions thrown in any controller */
@ControllerAdvice
public class ControllerAdviceExceptionHandler {

  /**
   * Responds with a proper API error when a entity
   *
   * @param e Exception thrown (when thrown in a controller it is auto-bound
   * @return ResponseEntity with status 404 and ApiError mapping
   */
  @ExceptionHandler({CustomEntityNotFoundException.class})
  public ResponseEntity<ApiError> numberFormatException(CustomEntityNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(HttpStatus.NOT_FOUND.value(), e.getType(), e.getMessage()));
  }
}
