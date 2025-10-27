package com.romiiis.repository;


import com.romiiis.filter.ProjectsFilter;
import com.romiiis.domain.Project;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Project entities.
 *
 * @author Roman Pejs
 */
public interface IProjectRepository {
    /**
     * Stores a project in the repository
     * @param project project to store
     */
    void save(Project project);

    /**
     * Fetches all projects, no matter their state
     *
     * @return all projects
     */
    List<Project> getAll(ProjectsFilter filter);

    /**
     * Finds a project by its ID
     * @param id project ID
     * @return project with the given ID, or null if not found
     */
    Project findById(UUID id);


    /**
     * Retrieves all project IDs as strings.
     *
     * @return a list of all project IDs in string format
     */
    List<String> getAllProjectIdsAsString();

    /**
     * Counts the number of projects associated with a specific translator.
     *
     * @param translatorId the UUID of the translator
     * @return the count of projects associated with the translator
     */
    int countProjectsWithTranslator(UUID translatorId);


    /**
     * Deletes all projects from the repository.
     */
    void deleteAll();
}
