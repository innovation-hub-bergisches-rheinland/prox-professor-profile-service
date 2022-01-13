package de.innovationhub.prox.professorprofileservice.domain.professor;


import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProfessorImage {
  private String filename;
}
