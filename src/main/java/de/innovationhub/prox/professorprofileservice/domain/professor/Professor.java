package de.innovationhub.prox.professorprofileservice.domain.professor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.innovationhub.prox.professorprofileservice.domain.core.AbstractEntity;
import de.innovationhub.prox.professorprofileservice.domain.faculty.Faculty;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class Professor extends AbstractEntity {
  private String name;
  private String affiliation;
  private String mainSubject;
  @JsonIgnore @ManyToOne private Faculty faculty;
  @Embedded private ContactInformation contactInformation;
  @JsonIgnore @Embedded private ProfileImage profileImage;
  @ElementCollection private List<ResearchSubject> researchSubjects;
  @ElementCollection private List<Publication> publications;
  @Lob private String vita;
}
