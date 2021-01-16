package de.innovationhub.prox.professorprofileservice.util;

import java.io.IOException;

public interface ImageUtils {
  String detectFileEnding(byte[] data) throws IOException;

  byte[] convertToPng(byte[] data) throws IOException;
}
