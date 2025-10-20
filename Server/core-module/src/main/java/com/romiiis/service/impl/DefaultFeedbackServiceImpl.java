package com.romiiis.service.impl;

import com.romiiis.domain.Feedback;
import com.romiiis.exception.FeedbackNotFoundException;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.service.interfaces.IFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DefaultFeedbackServiceImpl implements IFeedbackService {

    private final IFeedbackRepository feedbackRepository;


    /**
     * Retrieves feedback by project ID.
     *
     * @param projectId the ID of the project
     * @return the Feedback associated with the project ID
     * @throws FeedbackNotFoundException if no feedback is found for the given project ID
     */
    @Override
    public Feedback getFeedbackByProjectId(UUID projectId) {
        log.info("Fetching feedback for project ID: {}", projectId);
        Feedback feedback = feedbackRepository.getFeedbackByProjectId(projectId);

        if (feedback == null) {
            log.info("No feedback found for project ID: {}", projectId);
            throw new FeedbackNotFoundException("No feedback found for project ID: " + projectId);
        }
        return feedbackRepository.getFeedbackByProjectId(projectId);
    }
}
