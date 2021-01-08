package de.innovationhub.prox.professorprofileservice.domain.professor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;

@Embeddable
@Data
@NoArgsConstructor
public class ProfileImage {
  @Lob @Column private byte[] data;

  public ProfileImage(byte[] data) {
    setData(data);
  }

  public void setData(byte[] data) {
    if (!Base64.isBase64(data)) {
      this.data = Base64.encodeBase64(data);
    } else {
      this.data = data;
    }
  }

  public byte[] getData() {
    return Base64.decodeBase64(this.data);
  }
}
