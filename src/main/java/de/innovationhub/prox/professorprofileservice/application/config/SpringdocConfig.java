package de.innovationhub.prox.professorprofileservice.application.config;

import com.netflix.discovery.EurekaClient;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {

  EurekaClient eurekaClient;

  public SpringdocConfig(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
  }

  @Bean
  public GroupedOpenApi userOpenApi() {
    String paths[] = {"/**"};
    return GroupedOpenApi.builder().group("professor-profile-service").pathsToMatch(paths).build();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addServersItem(
            new Server()
                .url("https://api.prox.innovation-hub.de")
                .description("PROX Production API"))
        .addServersItem(
            new Server()
                .url("https://dev.api.prox.innovation-hub.de")
                .description("PROX Development API"));
  }
}
