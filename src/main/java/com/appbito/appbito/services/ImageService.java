package com.appbito.appbito.services;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.exceptions.FileStorageException;
import com.appbito.appbito.exceptions.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Image Service.
 */
@Service
public class ImageService {

    private static final String JPG = Constants.Files.JPG_EXTENSION;

    private static final List<String> ALLOWED_IMAGES_TYPES = List.of("image/jpeg", "image/png");

    private final Path rootLocation;

    /**
     * constructor to inject properties.
     *
     * @param imageDir directory's name.
     */
    public ImageService(@Value("${app.image-dir}") String imageDir) {
        this.rootLocation = Paths.get(imageDir).toAbsolutePath().normalize();
    }

    /**
     * Initialize directory after bean construction.
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not initialize directory", e);
        }
    }

    /**
     * method store image.
     *
     * @param file image.
     * @param userId user id.
     *
     * @return image's name.
     */
    public String storeProfileImage(MultipartFile file, Long userId) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException(Constants.Files.EMPTY_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGES_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(Constants.Files.FORMAT_NOT_ALLOWED);
        }

        try {

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = Scalr.resize(originalImage, 200);

            String filename = Constants.Files.PROFILE_PREFIX + userId + "." + JPG;
            Path destinationFile = resolveAndNormalizePath(filename);

            verifyPathTransversalSecurity(destinationFile, Constants.Files.CANNOT_STORE);

            ImageIO.write(resizedImage, JPG, destinationFile.toFile());

            return filename;

        } catch (Exception e) {
            throw new FileStorageException(Constants.Files.FAILED_TO_STORE, e);
        }
    }

    /**
     * method get image like resource.
     *
     * @param filename image's name.
     *
     * @return image like resource.
     */
    public Resource loadAsResource(String filename) {
        try {
            Path file = resolveAndNormalizePath(filename);

            verifyPathTransversalSecurity(file, Constants.Files.CANNOT_READ);

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throwNotFoundException(filename);
            }

        } catch (Exception e) {
            throwNotFoundException(filename);
        }

        return null;
    }

    private Path resolveAndNormalizePath(String filename) {
        return this.rootLocation.resolve(filename).normalize();
    }

    private void verifyPathTransversalSecurity(Path file, String message) {
        Path parent = file.getParent();
        if (parent == null || !parent.equals(this.rootLocation)) {
            throw new SecurityException(message);
        }
    }

    private void throwNotFoundException(String filename) {
        throw new ResourceNotFoundException(filename);
    }

}
