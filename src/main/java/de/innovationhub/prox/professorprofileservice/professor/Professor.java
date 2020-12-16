package de.innovationhub.prox.professorprofileservice.professor;

import java.net.URL;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class Professor {
  @Id private UUID id;
  @ManyToOne private Faculty faculty;
  private String room;
  private String consultationHour;
  private String email;
  private String telephone;
  private URL homepage;

  public Professor(UUID id) {
    this.id = id;
  }
}
