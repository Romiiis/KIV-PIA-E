package com.romiiis.repository;

import com.romiiis.domain.Feedback;

import java.util.UUID;

/**
 * Repository interface for feedback-related operations
 */
public interface IFeedbackRepository {

    /**
     * Fetches feedback by the associated project ID
     * @param projectId project ID
     * @return feedback associated with the given project ID
     */
    Feedback getFeedbackByProjectId(UUID projectId);


    /**
     * Saves feedback to the repository
     * @param feedback feedback to save
     */
    void save(Feedback feedback);

    /**
     * Deletes feedback from the repository
     * @param projectId ID of the project whose feedback is to be deleted
     */
    void deleteForProject(UUID projectId);


    /**
     * Deletes all feedback entries from the repository
     */
    void deleteAll();

}
