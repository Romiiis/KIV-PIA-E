package com.romiiis.service.interfaces;


import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Project;
import com.romiiis.domain.WrapperProjectFeedback;
import com.romiiis.exception.*;
import com.romiiis.filter.ProjectsFilter;

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
     * @param filter   filter to apply
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
    Project createProject(Locale targetLanguage, ResourceHeader sourceFile) throws ProjectNotFoundException, UserNotFoundException, FileStorageException, NoAccessToOperateException;


    /**
     * Retrieves the original file data for a given project.
     *
     * @param projectId The ID of the project.
     * @return The byte array of the original file data.
     */
    ResourceHeader getOriginalFile(UUID projectId) throws ProjectNotFoundException, FileStorageException;


    /**
     * Retrieves the translated file data for a given project.
     *
     * @param projectId The ID of the project.
     * @return The byte array of the translated file data.
     */
    ResourceHeader getTranslatedFile(UUID projectId) throws ProjectNotFoundException, FileStorageException, FileNotFoundException;

    /**
     * Updates an existing project.
     *
     * @param project  the project to be updated
     */
    void updateProject( Project project) throws ProjectNotFoundException;


    /**
     * Retrieves all project IDs as strings.
     *
     * @return A list of all project IDs in string format.
     */
    List<String> getAllProjectIdsAsString();


    /**
     * Retrieves all projects along with their feedback based on the provided filter.
     *
     * @param filter the filter criteria for retrieving projects
     * @return a list of WrapperProjectFeedback containing projects and their feedback
     */
    List<WrapperProjectFeedback> getAllProjectsWithFeedback(ProjectsFilter filter);

    /**
     * Retrieves a project along with its feedback by project ID.
     *
     * @param projectId the project ID
     * @return project with feedback wrapped in WrapperProjectFeedback
     */
    WrapperProjectFeedback getProjectFeedback(UUID projectId) throws ProjectNotFoundException;
}
