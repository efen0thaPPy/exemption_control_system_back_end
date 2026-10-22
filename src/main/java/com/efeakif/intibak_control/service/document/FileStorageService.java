package com.efeakif.intibak_control.service.document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path rootDir = Paths.get("uploads");

    public FileStorageService() {
        try {

            Files.createDirectories(rootDir);
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }

    }

    public String saveFile(MultipartFile file, String prefix, String suffix) {

        try {
            Path destinationPath = rootDir.resolve(prefix + "_" + suffix + ".pdf");
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            return destinationPath.toString();
        } catch (IOException ex) {
            throw new RuntimeException("failed to save the file " + ex.getMessage());
        }

    }

    public Resource loadFile(String filePath) {

        Path path = Paths.get(filePath);
        try {

            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable())
                return resource;

            else
                throw new RuntimeException("file not found or not readable");

        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex.getMessage());

        }

    }

}
