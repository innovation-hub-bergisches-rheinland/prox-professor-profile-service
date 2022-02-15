package de.innovationhub.prox.professorprofileservice.application.net.client;


import de.innovationhub.prox.professorprofileservice.application.net.schema.ProjectStats;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "project-service", url = "${prox.services.project-service.url}")
public interface ProjectServiceClient {
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/projects/search/findProjectStatsOfCreator",
      consumes = "application/json")
  ResponseEntity<ProjectStats> findProjectStatsOfCreator(@RequestParam("creatorId") UUID uuid);
}
