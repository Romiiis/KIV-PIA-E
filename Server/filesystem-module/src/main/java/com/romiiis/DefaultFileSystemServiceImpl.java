package com.romiiis;

import com.romiiis.exception.FileNotFoundException;
import com.romiiis.exception.FileStorageException;
import com.romiiis.service.interfaces.IFileSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Default implementation of the file system service.
 *
 * @author Roman Pejs
 */
@Slf4j
public class DefaultFileSystemServiceImpl implements IFileSystemService {

    /**
     * Root directory for file storage
     */
    private final String rootDirectory = "/files";

    /**
     * File name prefixes
     */
    private final String originalFileName = "original";
    private final String translatedFileName = "translated";

    /**
     * Saves the original file for a given project.
     *
     * @param projectId The ID of the project.
     * @param fileData  The byte array of the file data.
     * @throws FileStorageException File storage exception during operation (custom)
     */
    @Override
    public void saveOriginalFile(UUID projectId, byte[] fileData) throws FileStorageException {
        saveFile(projectId.toString(), originalFileName, fileData);

    }

    /**
     * Saves the translated file for a given project.
     *
     * @param projectId The ID of the project.
     * @param fileData  The byte array of the translated file data.
     * @throws FileStorageException File storage exception during operation (custom)
     */
    @Override
    public void saveTranslatedFile(UUID projectId, byte[] fileData) throws FileStorageException {
        saveFile(projectId.toString(), translatedFileName, fileData);

    }

    /**
     * Retrieves the original file for a given project.
     *
     * @param projectId The ID of the project.
     * @return The Resource representing the original file.
     * @throws FileNotFoundException File not found exception (custom)
     * @throws FileStorageException  File storage exception during operation (custom)
     */
    @Override
    public Resource getOriginalFile(UUID projectId) throws FileNotFoundException, FileStorageException {
        return getFile(projectId.toString(), originalFileName);
    }

    /**
     * Retrieves the translated file for a given project.
     *
     * @param projectId The ID of the project.
     * @return The Resource representing the translated file.
     * @throws FileNotFoundException File not found exception (custom)
     * @throws FileStorageException  File storage exception during operation (custom)
     */
    @Override
    public Resource getTranslatedFile(UUID projectId) throws FileNotFoundException, FileStorageException {
        return getFile(projectId.toString(), translatedFileName);
    }


    /**
     * Constructs the project directory path.
     *
     * @param projectId the project ID
     * @return the Path object representing the project directory
     */
    private Path getProjectDir(String projectId) throws FileStorageException {
        try {
            Path basePath = Paths.get(System.getProperty("user.dir"), rootDirectory, projectId);
            Files.createDirectories(basePath);
            return basePath;
        } catch (IOException e) {
            log.error("Error creating project directory for project {}: {}", projectId, e.getMessage());
            throw new FileStorageException("Error creating project directory for project " + projectId);
        }

    }

    /**
     * Saves a file to the specified project directory.
     *
     * @param projectId the project ID
     * @param fileName  the name of the file to save
     * @param fileData  the file data as a byte array
     */
    private void saveFile(String projectId, String fileName, byte[] fileData) throws FileStorageException {
        // Find the project directory
        Path projectDir = getProjectDir(projectId);

        try {
            // Save the file
            Files.createDirectories(projectDir);

            // Write the file data to the specified file
            Path filePath = projectDir.resolve(fileName);

            // Write the file data
            Files.write(filePath, fileData);
        } catch (IOException e) {
            log.error("Error saving file {} for project {}: {}", fileName, projectId, e.getMessage());
            throw new FileStorageException("Error saving file " + fileName + " for project " + projectId);
        }


    }


    /**
     * Retrieves a file from the specified project directory based on the given prefix.
     *
     * @param projectId     the project ID
     * @param savedFileName the name of the file to retrieve
     * @return the Resource representing the file, or null if not found
     */
    private Resource getFile(String projectId, String savedFileName) throws FileNotFoundException, FileStorageException {

        Path projectDir = getProjectDir(projectId);
        if (!Files.exists(projectDir)) {
            log.warn("Project directory not found for project {}: {}", projectId, projectDir);
            throw new FileNotFoundException("Project directory not found for project " + projectId);
        }

        Path filePath = projectDir.resolve(savedFileName);
        if (!Files.exists(filePath)) {
            log.warn("File not found for project {}: {}", projectId, savedFileName);
            throw new FileNotFoundException("File not found for project " + projectId);

        } else {
            return new FileSystemResource(filePath.toFile());
        }


    }
}
