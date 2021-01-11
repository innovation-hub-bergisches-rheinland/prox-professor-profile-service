package de.innovationhub.prox.professorprofileservice.application.controller.faculty;

import static org.assertj.core.api.Assertions.assertThat;

import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class FacultyJsonTest {

  @Autowired private JacksonTester<Faculty> json;

  @Test
  void testSerialize() throws Exception {
    var faculty = new Faculty("F99", "Fakultät für Tests und Qualitätssicherung");

    JsonContent<Faculty> result = this.json.write(faculty);

    System.out.println(result.getJson());

    assertThat(result).extractingJsonPathValue("$.id").isEqualTo(faculty.getId().toString());
    assertThat(result)
        .extractingJsonPathValue("$.abbreviation")
        .isEqualTo(faculty.getAbbreviation());
    assertThat(result).extractingJsonPathValue("$.name").isEqualTo(faculty.getName());
  }

  @Test
  void testDeserialize() throws Exception {
    String jsonContent =
        "{\"id\":\"31715d32-f4a0-4a2b-8e91-3e46fbc30e99\","
            + "\"name\":\"Fakultät für Tests und Qualitätssicherung\","
            + "\"abbreviation\":\"F99\""
            + "}";

    Faculty faculty = this.json.parse(jsonContent).getObject();

    assertThat(faculty.getId()).isEqualTo(UUID.fromString("31715d32-f4a0-4a2b-8e91-3e46fbc30e99"));
    assertThat(faculty.getName()).isEqualTo("Fakultät für Tests und Qualitätssicherung");
    assertThat(faculty.getAbbreviation()).isEqualTo("F99");
  }
}
