package com.romiiis.service.interfaces;


import com.romiiis.domain.Project;
import com.romiiis.exception.FileNotFoundException;
import com.romiiis.exception.FileStorageException;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.filter.ProjectsFilter;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Service interface for managing projects.
 *
 * @author Roman Pejs
 */
public interface IProjectService {

    /**
     * Fetches all projects, possibly filtered by the given filter
     *
     * @param filter filter to apply
     * @return all projects matching the filter
     */
    List<Project> getAllProjects(ProjectsFilter filter);


    /**
     * Finds a project by its ID
     *
     * @param projectId project ID
     * @return project with the given ID
     */
    Project getProjectById(UUID projectId) throws ProjectNotFoundException;

    /**
     * Creates a new project for the current user
     *
     * @param targetLanguage target language for translation
     * @param sourceFile     source file to translate
     * @return newly created project
     */
    Project createProject(UUID customerId, Locale targetLanguage, Resource sourceFile) throws ProjectNotFoundException, UserNotFoundException, FileStorageException, IllegalArgumentException;


    /**
     * Retrieves the original file data for a given project.
     *
     * @param projectId The ID of the project.
     * @return The byte array of the original file data.
     */
    Resource getOriginalFile(UUID projectId) throws ProjectNotFoundException, FileStorageException;



    Resource getTranslatedFile(UUID projectId) throws ProjectNotFoundException, FileStorageException, FileNotFoundException;


}
