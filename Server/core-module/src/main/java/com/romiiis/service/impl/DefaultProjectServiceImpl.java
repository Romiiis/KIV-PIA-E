package com.romiiis.service.impl;


import com.romiiis.configuration.ProjectsFilter;
import com.romiiis.domain.Project;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.service.interfaces.IProjectService;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Default implementation of the IProjectService interface.
 *
 * @author Roman Pejs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultProjectServiceImpl implements IProjectService {

    /** Repositories & Services */
    private final IUserService userService;
    private final IProjectRepository projectRepository;

    /**
     * Creates a new project for the current user
     * @param targetLanguage target language for translation
     * @param sourceFile source file to translate
     * @return newly created project
     */
    @Override
    public Project createProject(UUID customerId, Locale targetLanguage, byte[] sourceFile) throws ProjectNotFoundException, UserNotFoundException {

        var customer = userService.getUserById(customerId);

        if (customer == null) {
            log.error("Customer with ID {} not found", customerId);
            throw new UserNotFoundException("Customer not found");
        }

        var newProject = new Project(customer, targetLanguage, sourceFile);

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

    @Override
    public List<Project> getAllProjects(ProjectsFilter filter) {
        return projectRepository.getAll(filter);
    }
}
