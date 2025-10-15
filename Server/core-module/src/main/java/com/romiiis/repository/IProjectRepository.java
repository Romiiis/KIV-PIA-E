package com.romiiis.repository;


import com.romiiis.domain.Project;

import java.util.List;
import java.util.UUID;

public interface IProjectRepository {
    /**
     * Stores a project in the repository
     * @param project project to store
     */
    void store(Project project);

    /**
     * Fetches all projects, no matter their state
     *
     * @return all projects
     */
    List<Project> getAll();

    /**
     * Finds a project by its ID
     * @param id project ID
     * @return project with the given ID, or null if not found
     */
    Project findById(UUID id);

}
