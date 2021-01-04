package de.innovationhub.prox.professorprofileservice.domain.professor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImage {
  @Column(length = 1000, nullable = true)
  private byte[] data = null;
}
