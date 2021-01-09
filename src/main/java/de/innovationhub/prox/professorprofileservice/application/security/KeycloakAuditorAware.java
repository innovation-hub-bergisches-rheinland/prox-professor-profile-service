package de.innovationhub.prox.professorprofileservice.application.security;

import java.util.Optional;
import java.util.UUID;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** AuditorAware implementation for a Keycloak managed authentication */
@Component
public class KeycloakAuditorAware implements AuditorAware<UUID> {

  @Override
  public Optional<UUID> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .map(KeycloakAuthenticationToken.class::cast)
        .map(KeycloakAuthenticationToken::getAccount)
        .map(OidcKeycloakAccount::getKeycloakSecurityContext)
        .map(KeycloakSecurityContext::getToken)
        .map(AccessToken::getSubject)
        .map(UUID::fromString);
  }
}
