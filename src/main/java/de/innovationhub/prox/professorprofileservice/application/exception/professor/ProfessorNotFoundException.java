package de.innovationhub.prox.professorprofileservice.application.exception.professor;

import javax.persistence.EntityNotFoundException;

public class ProfessorNotFoundException extends EntityNotFoundException {

  public ProfessorNotFoundException() {
    super("Could not find professor");
  }

  public ProfessorNotFoundException(String message) {
    super(message);
  }
}
