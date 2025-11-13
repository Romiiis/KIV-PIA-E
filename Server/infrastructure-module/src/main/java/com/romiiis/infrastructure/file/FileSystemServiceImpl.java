package com.romiiis.infrastructure.file;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.exception.FileNotFoundException;
import com.romiiis.exception.FileStorageException;
import com.romiiis.port.IFileSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of the file system service.
 *
 * <br>
 * <p>
 * Service saves original and translated files associated with projects,
 * and retrieves them when needed.
 * <br>
 * In root directory, each project has its own subdirectory named by its UUID.
 * Inside each project directory, there are two files:
 *     <ul>
 *         <li>"original" - contains the original file data.</li>
 *         <li>"translated" - contains the translated file data.</li>
 *     </ul>
 * <p>
 *     Original names are saved in the database associated with the project entity.
 *     After getting the files, the service returns them as Resource objects for further processing.
 *     Responsibility of handling and naming file have the API layer.
 * </p>
 *
 */
@Slf4j
public class FileSystemServiceImpl implements IFileSystemService {

    private final String rootPath;
    /**
     * Root directory for file storage
     */
    private final String projectsDirectory = "/files";

    /**
     * File name prefixes
     */
    private final String originalFileName = "original";
    private final String translatedFileName = "translated";

    public FileSystemServiceImpl(String fileSystemRoot) {
        this.rootPath = fileSystemRoot + projectsDirectory;
    }
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
    public ResourceHeader getOriginalFile(UUID projectId) throws FileNotFoundException, FileStorageException {

        Resource res = getFile(projectId.toString(), originalFileName);
        try {
            if (!res.exists()) {
                log.warn("Original file not found for project {}: {}", projectId, originalFileName);
                throw new FileNotFoundException("Original file not found for project " + projectId);
            }
            return new ResourceHeader(res.getFilename(), res.getContentAsByteArray());
        } catch (Exception e) {
            log.error("Error accessing original file for project {}: {}", projectId, e.getMessage());
            throw new FileStorageException("Error accessing original file for project " + projectId);
        }
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
    public ResourceHeader getTranslatedFile(UUID projectId) throws FileNotFoundException, FileStorageException {
        Resource res = getFile(projectId.toString(), translatedFileName);
        try {
            if (!res.exists()) {
                log.warn("Translated file not found for project {}", projectId);
                throw new FileNotFoundException("Translated file not found for project " + projectId);
            }
            return new ResourceHeader(res.getFilename(), res.getContentAsByteArray());
        } catch (Exception e) {
            log.error("Error accessing translated file for project {}: {}", projectId, e.getMessage());
            throw new FileStorageException("Error accessing translated file for project " + projectId);
        }
    }


    /**
     * Constructs the project directory path.
     *
     * @param projectId the project ID, can be null for root directory
     * @return the Path object representing the project directory
     */
    private Path getProjectDir(String projectId) throws FileStorageException {
        try {

            Path basePath = Paths.get(System.getProperty("user.dir"), rootPath);

            // if projectId is null, return root directory
            if (projectId == null) {
                return basePath;
            }
            basePath = basePath.resolve(projectId);

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

    /**
     * Lists all project folders in the root directory.
     *
     * @return a list of project folder names
     * @throws FileStorageException if an error occurs while accessing the file system
     */
    @Override
    public List<String> listAllProjectFolders() throws FileStorageException {
        try {
            Path rootPath = getProjectDir(null);
            try (var paths = Files.list(rootPath)) {
                return paths
                        .filter(Files::isDirectory)
                        .map(path -> path.getFileName().toString())
                        .toList();
            }

        } catch (IOException e) {
            log.error("Error listing project folders: {}", e.getMessage());
            throw new FileStorageException("Error listing project folders");
        }
    }

    /**
     * Deletes the project folder for the specified project ID.
     *
     * @param projectIdString the project ID as a string
     * @throws FileStorageException if an error occurs while deleting the folder
     */
    @Override
    public void deleteProjectFolder(String projectIdString) throws FileStorageException {

        try {
            Path projectDir = getProjectDir(projectIdString);
            if (Files.exists(projectDir)) {
                try (var files = Files.list(projectDir)) {
                    files.forEach(filePath -> {
                        try {
                            Files.deleteIfExists(filePath);
                        } catch (IOException e) {
                            log.error("Error deleting file {}: {}", filePath, e.getMessage());
                        }
                    });
                }
                Files.deleteIfExists(projectDir);
            } else {
                log.warn("Project directory not found for deletion: {}", projectIdString);
            }
        } catch (IOException e) {
            log.error("Error deleting project folder for project {}: {}", projectIdString, e.getMessage());
            throw new FileStorageException("Error deleting project folder for project " + projectIdString);
        }
    }
}
