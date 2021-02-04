package de.innovationhub.prox.professorprofileservice.util;

import java.io.IOException;

public interface ImageUtils {
  String detectFileEnding(byte[] data) throws IOException;

  byte[] convertAndResizeToPng(byte[] data, int width, int height) throws IOException;

  default byte[] convertAndResizeToPng(byte[] data) throws IOException {
    return convertAndResizeToPng(data, 200, 200);
  }
}
