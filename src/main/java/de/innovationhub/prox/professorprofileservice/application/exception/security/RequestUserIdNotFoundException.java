package de.innovationhub.prox.professorprofileservice.application.exception.security;

public class RequestUserIdNotFoundException extends SecurityException {
  public RequestUserIdNotFoundException() {
    super("Could not obtain user from request");
  }
}
