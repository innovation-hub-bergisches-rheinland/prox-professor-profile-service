package de.innovationhub.prox.professorprofileservice.application.exception;

import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdviceExceptionHandler {
  @ExceptionHandler({EntityNotFoundException.class})
  public ResponseEntity<ApiError> numberFormatException(EntityNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            new ApiError(
                HttpStatus.NOT_FOUND.value(), e.getClass().getSimpleName(), e.getMessage()));
  }
}
