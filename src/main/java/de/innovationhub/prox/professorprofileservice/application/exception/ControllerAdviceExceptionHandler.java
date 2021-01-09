package de.innovationhub.prox.professorprofileservice.application.exception;

import de.innovationhub.prox.professorprofileservice.application.exception.core.CustomEntityNotFoundException;
import de.innovationhub.prox.professorprofileservice.application.exception.integrety.PathIdNotMatchingEntityIdException;
import de.innovationhub.prox.professorprofileservice.application.exception.security.RequestUserIdNotFoundException;
import de.innovationhub.prox.professorprofileservice.application.exception.security.SecurityException;
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

  @ExceptionHandler({PathIdNotMatchingEntityIdException.class})
  public ResponseEntity<ApiError> pathIdNotMatchingEntityIdException(
      PathIdNotMatchingEntityIdException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError(HttpStatus.BAD_REQUEST.value(), e.getType(), e.getMessage()));
  }

  @ExceptionHandler({SecurityException.class})
  public ResponseEntity<ApiError> requestUserIdNotFoundException(RequestUserIdNotFoundException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError(HttpStatus.UNAUTHORIZED.value(), e.getType(), e.getMessage()));
  }
}
