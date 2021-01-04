package de.innovationhub.prox.professorprofileservice.domain.professor;

import java.net.URL;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContactInformation {
  private String room;
  private String consultationHour;
  private String email;
  private String telephone;
  private URL homepage;
}
