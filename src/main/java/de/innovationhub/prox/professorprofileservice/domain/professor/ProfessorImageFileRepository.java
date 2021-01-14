package de.innovationhub.prox.professorprofileservice.domain.professor;

import java.io.IOException;

public interface ProfessorImageFileRepository {
  byte[] getProfessorImage(String filename) throws IOException;

  String saveProfessorImage(byte[] data) throws IOException;

  boolean deleteProfessorImage(String filename) throws IOException;

  boolean imageExists(String filename);
}
