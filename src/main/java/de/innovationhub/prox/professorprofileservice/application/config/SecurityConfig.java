package de.innovationhub.prox.professorprofileservice.application.config;


import de.innovationhub.prox.professorprofileservice.application.security.AuthenticationUtils;
import de.innovationhub.prox.professorprofileservice.application.security.KeycloakAuthenticationUtils;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider =
        this.keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  public AuthenticationUtils authenticationUtils() {
    return new KeycloakAuthenticationUtils();
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/**")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/professors/**")
        .access("hasRole('professor')")
        .antMatchers(HttpMethod.PUT, "/professors/{id}/**")
        .access(
            "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(request, #id)")
        .antMatchers(HttpMethod.PUT, "/professors/{id}/faculty/**")
        .access(
            "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(request, #id)")
        .antMatchers(HttpMethod.POST, "/professors/{id}/image/**")
        .access(
            "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(request, #id)")
        .antMatchers(HttpMethod.DELETE, "/professors/{id}/image/**")
        .access(
            "hasRole('professor') and @authenticationUtils.compareUserIdAndRequestId(request, #id)")
        .anyRequest()
        .denyAll();
  }
}
