package com.romiiis.repository.mongo;

import com.romiiis.model.ProjectDB;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing ProjectDB entities in MongoDB.
 * This interface extends MongoRepository to provide CRUD operations and custom queries for ProjectDB entities.
 *
 * @author Roman Pejs
 */
@Repository
public interface MongoProjectRepository extends MongoRepository<ProjectDB, UUID> {

    /**
     * Finds all ProjectDB entities and returns only their IDs.
     *
     * @return a list of ProjectDB entities with only the ID field populated
     */
    @Query(value = "{}", fields = "{ '_id' : 1 }")
    List<ProjectDB> findAllIds();


    /**
     * Counts the number of ProjectDB entities associated with a specific translator ID.
     *
     * @param translatorId the UUID of the translator
     * @return the count of ProjectDB entities for the given translator ID
     */
    @Query(value = "{ 'translator.$id': ?0 }", count = true)
    int countByTranslatorId(UUID translatorId);
}
