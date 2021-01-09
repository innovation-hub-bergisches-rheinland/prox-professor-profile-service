package de.innovationhub.prox.professorprofileservice.application.config;

import com.netflix.discovery.EurekaClient;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

/** WORKAROUND for https://github.com/springfox/springfox/issues/3483 */
@Component
public class SpringfoxSwaggerHostResolver implements WebMvcOpenApiTransformationFilter {

  private final EurekaClient eurekaClient;

  public SpringfoxSwaggerHostResolver(@Qualifier("eurekaClient") EurekaClient eurekaClient) {
    this.eurekaClient = eurekaClient;
  }

  private String getApiGatwayUrl() {
    try {
      return eurekaClient.getNextServerFromEureka("api-gateway", false).getHomePageUrl();
    } catch (Exception exception) {
      return "http://api.prox.innovation-hub.de";
    }
  }

  @Override
  public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
    OpenAPI swagger = context.getSpecification();
    Server server = new Server();
    server.setUrl(getApiGatwayUrl());
    swagger.setServers(Arrays.asList(server));
    return swagger;
  }

  @Override
  public boolean supports(DocumentationType docType) {
    return docType.equals(DocumentationType.OAS_30);
  }
}
