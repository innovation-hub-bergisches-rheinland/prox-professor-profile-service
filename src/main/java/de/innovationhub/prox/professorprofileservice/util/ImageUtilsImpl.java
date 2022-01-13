package de.innovationhub.prox.professorprofileservice.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.springframework.stereotype.Component;

@Component
public class ImageUtilsImpl implements ImageUtils {

  /**
   * Detects the file ending of image data
   *
   * @param data image data
   * @return file ending of image data or an empty string when the type somehow can not detecte
   * @throws IOException on I/O error
   */
  public String detectFileEnding(byte[] data) throws IOException {
    ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));

    Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

    if (imageReaders.hasNext()) {
      return "." + imageReaders.next().getFormatName();
    }
    return "";
  }

  @Override
  public byte[] convertAndResizeToPng(byte[] data, int width, int height) throws IOException {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      var bufferedImage = ImageIO.read(byteArrayInputStream);
      var resizedImage = Scalr.resize(bufferedImage, Method.QUALITY, width, height);
      if (resizedImage != null && ImageIO.write(resizedImage, "png", byteArrayOutputStream)) {
        return byteArrayOutputStream.toByteArray();
      } else {
        throw new IOException("No appropriate writer found.");
      }
    }
  }
}
