package com.romiiis.service.impl;


import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.*;
import com.romiiis.event.NoTranslatorAssignedToProjectEvent;
import com.romiiis.event.TranslatorAssignedToProjectEvent;
import com.romiiis.exception.*;
import com.romiiis.filter.ProjectsFilter;
import com.romiiis.port.IDomainEventPublisher;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.port.IFileSystemService;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.api.IProjectService;
import com.romiiis.service.api.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Default implementation of the IProjectService interface.
 *
 * @author Roman Pejs
 */

@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    /**
     * Repositories & Services
     */

    private final IProjectRepository projectRepository;
    private final IFeedbackRepository feedbackRepository;
    private final IUserRepository userRepository;
    private final IFileSystemService fsService;
    private final IExecutionContextProvider callerContextProvider;
    private final IDomainEventPublisher eventPublisher;


    /**
     * Creates a new project for the current user
     *
     * @param targetLanguage target language for translation
     * @param sourceFile     source file to translate
     * @return newly created project
     */
    @Override
    @Transactional(readOnly = false)
    public Project createProject(Locale targetLanguage, ResourceHeader sourceFile) throws ProjectNotFoundException, UserNotFoundException, FileStorageException, NoAccessToOperateException {

        User customer = this.fetchUserFromContext();

        if (!customer.getRole().equals(UserRole.CUSTOMER)) {
            log.error("User with ID {} is not a customer", customer.getId());
            throw new NoAccessToOperateException("User is not a customer");
        }

        var newProject = new Project(customer, targetLanguage, sourceFile.resourceName());

        // Store the source file in the filesystem
        fsService.saveOriginalFile(newProject.getId(), sourceFile.resourceData());

        // Store the new project in the repository
        projectRepository.save(newProject);

        log.info("Created new project with ID {} for customer {}", newProject.getId(), customer.getClass());

        // Now try to assign the best translator
        try {
            User bestTranslator = getBestTranslatorForProject(newProject);
            newProject.assignTranslator(bestTranslator);
            projectRepository.save(newProject);
            log.info("Assigned translator with ID {} to project ID {}", bestTranslator.getId(), newProject.getId());

            eventPublisher.publish(new TranslatorAssignedToProjectEvent(newProject));


        } catch (UserNotFoundException ex) {
            log.warn("No suitable translator found for project ID {}", newProject.getId());
            eventPublisher.publish(new NoTranslatorAssignedToProjectEvent(newProject));
        }


        return newProject;

    }

    /**
     * Retrieves all projects based on the provided filter.
     *
     * @param filter the filter criteria for retrieving projects
     * @return a list of projects matching the filter criteria
     */
    @Override
    @Transactional(readOnly = true)
    public List<Project> getAllProjects(ProjectsFilter filter) {
        User caller = fetchUserFromContext();
        if (caller.getRole() == UserRole.CUSTOMER) {
            filter.setCustomerId(caller.getId());
        } else if (caller.getRole() == UserRole.TRANSLATOR) {
            filter.setTranslatorId(caller.getId());
        }
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
    @Transactional(readOnly = true)
    public Project getProjectById(UUID projectId) throws ProjectNotFoundException, NoAccessToOperateException {

        // If caller is not system, check access rights


            User caller = fetchUserFromContext();
            Project project = fetchProject(projectId);
            boolean isProjectOwner = project.getCustomer().getId().equals(caller.getId());
            boolean isAssignedTranslator = project.getTranslator() != null &&
                    project.getTranslator().getId().equals(caller.getId());
            if (!isProjectOwner && !isAssignedTranslator && caller.getRole() != UserRole.ADMINISTRATOR) {
                log.error("User with ID {} is not authorized to access project ID: {}", caller.getId(), projectId);
                throw new NoAccessToOperateException("User is not authorized to access this project");
            }



        return this.fetchProject(projectId);
    }


    /**
     * Retrieves the original file data for a given project.
     *
     * @param projectId The ID of the project.
     * @return The byte array of the original file data.
     */
    @Override
    @Transactional(readOnly = true)
    public ResourceHeader getOriginalFile(UUID projectId) throws ProjectNotFoundException, FileStorageException, NoAccessToOperateException {
        User caller = fetchUserFromContext();
        Project project = fetchProject(projectId);

        // Original file can be accessed by customer who owns the project or the assigned translator or admin
            boolean isProjectOwner = project.getCustomer().getId().equals(caller.getId());
            boolean isAssignedTranslator = project.getTranslator() != null &&
                    project.getTranslator().getId().equals(caller.getId());
            if (!isProjectOwner && !isAssignedTranslator && caller.getRole() != UserRole.ADMINISTRATOR) {
                log.error("User with ID {} is not authorized to access original file for project ID: {}", caller.getId(), projectId);
                throw new NoAccessToOperateException("User is not authorized to access original file for this project");
            }


        if (project.getOriginalFileName() == null || project.getOriginalFileName().isEmpty()) {
            log.error("Original file for project ID {} not found", projectId);
            throw new FileStorageException("Original file not found for project ID " + projectId);
        }

        return fsService.getOriginalFile(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceHeader getTranslatedFile(UUID projectId) throws ProjectNotFoundException, FileStorageException, FileNotFoundException, NoAccessToOperateException {

        Project project = fetchProject(projectId);
        User caller = fetchUserFromContext();

        // Translated file can be accessed by customer who owns the project or the assigned translator or admin
            boolean isProjectOwner = project.getCustomer().getId().equals(caller.getId());
            boolean isAssignedTranslator = project.getTranslator() != null &&
                    project.getTranslator().getId().equals(caller.getId());
            if (!isProjectOwner && !isAssignedTranslator && caller.getRole() != UserRole.ADMINISTRATOR) {
                log.error("User with ID {} is not authorized to access translated file for project ID: {}", caller.getId(), projectId);
                throw new NoAccessToOperateException("User is not authorized to access translated file for this project");
            }


        if (project.getTranslatedFileName() == null || project.getTranslatedFileName().isEmpty()) {
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

    /**
     * Updates an existing project.
     *
     * @param project the project to be updated
     * @throws ProjectNotFoundException if the project is not found
     */
    @Override
    @Transactional(readOnly = false)
    public void updateProject(Project project) throws ProjectNotFoundException {
        //
        projectRepository.save(project);
    }

    /**
     * Retrieves all project IDs as strings.
     *
     * @return a list of all project IDs in string format
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllProjectIdsAsString() {
        return projectRepository.getAllProjectIdsAsString();
    }


    /**
     * Finds the best translator for a given project based on their workload and language proficiency.
     *
     * @param project the project to find a translator for
     * @return the best suited translator
     * @throws UserNotFoundException if no suitable translator is found
     */
    private User getBestTranslatorForProject(Project project) throws UserNotFoundException {
        // First get the target language of the project
        Locale targetLanguage = project.getTargetLanguage();

        // Then get all translators who know this language

        List<UUID> translators = userRepository.getTranslatorsIdsByLanguage(targetLanguage);

        // Find the translator with the least number of assigned projects
        UUID bestTranslatorId = null;
        int minProjects = Integer.MAX_VALUE;

        for (UUID translatorId : translators) {
            int projectCount = projectRepository.countProjectsWithTranslator(translatorId);
            if (projectCount < minProjects) {
                minProjects = projectCount;
                bestTranslatorId = translatorId;
            }
        }

        if (bestTranslatorId != null) {
            try {
                UUID finalBestTranslatorId = bestTranslatorId;
                // Fetch the user by Id from repositor
                Optional<User> userOpt = userRepository.getUserById(finalBestTranslatorId);
                if (userOpt.isPresent()) {
                    return userOpt.get();
                }
                throw new UserNotFoundException("Best translator not found");

            } catch (UserNotFoundException e) {
                log.error("Best translator with ID {} not found", bestTranslatorId);
                throw new UserNotFoundException("Best translator not found");
            }
        }

        log.error("No suitable translator found for project ID {}", project.getId());
        throw new UserNotFoundException("No suitable translator found");
    }


    private User fetchUserFromContext() throws UserNotFoundException {
        User caller = callerContextProvider.getCaller();

        if (caller == null) {
            log.error("Caller not found in context");
            throw new UserNotFoundException("Caller not found");
        }
        return caller;
    }

    private Project fetchProject(UUID projectId) throws ProjectNotFoundException {
        Project project = projectRepository.findById(projectId);

        if (project == null) {
            log.error("Project with ID {} not found", projectId);
            throw new ProjectNotFoundException("Project not found");
        }
        return project;
    }


    @Override
    @Transactional(readOnly = true)
    public List<WrapperProjectFeedback> getAllProjectsWithFeedback(ProjectsFilter filter) {
        User caller = fetchUserFromContext();
            if (caller.getRole() == UserRole.CUSTOMER) {
                filter.setCustomerId(caller.getId());
            } else if (caller.getRole() == UserRole.TRANSLATOR) {
                filter.setTranslatorId(caller.getId());
            }



        // Get all projects based on the filter
        List<Project> projects = projectRepository.getAll(filter);

        // For each project, get its feedback wrapper
        List<Feedback> feedbacks = feedbackRepository.getAllFeedbackForProjectIds(
                projects.stream().map(Project::getId).toList());


        List<WrapperProjectFeedback> wrapperProjectFeedbacks = new ArrayList<>();
        for (Project project : projects) {
            Feedback feedbackForProject = feedbacks.stream()
                    .filter(fb -> fb.getProjectId().equals(project.getId()))
                    .findFirst()
                    .orElse(null);

            WrapperProjectFeedback wrapper = new WrapperProjectFeedback(project, feedbackForProject);
            wrapperProjectFeedbacks.add(wrapper);
        }

        // Check filter for projects with feedback only
        if (filter.isHasFeedback()) {
            wrapperProjectFeedbacks.removeIf(wpf -> wpf.getFeedback() == null);
        }

        return wrapperProjectFeedbacks;


    }

    @Override
    @Transactional(readOnly = true)
    public WrapperProjectFeedback getProjectFeedback(UUID projectId) throws ProjectNotFoundException {
        Project project = fetchProject(projectId);
        Feedback feedback = feedbackRepository.getFeedbackByProjectId(projectId);
        return new WrapperProjectFeedback(project, feedback);


    }
}
