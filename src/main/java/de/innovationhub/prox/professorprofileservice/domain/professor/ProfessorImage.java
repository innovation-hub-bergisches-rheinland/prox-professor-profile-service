package de.innovationhub.prox.professorprofileservice.domain.professor;

import de.innovationhub.prox.professorprofileservice.domain.core.AbstractEntity;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProfessorImage extends AbstractEntity {
  private String filename;
}
