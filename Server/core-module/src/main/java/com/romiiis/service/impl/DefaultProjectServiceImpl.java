package com.romiiis.service.impl;


import com.romiiis.domain.Project;
import com.romiiis.domain.UserRole;
import com.romiiis.exception.FileNotFoundException;
import com.romiiis.exception.FileStorageException;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.filter.ProjectsFilter;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.service.interfaces.IFileSystemService;
import com.romiiis.service.interfaces.IProjectService;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Default implementation of the IProjectService interface.
 *
 * @author Roman Pejs
 */

@Slf4j
@RequiredArgsConstructor
public class DefaultProjectServiceImpl implements IProjectService {

    /**
     * Repositories & Services
     */
    private final IUserService userService;
    private final IProjectRepository projectRepository;
    private final IFileSystemService fsService;

    /**
     * Creates a new project for the current user
     *
     * @param targetLanguage target language for translation
     * @param sourceFile     source file to translate
     * @return newly created project
     */
    @Override
    public Project createProject(UUID customerId, Locale targetLanguage, Resource sourceFile) throws ProjectNotFoundException, UserNotFoundException, FileStorageException, IllegalArgumentException {

        var customer = userService.getUserById(customerId);

        if (customer == null) {
            log.error("Customer with ID {} not found", customerId);
            throw new UserNotFoundException("Customer not found");
        }

        if (!customer.getRole().equals(UserRole.CUSTOMER)) {
            log.error("User with ID {} is not a customer", customerId);
            throw new IllegalArgumentException("User is not a customer");
        }

        var newProject = new Project(customer, targetLanguage, sourceFile.getFilename());

        try {
            // Store the source file in the filesystem
            fsService.saveOriginalFile(newProject.getId(), sourceFile.getContentAsByteArray());
        } catch (IOException ex) {
            log.error("Failed to read source file content for project ID {}", newProject.getId(), ex);
            throw new FileStorageException("Failed to read source file content for project ID " + newProject.getId());
        }

        // Store the new project in the repository
        projectRepository.save(newProject);

        log.info("Created new project with ID {} for customer {}", newProject.getId(), customer.getClass());

        // Try to find the project and return it
        var storedProject = projectRepository.findById(newProject.getId());

        if (storedProject == null) {
            log.error("Failed to retrieve newly created project with ID {}", newProject.getId());
            throw new ProjectNotFoundException("Failed to retrieve newly created project");

        } else {
            return storedProject;
        }
    }

    /**
     * Retrieves all projects based on the provided filter.
     *
     * @param filter the filter criteria for retrieving projects
     * @return a list of projects matching the filter criteria
     */
    @Override
    public List<Project> getAllProjects(ProjectsFilter filter) {
        return projectRepository.getAll(filter);
    }

    /**
     * Retrieves a project by its unique identifier.
     *
     * @param projectId the unique identifier of the project
     * @return the project with the given ID
     * @throws ProjectNotFoundException if the project is not found
     */
    @Override
    public Project getProjectById(UUID projectId) throws ProjectNotFoundException {
        var project = projectRepository.findById(projectId);

        if (project == null) {
            log.error("Project with ID {} not found", projectId);
            throw new ProjectNotFoundException("Project not found");
        }

        return project;
    }


    /**
     * Retrieves the original file data for a given project.
     *
     * @param projectId The ID of the project.
     * @return The byte array of the original file data.
     */
    @Override
    public Resource getOriginalFile(UUID projectId) throws ProjectNotFoundException, FileStorageException{
        return fsService.getOriginalFile(projectId);
    }

    @Override
    public Resource getTranslatedFile(UUID projectId) throws ProjectNotFoundException, FileStorageException, FileNotFoundException {

        Project project = getProjectById(projectId);

        if (project.getTranslatedFileName().isEmpty()) {
            log.error("Translated file for project ID {} not found", projectId);
            throw new FileNotFoundException("Translated file not found for project ID " + projectId);
        }

        try {
            return fsService.getTranslatedFile(projectId);
        } catch (FileNotFoundException ex) {
            log.error("Translated file for project ID {} not found in filesystem (probably not uploaded yet?!)", projectId, ex);
            throw new FileNotFoundException("Translated file not found for project ID (probably not uploaded yet?!) " + projectId);
        }
    }
}
