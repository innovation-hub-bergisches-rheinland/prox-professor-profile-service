package de.innovationhub.prox.professorprofileservice.professor;

import java.net.URL;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class ContactInformation {
  private String room;
  private String consultationHour;
  private String email;
  private String telephone;
  private URL homepage;
}
