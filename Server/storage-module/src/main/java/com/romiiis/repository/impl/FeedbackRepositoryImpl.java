package com.romiiis.repository.impl;

import com.romiiis.domain.Feedback;
import com.romiiis.mapper.MongoFeedbackMapper;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.repository.mongo.MongoFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements IFeedbackRepository {

    private final MongoFeedbackRepository mongoFeedbackRepository;
    private final MongoFeedbackMapper mapper;

    /**
     * Retrieves feedback by project ID.
     *
     * @param projectId the UUID of the project
     * @return the Feedback associated with the given project ID
     */
    @Override
    public Feedback getFeedbackByProjectId(UUID projectId) {
        return mapper.mapDBToDomain(
                mongoFeedbackRepository.getFeedbackById(projectId)
        );
    }
}
