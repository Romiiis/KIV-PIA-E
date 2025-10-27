package com.romiiis.service.interfaces;

import com.romiiis.domain.Feedback;
import com.romiiis.exception.FeedbackNotFoundException;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.exception.UserNotFoundException;

import java.util.UUID;

/**
 * Service interface for managing feedback.
 *
 * @author Roman Pejs
 */
public interface IFeedbackService {

    /**
     * Retrieves feedback by project ID.
     *
     * @param projectId the ID of the project
     * @return the Feedback associated with the project ID
     */
    Feedback getFeedbackByProjectId(UUID projectId) throws FeedbackNotFoundException, UserNotFoundException, ProjectNotFoundException;

    /**
     * Saves the feedback.
     *
     * @param feedback the Feedback to save
     */
    void saveFeedback(Feedback feedback) throws UserNotFoundException, ProjectNotFoundException;

    /**
     * Deletes existing feedback.
     *
     * @param projectId ID of the project whose feedback is to be deleted
     */
    void deleteProjectFeedback(UUID projectId) throws UserNotFoundException, ProjectNotFoundException ;
}
