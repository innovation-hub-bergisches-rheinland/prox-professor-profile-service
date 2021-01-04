package de.innovationhub.prox.professorprofileservice.professor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class ProfessorJsonTest {

  @Autowired private JacksonTester<Professor> json;

  @Test
  void testSerialize() throws Exception {
    var professor =
        new Professor(
            "Prof. Dr. Xavier Tester",
            new Faculty("F10", "Fakultät für Informatik und Ingenieurwissenschaften"),
            new ContactInformation(
                "2.100",
                "Mo. 18-20 Uhr",
                "example@example.org",
                "+12 3456 789",
                new URI("http://example.org").toURL()),
            new ProfileImage(),
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")),
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")),
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");

    JsonContent<Professor> result = this.json.write(professor);

    System.out.println(result.getJson());

    assertThat(result).extractingJsonPathValue("$.id").isEqualTo(professor.getId().toString());
    assertThat(result).extractingJsonPathValue("$.name").isEqualTo(professor.getName());
    assertThat(result)
        .extractingJsonPathValue("$.contactInformation.room")
        .isEqualTo(professor.getContactInformation().getRoom());
    assertThat(result)
        .extractingJsonPathValue("$.contactInformation.consultationHour")
        .isEqualTo(professor.getContactInformation().getConsultationHour());
    assertThat(result)
        .extractingJsonPathValue("$.contactInformation.email")
        .isEqualTo(professor.getContactInformation().getEmail());
    assertThat(result)
        .extractingJsonPathValue("$.contactInformation.telephone")
        .isEqualTo(professor.getContactInformation().getTelephone());
    assertThat(result)
        .extractingJsonPathValue("$.contactInformation.homepage")
        .isEqualTo(professor.getContactInformation().getHomepage().toString());
    assertThat(result)
        .extractingJsonPathArrayValue("$.researchSubjects")
        .hasSameSizeAs(professor.getResearchSubjects());
    assertThat(result)
        .extractingJsonPathArrayValue("$.researchSubjects[*].subject")
        .containsExactlyInAnyOrderElementsOf(
            professor.getResearchSubjects().stream()
                .map(ResearchSubject::getSubject)
                .collect(Collectors.toList()));
    assertThat(result)
        .extractingJsonPathArrayValue("$.publications")
        .hasSameSizeAs(professor.getPublications());
    assertThat(result)
        .extractingJsonPathArrayValue("$.publications[*].publication")
        .containsExactlyInAnyOrderElementsOf(
            professor.getPublications().stream()
                .map(Publication::getPublication)
                .collect(Collectors.toList()));
    assertThat(result).extractingJsonPathValue("$.vita").isEqualTo(professor.getVita());

    assertThat(result).doesNotHaveJsonPath("$.faculty");
  }

  @Test
  void testDeserialize() throws Exception {
    String jsonContent =
        "{\"id\":\"31715d32-f4a0-4a2b-8e91-3e46fbc30e99\","
            + "\"name\":\"Prof. Dr. Xavier Tester\","
            + "\"contactInformation\":{"
            + "\"room\":\"2.100\","
            + "\"consultationHour\":\"Mo. 18-20 Uhr\","
            + "\"email\":\"example@example.org\","
            + "\"telephone\":\"+12 3456 789\","
            + "\"homepage\":\"http://example.org\"},"
            + "\"researchSubjects\":["
            + "{\"subject\":\"IoT\"},"
            + "{\"subject\":\"Mobile\"}"
            + "],"
            + "\"publications\":["
            + "{\"publication\":\"Book\"},"
            + "{\"publication\":\"Paper 1\"},"
            + "{\"publication\":\"Paper 2\"}"
            + "],"
            + "\"vita\":\"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\"}";

    Professor professor = this.json.parse(jsonContent).getObject();

    assertThat(professor.getId())
        .isEqualTo(UUID.fromString("31715d32-f4a0-4a2b-8e91-3e46fbc30e99"));
    assertThat(professor.getName()).isEqualTo("Prof. Dr. Xavier Tester");
    assertThat(professor.getContactInformation()).isNotNull();
    assertThat(professor.getContactInformation().getConsultationHour()).isEqualTo("Mo. 18-20 Uhr");
    assertThat(professor.getContactInformation().getEmail()).isEqualTo("example@example.org");
    assertThat(professor.getContactInformation().getTelephone()).isEqualTo("+12 3456 789");
    assertThat(professor.getContactInformation().getHomepage())
        .isEqualTo(new URI("http://example.org").toURL());
    assertThat(professor.getContactInformation().getRoom()).isEqualTo("2.100");
    assertThat(professor.getResearchSubjects())
        .containsExactlyInAnyOrderElementsOf(
            Arrays.asList(new ResearchSubject("IoT"), new ResearchSubject("Mobile")));
    assertThat(professor.getPublications())
        .containsExactlyInAnyOrderElementsOf(
            Arrays.asList(
                new Publication("Book"), new Publication("Paper 1"), new Publication("Paper 2")));
    assertThat(professor.getVita())
        .isEqualTo(
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
  }
}
