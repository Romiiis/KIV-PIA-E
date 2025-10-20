package com.romiiis.service.interfaces;

import com.romiiis.domain.Feedback;

import java.util.UUID;

/**
 * Service interface for managing feedback.
 *
 * @author Roman Pejs
 */
public interface IFeedbackService {

    Feedback getFeedbackByProjectId(UUID projectId);

}
