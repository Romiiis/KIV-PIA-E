package com.romiiis.service.impl;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Feedback;
import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.event.ProjectApprovedEvent;
import com.romiiis.event.ProjectClosedEvent;
import com.romiiis.event.ProjectCompletedEvent;
import com.romiiis.event.ProjectRejectedEvent;
import com.romiiis.exception.NoAccessToOperateException;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.port.IDomainEventPublisher;
import com.romiiis.port.IFileSystemService;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.service.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ProjectWFServiceImpl implements IProjectWFService {

    private final IFileSystemService fileSystemService;
    private final IProjectService projectService;
    private final IFeedbackService feedbackService;
    private final IExecutionContextProvider callerContextProvider;
    private final IDomainEventPublisher eventPublisher;


    /**
     * Uploads the translated file for the given project.
     *
     * @param projectId the ID of the project
     * @param resHeader header containing resource metadata and data
     * @return the updated Project with the uploaded translated file
     */
    @Override
    public Project uploadTranslatedFile(UUID projectId, ResourceHeader resHeader) throws NoAccessToOperateException {

        User user = fetchUserFromContext();

        log.info("Uploading translated file for project ID: {}", projectId);
        Project project = this.fetchProject(projectId);


        // Check if caller is the assigned translator
        if (!project.getTranslator().getId().equals(user.getId())) {
            log.error("User with ID {} is not the assigned translator for project ID: {}", user.getId(), projectId);
            throw new NoAccessToOperateException("User is not the assigned translator for this project");
        }

        fileSystemService.saveTranslatedFile(projectId, resHeader.resourceData());
        log.info("Successfully uploaded translated file for project ID: {}", projectId);

        project.complete(resHeader.resourceName());

        projectService.updateProject(project);

        log.info("Project ID: {} marked as completed", projectId);

        eventPublisher.publish(new ProjectCompletedEvent(project));


        // Return updated project
        return fetchProject(projectId);


    }

    /**
     * Closes the project with the given ID.
     *
     * @param projectId the ID of the project to close
     * @return the closed Project
     */
    @Override
    public Project closeProject(UUID projectId) throws NoAccessToOperateException {

        User user = fetchUserFromContext();

        Project project = this.fetchProject(projectId);

        if (user.getRole() != UserRole.ADMINISTRATOR) {
            log.error("User with ID {} is not an admin", user.getId());
            throw new NoAccessToOperateException("User is not authorized to close the project");

        }

        project.close();

        projectService.updateProject(project);
        log.info("Project ID: {} marked as closed", projectId);

        eventPublisher.publish(new ProjectClosedEvent(project));

        // Return updated project
        return fetchProject(projectId);

    }

    /**
     * Approves the project with the given ID.
     *
     * @param projectId the ID of the project to approve
     * @return the approved Project
     */
    @Override
    public Project approveProject(UUID projectId) throws NoAccessToOperateException {

        User user = fetchUserFromContext();

        Project project = this.fetchProject(projectId);

        if (!project.getCustomer().getId().equals(user.getId())) {
            log.error("User with ID {} is not the owner of project ID: {}", user.getId(), projectId);
            throw new NoAccessToOperateException("User is not authorized to approve the project");
        }

        project.approve();

        projectService.updateProject(project);
        log.info("Project ID: {} marked as approved", projectId);

        eventPublisher.publish(new ProjectApprovedEvent(project));

        // Return updated project
        return fetchProject(projectId);

    }

    /**
     * Rejects the project with the given ID and provides feedback.
     *
     * @param projectId the ID of the project to reject
     * @param feedback  the feedback for rejection
     * @return the rejected Project
     */
    @Override
    public Project rejectProject(UUID projectId, String feedback) throws NoAccessToOperateException {

        User user = fetchUserFromContext();

        Project project = this.fetchProject(projectId);

        if (!project.getCustomer().getId().equals(user.getId())) {
            log.error("User with ID {} is not the owner of project ID: {}", user.getId(), projectId);
            throw new NoAccessToOperateException("User is not authorized to reject the project");
        }

        // Delete existing feedback
        feedbackService.deleteProjectFeedbackByProjectId(projectId);

        // Create feedback
        Feedback feedbackObject = project.reject(feedback);

        projectService.updateProject(project);
        log.info("Project ID: {} marked as rejected", projectId);

        eventPublisher.publish(new ProjectRejectedEvent(project, feedback));

        // Store feedback
        feedbackService.saveFeedback(feedbackObject);

        // Return updated project
        return fetchProject(projectId);
    }

    /**
     * Fetches the project by ID and handles not found exception.
     *
     * @param projectId the ID of the project
     * @return the Project
     * @throws ProjectNotFoundException if the project is not found
     */
    private Project fetchProject(UUID projectId) throws ProjectNotFoundException {
        Project project = projectService.getProjectById(projectId);

        if (project == null) {
            log.error("Project with ID {} not found", projectId);
            throw new ProjectNotFoundException("Project not found");
        }
        return project;
    }


    private User fetchUserFromContext() throws IllegalArgumentException {
        User user = callerContextProvider.getCaller();

        if (user == null) {
            log.error("Caller context does not contain a valid user");
            throw new IllegalArgumentException("Invalid caller context: user not found");
        }

        return user;
    }
}
