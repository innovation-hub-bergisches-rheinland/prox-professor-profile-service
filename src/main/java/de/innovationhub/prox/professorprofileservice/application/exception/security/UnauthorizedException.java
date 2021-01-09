package de.innovationhub.prox.professorprofileservice.application.exception.security;

public class UnauthorizedException extends SecurityException {

  public UnauthorizedException() {
    super("Not authorized");
  }
}
