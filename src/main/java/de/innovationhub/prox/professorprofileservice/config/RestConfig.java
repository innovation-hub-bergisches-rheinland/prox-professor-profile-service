package de.innovationhub.prox.professorprofileservice.config;

import de.innovationhub.prox.professorprofileservice.professor.Faculty;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

  private final EntityManager entityManager;

  public RestConfig(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.exposeIdsFor(
        this.entityManager.getMetamodel().getEntities().stream()
            .map(Type::getJavaType)
            .toArray(Class[]::new));
    config
        .getExposureConfiguration()
        .forDomainType(Faculty.class)
        .withItemExposure(
            ((metdata, httpMethods) ->
                httpMethods.disable(
                    HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.POST, HttpMethod.PUT)));
  }
}
