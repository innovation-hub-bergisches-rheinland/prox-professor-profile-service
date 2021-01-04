package de.innovationhub.prox.professorprofileservice.domain.faculty;

import de.innovationhub.prox.professorprofileservice.domain.core.AbstractEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Faculty extends AbstractEntity {

  @Column(length = 3)
  @Size(min = 3, max = 3)
  @NotBlank
  private String abbreviation;

  @NotBlank private String name;
}
