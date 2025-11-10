package com.romiiis.repository.mongo;


import com.romiiis.model.FeedbackDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Feedbacks entities in MongoDB.
 * This interface extends MongoRepository to provide CRUD operations and custom queries for FeedbackDB entities.
 *
 * @author Roman Pejs
 */
@Repository
public interface MongoFeedbackRepository extends MongoRepository<FeedbackDB, UUID> {

    /**
     * Retrieves a FeedbackDB entity by its unique identifier.
     *
     * @param id the UUID of the feedback to retrieve
     * @return the FeedbackDB entity with the given ID, or null if not found
     */
    FeedbackDB getFeedbackByProjectId(UUID id);


    /**
     * Deletes a FeedbackDB entity by its associated project ID.
     *
     * @param projectId the UUID of the project whose feedback is to be deleted
     */
    void deleteByProjectId(UUID projectId);

    /**
     * Finds all FeedbackDB entities associated with the given list of project IDs.
     *
     * @param projectIds a list of UUIDs representing project IDs
     * @return a list of FeedbackDB entities associated with the specified project IDs
     */
    @Query("{ 'projectId': { $in: ?0 } }")
    List<FeedbackDB> findByProjectIdIn(List<UUID> projectIds);

}

