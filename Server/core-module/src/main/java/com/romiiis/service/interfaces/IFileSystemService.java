package com.romiiis.service.interfaces;

import com.romiiis.exception.FileNotFoundException;
import com.romiiis.exception.FileStorageException;
import org.springframework.core.io.Resource;

import java.util.UUID;

/**
 * Interface for file system services.
 * <p>
 * Handles operations for managing files and directories.
 * </p>
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
public interface IFileSystemService {

    /**
     * Saves the original file data for a given project.
     *
     * @param projectId The ID of the project.
     * @param fileData  The byte array of the file data.
     * @throws FileStorageException File storage exception during operation (custom)
     */
    void saveOriginalFile(UUID projectId, byte[] fileData) throws FileStorageException;


    /**
     * Saves the translated file data for a given project.
     *
     * @param projectId The ID of the project.
     * @param fileData  The byte array of the translated file data.
     * @throws FileStorageException File storage exception during operation (custom)
     */
    void saveTranslatedFile(UUID projectId, byte[] fileData) throws FileStorageException;


    /**
     * Retrieves the translated file data for a given project.
     *
     * @param projectId The ID of the project.
     * @return The byte array of the translated file data.
     * @throws FileNotFoundException if the translated file is not found
     * @throws FileStorageException  File storage exception during operation (custom)
     */
    Resource getTranslatedFile(UUID projectId) throws FileNotFoundException, FileStorageException;


    /**
     * Retrieves the original file data for a given project.
     *
     * @param projectId The ID of the project.
     * @return The byte array of the original file data.
     * @throws FileNotFoundException if the original file is not found
     * @throws FileStorageException  File storage exception during operation (custom)
     */
    Resource getOriginalFile(UUID projectId) throws FileNotFoundException, FileStorageException;


}
