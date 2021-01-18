package de.innovationhub.prox.professorprofileservice.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {ImageUtilsImpl.class})
class ImageUtilsImplTest {

  @Autowired ImageUtils imageUtils;

  @Autowired ResourceLoader resourceLoader;

  @Test
  void when_jpg_image_should_return_jpg() throws IOException {
    var img = resourceLoader.getResource("classpath:/img/test_jpg_img.jpg");
    var fileEnding = imageUtils.detectFileEnding(img.getInputStream().readAllBytes());
    assertEquals(".JPEG", fileEnding);
  }

  @Test
  void when_png_image_should_return_png() throws IOException {
    var img = resourceLoader.getResource("classpath:/img/test_png_img.png");
    var fileEnding = imageUtils.detectFileEnding(img.getInputStream().readAllBytes());
    assertEquals(".png", fileEnding);
  }

  @Test
  void convertToPng() throws IOException {
    var img = resourceLoader.getResource("classpath:/img/test_jpg_img.jpg");
    var pngValue = imageUtils.convertToPng(img.getInputStream().readAllBytes());

    ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(pngValue));
    Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

    assertTrue(imageReaders.hasNext());
    assertEquals("png", imageReaders.next().getFormatName());
  }
}
