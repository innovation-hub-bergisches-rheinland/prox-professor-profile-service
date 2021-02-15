package de.innovationhub.prox.professorprofileservice.application.controller.professor;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.professorprofileservice.domain.dto.ProfessorOverviewDto;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
public class ProfessorOverviewJsonTest {

  @Autowired private JacksonTester<ProfessorOverviewDto> json;

  @Test
  void testSerialize() throws Exception {
    var professorOverviewDto =
        new ProfessorOverviewDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Prof. Dr. Xavier Tester",
            "Fakultät für Informatik und Ingenieurwissenschaften",
            "IoT",
            new String[] {"IoT", "Mobile"},
            44,
            22,
            11);

    JsonContent<ProfessorOverviewDto> result = this.json.write(professorOverviewDto);

    System.out.println(result.getJson());

    assertThat(result)
        .extractingJsonPathValue("$.id")
        .isEqualTo(professorOverviewDto.getId().toString());
    assertThat(result).extractingJsonPathValue("$.name").isEqualTo(professorOverviewDto.getName());
    assertThat(result)
        .extractingJsonPathValue("$.facultyId")
        .isEqualTo(professorOverviewDto.getFacultyId().toString());
    assertThat(result)
        .extractingJsonPathValue("$.faculty")
        .isEqualTo(professorOverviewDto.getFaculty());
    assertThat(result)
        .extractingJsonPathValue("$.mainSubject")
        .isEqualTo(professorOverviewDto.getMainSubject());
    assertThat(result)
        .extractingJsonPathArrayValue("$.researchSubjects[*]")
        .contains(professorOverviewDto.getResearchSubjects());
    assertThat(result)
        .extractingJsonPathValue("$.sumRunningProjects")
        .isEqualTo(professorOverviewDto.getSumRunningProjects());
    assertThat(result)
        .extractingJsonPathValue("$.sumFinishedProjects")
        .isEqualTo(professorOverviewDto.getSumFinishedProjects());
    assertThat(result)
        .extractingJsonPathValue("$.sumAvailableProjects")
        .isEqualTo(professorOverviewDto.getSumAvailableProjects());
  }

  @Test
  void testDeserialize() throws Exception {
    String jsonContent =
        "{\n"
            + "  \"id\": \"f6b81e40-6bd3-4f51-a65c-b38e805f1125\",\n"
            + "  \"facultyId\": \"f6b81e40-6bd3-4f51-a65c-b38e805f1126\",\n"
            + "  \"name\": \"Prof. Dr. Max Mustermann\",\n"
            + "  \"faculty\": \"Fakultaet Test\",\n"
            + "  \"mainSubject\": \"Qualitätssicherung\",\n"
            + "  \"researchSubjects\": [\n"
            + "    \"Qualitätssicherung\",\n"
            + "    \"Qualitätsmanagement\",\n"
            + "    \"Produktionsautomation\",\n"
            + "    \"Intralogistik\",\n"
            + "    \"Internet Of Things\"\n"
            + "  ],\n"
            + "  \"sumRunningProjects\": 25,\n"
            + "  \"sumFinishedProjects\": 3,\n"
            + "  \"sumAvailableProjects\": 1\n"
            + "}";

    ProfessorOverviewDto professor = this.json.parse(jsonContent).getObject();

    assertThat(professor.getId())
        .isEqualTo(UUID.fromString("f6b81e40-6bd3-4f51-a65c-b38e805f1125"));
    assertThat(professor.getName()).isEqualTo("Prof. Dr. Max Mustermann");
    assertThat(professor.getFacultyId())
        .isEqualTo(UUID.fromString("f6b81e40-6bd3-4f51-a65c-b38e805f1126"));
    assertThat(professor.getFaculty()).isEqualTo("Fakultaet Test");
    assertThat(professor.getMainSubject()).isEqualTo("Qualitätssicherung");
    assertThat(professor.getSumAvailableProjects()).isEqualTo(1);
    assertThat(professor.getSumFinishedProjects()).isEqualTo(3);
    assertThat(professor.getSumRunningProjects()).isEqualTo(25);
    assertThat(professor.getResearchSubjects())
        .containsExactlyInAnyOrderElementsOf(
            Arrays.asList(
                "Qualitätssicherung",
                "Qualitätsmanagement",
                "Produktionsautomation",
                "Intralogistik",
                "Internet Of Things"));
  }
}
