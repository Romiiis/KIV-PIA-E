package com.romiiis.repository;


import com.romiiis.configuration.ProjectsFilter;
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

}
