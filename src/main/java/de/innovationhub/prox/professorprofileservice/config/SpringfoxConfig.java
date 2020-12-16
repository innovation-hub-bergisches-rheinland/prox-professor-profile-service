package de.innovationhub.prox.professorprofileservice.config;

import com.netflix.discovery.EurekaClient;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringfoxConfig {

  private final EurekaClient eurekaClient;

  public SpringfoxConfig(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
      }
    };
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
        .forCodeGeneration(true)
        .securityContexts(Collections.singletonList(securityContext()))
        .securitySchemes(Collections.singletonList(jwtScheme()))
        .groupName("professor-profile-service")
        .select()
        .paths(PathSelectors.ant("/faculties/**").or(PathSelectors.ant("/professors/**")))
        .build();
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(List.of(defaultAuth()))
        .operationSelector(o -> o.httpMethod() != HttpMethod.GET)
        .build();
  }

  private SecurityReference defaultAuth() {
    return SecurityReference.builder().scopes(new AuthorizationScope[0]).reference("JWT").build();
  }

  private SecurityScheme jwtScheme() {
    return HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("JWT").build();
  }
}