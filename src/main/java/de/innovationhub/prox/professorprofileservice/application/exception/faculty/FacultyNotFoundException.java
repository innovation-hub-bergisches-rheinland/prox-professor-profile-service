package de.innovationhub.prox.professorprofileservice.application.exception.faculty;

import javax.persistence.EntityNotFoundException;

public class FacultyNotFoundException extends EntityNotFoundException {

  public FacultyNotFoundException() {
    super("Could not find faculty");
  }
}
