/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.professorprofileservice.application.security;


import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;

/** Implements AuthenticationUtils for a Keycloak context */
public class KeycloakAuthenticationUtils implements AuthenticationUtils {

  public Optional<UUID> getUserUUIDFromRequest(HttpServletRequest request) {
    KeycloakAuthenticationToken keycloakAuthenticationToken = getAuthentication(request);
    if (keycloakAuthenticationToken != null) {
      KeycloakSecurityContext keycloakSecurityContext =
          keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext();
      return getSubjectUUIDFromToken(keycloakSecurityContext.getToken());
    }
    return Optional.empty();
  }

  /**
   * Helper Method which casts the principal to a keycloak specific token
   *
   * @param request the request of which the user principal should be casted
   * @return UserPrincipal casted to KeycloakAuthenticationToken
   */
  private KeycloakAuthenticationToken getAuthentication(HttpServletRequest request) {
    HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(request);
    return (KeycloakAuthenticationToken) httpServletRequestWrapper.getUserPrincipal();
  }

  /**
   * Method to extract subjects UserID from Keycloak AccessToken by
   *
   * @param accessToken Keycloak AccessToken which contains subject
   * @return Empty Optional if the AccessToken does not contain a subject or subject is not a valid
   *     UUID
   */
  private Optional<UUID> getSubjectUUIDFromToken(AccessToken accessToken) {
    String subject = accessToken.getSubject();
    try {
      return Optional.of(UUID.fromString(subject));
    } catch (IllegalArgumentException illegalArgumentException) {
      return Optional.empty();
    }
  }
}
