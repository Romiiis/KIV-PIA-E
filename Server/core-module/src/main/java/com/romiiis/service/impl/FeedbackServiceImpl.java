package com.romiiis.service.impl;

import com.romiiis.domain.Feedback;
import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.exception.FeedbackNotFoundException;
import com.romiiis.exception.NoAccessToOperateException;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.service.api.IFeedbackService;
import com.romiiis.service.api.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class FeedbackServiceImpl implements IFeedbackService {

    private final IFeedbackRepository feedbackRepository;
    private final IProjectService projectService;
    private final IExecutionContextProvider callerContextProvider;


    /**
     * Retrieves feedback by project ID.
     *
     * @param projectId the ID of the project
     * @return the Feedback associated with the project ID
     * @throws FeedbackNotFoundException if no feedback is found for the given project ID
     */
    @Override
    @Transactional(readOnly = true)
    public Feedback getFeedbackByProjectId(UUID projectId) throws FeedbackNotFoundException, UserNotFoundException, ProjectNotFoundException {

        User caller = fetchUserFromContext();
        Project project = fetchProject(projectId);

        // If caller is not admin, check ownership or assignment
        if (caller.getRole() != UserRole.ADMINISTRATOR) {
            boolean isProjectOwner = project.getCustomer().getId().equals(caller.getId());
            boolean isAssignedTranslator = project.getTranslator() != null &&
                    project.getTranslator().getId().equals(caller.getId());
            if (!isProjectOwner && !isAssignedTranslator) {
                log.error("User with ID {} is not authorized to access feedback for project ID: {}", caller.getId(), projectId);
                throw new NoAccessToOperateException("User is not authorized to access feedback for this project");
            }
        }

        log.info("Fetching feedback for project ID: {}", projectId);
        Feedback feedback = feedbackRepository.getFeedbackByProjectId(projectId);

        if (feedback == null) {
            log.info("No feedback found for project ID: {}", projectId);
            throw new FeedbackNotFoundException("No feedback found for project ID: " + projectId);
        }
        return feedbackRepository.getFeedbackByProjectId(projectId);
    }

    /**
     * Saves feedback to the repository.
     *
     * @param feedback the Feedback to save
     */
    @Override
    @Transactional(readOnly = false)
    public void saveFeedback(Feedback feedback) throws UserNotFoundException, ProjectNotFoundException {

        User user = fetchUserFromContext();
        Project project = fetchProject(feedback.getProjectId());

        if (!project.getCustomer().getId().equals(user.getId())) {
            log.error("User with ID {} is not the owner of project ID: {}", user.getId(), feedback.getProjectId());
            throw new NoAccessToOperateException("User is not authorized to save feedback for this project");
        }


        log.info("Saving feedback for project ID: {}", feedback.getProjectId());

        feedbackRepository.save(feedback);

        log.info("Feedback saved successfully for project ID: {}", feedback.getProjectId());
    }

    /**
     * Deletes existing feedback from the repository.
     *
     * @param projectId ID of the project whose feedback is to be deleted
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteProjectFeedbackByProjectId(UUID projectId) throws UserNotFoundException, ProjectNotFoundException {

        User user = fetchUserFromContext();
        Project project = fetchProject(projectId);


        if (!project.getCustomer().getId().equals(user.getId())) {
            log.error("User with ID {} is not the owner of project ID: {}", user.getId(), projectId);
            throw new NoAccessToOperateException("User is not authorized to delete feedback for this project");
        }


        log.info("Deleting feedback for project ID: {}", projectId);

        feedbackRepository.deleteForProject(projectId);

        log.info("Feedback deleted successfully for project ID: {}", projectId);
    }


    // Helper methods

    /**
     * Fetches the user from the caller context.
     *
     * @return the User
     * @throws UserNotFoundException if no valid user is found in the context
     */
    private User fetchUserFromContext() throws UserNotFoundException {
        User user = callerContextProvider.getCaller();

        if (user == null) {
            log.error("Caller context does not contain a valid user");
            throw new UserNotFoundException("Invalid caller context: user not found");
        }

        return user;
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


    /**
     * Retrieves all feedbacks for the given list of project IDs.
     * ONLY SYSTEM CONTEXT CAN CALL THIS METHOD
     *
     * @param projectIds List of project IDs
     * @return List of Feedbacks associated with the given project IDs
     * @throws UserNotFoundException        if no valid user is found in the context
     * @throws ProjectNotFoundException     if any of the projects are not found
     */
    @Override
    @Transactional(readOnly = true)
    public List<Feedback> getAllFeedbacksByProjectIds(List<UUID> projectIds) throws UserNotFoundException, ProjectNotFoundException {

        if (!callerContextProvider.isSystem()) {
            log.error("Only system context can access all feedbacks by project IDs");
            throw new NoAccessToOperateException("Only system context can access all feedbacks by project IDs");
        }

        log.info("Fetching all feedbacks for project IDs: {}", projectIds);
        return feedbackRepository.getAllFeedbackForProjectIds(projectIds);


    }
}
