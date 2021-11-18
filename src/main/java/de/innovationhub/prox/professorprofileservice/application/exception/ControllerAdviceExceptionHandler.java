package de.innovationhub.prox.professorprofileservice.application.exception;


import de.innovationhub.prox.professorprofileservice.application.exception.core.CustomEntityNotFoundException;
import de.innovationhub.prox.professorprofileservice.application.exception.integrety.PathIdNotMatchingEntityIdException;
import de.innovationhub.prox.professorprofileservice.application.exception.security.RequestUserIdNotFoundException;
import de.innovationhub.prox.professorprofileservice.application.exception.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** General purpose exception handler which takes care of exceptions thrown in any controller */
@ControllerAdvice
public class ControllerAdviceExceptionHandler {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<ApiError> accessDeniedException(AccessDeniedException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ApiError(HttpStatus.FORBIDDEN.value(), "Access Denied", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ApiError(
                HttpStatus.BAD_REQUEST.value(), e.getParameter() + " is invalid", e.getMessage()));
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ApiError> unknownException(Exception e) {
    logger.error("Unknown Exception", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal error", e.getMessage()));
  }
}
