package com.romiiis.service.interfaces;


import com.romiiis.configuration.ProjectsFilter;
import com.romiiis.domain.Project;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.exception.UserNotFoundException;

import java.io.IOException;
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
     * Creates a new project for the current user
     * @param targetLanguage target language for translation
     * @param sourceFile source file to translate
     * @return newly created project
     */
    Project createProject(UUID customerId, Locale targetLanguage, byte[] sourceFile) throws ProjectNotFoundException, UserNotFoundException;
}
