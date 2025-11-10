package com.romiiis.repository.impl;

import com.romiiis.domain.Feedback;
import com.romiiis.mapper.MongoFeedbackMapper;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.repository.mongo.MongoFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
                mongoFeedbackRepository.getFeedbackByProjectId(projectId)
        );
    }

    /**
     * Saves feedback to the repository.
     *
     * @param feedback the Feedback to save
     */
    @Override
    public void save(Feedback feedback) {
        mongoFeedbackRepository.save(
                mapper.mapDomainToDB(feedback)
        );
    }

    /**
     * Deletes feedback for the specified project ID.
     *
     * @param projectId the UUID of the project whose feedback is to be deleted
     */
    @Override
    public void deleteForProject(UUID projectId) {
        mongoFeedbackRepository.deleteByProjectId(projectId);
    }

    /**
     * Deletes all feedback entries from the repository.
     */
    @Override
    public void deleteAll() {
        mongoFeedbackRepository.deleteAll();
    }

    @Override
    public List<Feedback> getAllFeedbackForProjectIds(List<UUID> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of();
        }

        return mongoFeedbackRepository.findByProjectIdIn(projectIds)
                .stream()
                .map(mapper::mapDBToDomain)
                .toList();    }
}
