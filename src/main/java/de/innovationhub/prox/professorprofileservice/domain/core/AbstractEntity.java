package de.innovationhub.prox.professorprofileservice.domain.core;


import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@MappedSuperclass
@Data
@Setter(AccessLevel.NONE)
public class AbstractEntity {

  @Id @NotNull private UUID id;

  protected AbstractEntity() {
    this.id = UUID.randomUUID();
  }
}
