package de.innovationhub.prox.professorprofileservice.application.config;


import de.innovationhub.prox.professorprofileservice.application.security.KeycloakAuditorAware;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfig {

  @Bean
  AuditorAware<UUID> auditorProvider() {
    return new KeycloakAuditorAware();
  }
}
