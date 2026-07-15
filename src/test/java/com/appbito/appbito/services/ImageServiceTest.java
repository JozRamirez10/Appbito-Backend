package com.appbito.appbito.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.appbito.appbito.exceptions.FileStorageException;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Image Service Test
 */
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @TempDir
    Path tempDir;

    private ImageService imageService;

    private static final Long USER_ID = 1L;

    private static final String JPG = "jpg";

    private static final String IMAGE = "image";

    private static final String NAME_IMAGE = "original." + JPG;

    private static final String IMAGE_JPEG = "image/jpeg";

    private static final String DOCUMENT_PDF = "document.pdf";

    private static final String APP_PDF = "application/pdf";

    private static final String DATA_FAKE = "data_fake";

    private static final String TEST_FILENAME = "test_image." + JPG;

    private static final String PHANTOM_IMAGE = "phantom." + JPG;

    private static final String PATH_TRANSVERSAL = "../malicious." + JPG;

    private static final String CONFLICT_FILE = "conflict.txt";

    @BeforeEach
    void setUp() {
        imageService = new ImageService(tempDir.toString());
        imageService.init();
    }

    @Test
    void initException() throws IOException {

        Path conflictFile = tempDir.resolve(CONFLICT_FILE);
        Files.createFile(conflictFile);

        ImageService badService = new ImageService(conflictFile.toString());

        assertThrows(FileStorageException.class, badService::init);
    }

    @Test
    void storeProfileImageOk() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
            IMAGE,
            NAME_IMAGE,
            IMAGE_JPEG,
            generateValidImageBytes()
        );

        String result = imageService.storeProfileImage(file, USER_ID);

        assertNotNull(result);
        assertTrue(Files.exists(tempDir.resolve(result)));
    }

    @Test
    void storeProfileImageEmpty() {

        MockMultipartFile emptyFile = new MockMultipartFile(IMAGE, new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> {
            imageService.storeProfileImage(emptyFile, USER_ID);
        });
    }

    @Test
    void storeProfileImageNull() {

        MockMultipartFile nullContent = new MockMultipartFile(
            IMAGE, NAME_IMAGE, null, DATA_FAKE.getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            imageService.storeProfileImage(nullContent, USER_ID);
        });
    }


    @Test
    void storeProfileImageInvalidFormat() {

        MockMultipartFile invalidFile = new MockMultipartFile(
            IMAGE, DOCUMENT_PDF, APP_PDF, DATA_FAKE.getBytes());

        assertThrows(IllegalArgumentException.class, () -> {
            imageService.storeProfileImage(invalidFile, USER_ID);
        });
    }

    @Test
    void storeProfileImageException() throws Exception {

        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn(IMAGE_JPEG);

        when(mockFile.getInputStream()).thenThrow(new IOException());

        assertThrows(FileStorageException.class, () -> {
            imageService.storeProfileImage(mockFile, USER_ID);
        });
    }

    @Test
    void loadAsResourceOk() throws Exception {

        Path testFile = tempDir.resolve(TEST_FILENAME);
        Files.write(testFile, IMAGE.getBytes());

        Resource resource = imageService.loadAsResource(TEST_FILENAME);

        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void loadAsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            imageService.loadAsResource(PHANTOM_IMAGE);
        });
    }

    @Test
    void verifyPathTransversalSecurityException() {
        assertThrows(SecurityException.class, () -> {
            imageService.loadAsResource(PATH_TRANSVERSAL);
        });
    }

    @Test

    void verifyPathTransversalSecurityParentNull() {
        Iterable<Path> roots = FileSystems.getDefault().getRootDirectories();
        Path rootPath = roots.iterator().next();
        assertThrows(SecurityException.class, () -> {
            imageService.loadAsResource(rootPath.toString());
        });
    }

    private byte[] generateValidImageBytes() throws Exception {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, JPG, baos);
        return baos.toByteArray();
    }


}
