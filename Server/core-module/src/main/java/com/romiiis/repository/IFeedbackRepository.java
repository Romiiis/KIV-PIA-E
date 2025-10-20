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

}
