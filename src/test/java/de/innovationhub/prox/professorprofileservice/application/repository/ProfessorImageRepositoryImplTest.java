package de.innovationhub.prox.professorprofileservice.application.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.common.jimfs.Jimfs;
import de.innovationhub.prox.professorprofileservice.util.ImageUtils;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ContextConfiguration(classes = {ProfessorImageRepositoryImpl.class})
class ProfessorImageRepositoryImplTest {

  private final FileSystem fileSystem = Jimfs.newFileSystem();

  @Autowired private ProfessorImageRepositoryImpl professorImageRepository;

  @MockBean private ImageUtils imageUtils;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this.getClass());
    ReflectionTestUtils.setField(
        professorImageRepository, "imageDirectory", fileSystem.getPath(""));
  }

  @Test
  void when_file_does_not_exist_should_return_empty() {
    var result =
        professorImageRepository.getProfessorImage(
            fileSystem.getPath(UUID.randomUUID().toString() + ".tst"));
    assertTrue(result.isEmpty());
  }

  @Test
  void when_file_does_exist_should_return_data() throws IOException {
    var path = fileSystem.getPath("", UUID.randomUUID().toString() + ".tst");
    var bytes = new byte[] {0x00, 0x01, 0x02, 0x03};
    Files.write(path, bytes);

    var result = professorImageRepository.getProfessorImage(path);
    assertTrue(result.isPresent());
    assertArrayEquals(bytes, result.get());
  }

  @Test
  void when_image_save_should_save_image() throws IOException {
    when(imageUtils.convertToPng(any(byte[].class))).then(returnsFirstArg());
    var bytes = new byte[] {0x00, 0x01, 0x02, 0x03};

    var filename = this.professorImageRepository.saveProfessorImage(bytes);
    var path = fileSystem.getPath(filename);

    assertTrue(Files.exists(path));
    assertArrayEquals(bytes, Files.readAllBytes(path));
  }

  @Test
  void when_image_exists_should_return_true() throws IOException {
    var path = fileSystem.getPath("", UUID.randomUUID().toString() + ".tst");
    var bytes = new byte[] {0x00, 0x01, 0x02, 0x03};
    Files.write(path, bytes);

    assertTrue(professorImageRepository.imageExists(path));
  }

  @Test
  void when_image_not_exists_should_return_false() {
    var path = fileSystem.getPath("", UUID.randomUUID().toString() + ".tst");
    assertFalse(professorImageRepository.imageExists(path));
  }

  @Test
  void should_return_default_image() {
    var result = this.professorImageRepository.getDefaultImage();

    assertTrue(result.isPresent());
    assertTrue(result.get().length > 0);
  }

  @Test
  void should_delete_image() throws IOException {
    var path = fileSystem.getPath("", UUID.randomUUID().toString() + ".tst");
    var bytes = new byte[] {0x00, 0x01, 0x02, 0x03};
    Files.write(path, bytes);

    var result = this.professorImageRepository.deleteProfessorImage(path);
    assertTrue(result);
    assertFalse(Files.exists(path));
  }
}
