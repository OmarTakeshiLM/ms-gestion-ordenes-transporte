package com.movilidad.ordenes.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path root;

    public LocalFileStorageService(@Value("${app.storage.location}") String location) {
        this.root = Path.of(location).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String folder) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + (extension != null ? "." + extension.toLowerCase() : "");
        Path target = root.resolve(folder).resolve(fileName).normalize();

        try (InputStream in = file.getInputStream()) {
            Files.createDirectories(target.getParent());
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo " + file.getOriginalFilename(), e);
        }

        log.debug("Archivo guardado en {}", target);
        return root.relativize(target).toString().replace('\\', '/');
    }

    @Override
    public void delete(String path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(root.resolve(path).normalize());
        } catch (IOException e) {
            log.warn("No se pudo borrar el archivo {}", path, e);
        }
    }
}