package de.innovationhub.prox.professorprofileservice.domain.faculty;

import de.innovationhub.prox.professorprofileservice.domain.core.AbstractEntity;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"abbreviation", "name"}))
public class Faculty extends AbstractEntity {

  @NotBlank private String abbreviation;

  @NotBlank private String name;
}
