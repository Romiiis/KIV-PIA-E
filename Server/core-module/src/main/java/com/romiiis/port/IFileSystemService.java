package com.romiiis.port;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.exception.FileNotFoundException;
import com.romiiis.exception.FileStorageException;

import java.util.List;
import java.util.UUID;

/**
 * Interface for file system services.
 * <p>
 * Handles operations for managing files and directories.
 * </p>
 * @author Roman Pejs
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
    ResourceHeader getTranslatedFile(UUID projectId) throws FileNotFoundException, FileStorageException;


    /**
     * Retrieves the original file data for a given project.
     *
     * @param projectId The ID of the project.
     * @return The byte array of the original file data.
     * @throws FileNotFoundException if the original file is not found
     * @throws FileStorageException  File storage exception during operation (custom)
     */
    ResourceHeader getOriginalFile(UUID projectId) throws FileNotFoundException, FileStorageException;

    /**
     * Lists all project folder names in the file system.
     *
     * @return A list of project folder names.
     * @throws FileStorageException File storage exception during operation (custom)
     */
    List<String> listAllProjectFolders() throws FileStorageException;


    /**
     * Deletes the project folder for a given project ID.
     *
     * @param projectIdString The ID of the project.
     * @throws FileStorageException File storage exception during operation (custom)
     */
    void deleteProjectFolder(String projectIdString) throws FileStorageException;


}
