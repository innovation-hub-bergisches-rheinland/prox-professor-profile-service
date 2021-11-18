package de.innovationhub.prox.professorprofileservice.application.exception.professor;


import de.innovationhub.prox.professorprofileservice.application.exception.core.CustomEntityNotFoundException;

/** Thrown when a professor entity could not found */
public class ProfessorNotFoundException extends CustomEntityNotFoundException {

  public ProfessorNotFoundException() {
    super("Could not find professor");
  }
}
