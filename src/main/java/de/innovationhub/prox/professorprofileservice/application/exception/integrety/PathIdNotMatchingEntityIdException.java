package de.innovationhub.prox.professorprofileservice.application.exception.integrety;


import de.innovationhub.prox.professorprofileservice.application.exception.core.CustomRuntimeException;

public class PathIdNotMatchingEntityIdException extends CustomRuntimeException {
  public PathIdNotMatchingEntityIdException() {
    super("The Path ID segment does not match entity ID");
  }
}
