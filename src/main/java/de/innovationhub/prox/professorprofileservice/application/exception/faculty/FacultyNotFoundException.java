package de.innovationhub.prox.professorprofileservice.application.exception.faculty;


import de.innovationhub.prox.professorprofileservice.application.exception.core.CustomEntityNotFoundException;

/** Thrown when a faculty entity could not found */
public class FacultyNotFoundException extends CustomEntityNotFoundException {

  public FacultyNotFoundException() {
    super("Could not find faculty");
  }
}
