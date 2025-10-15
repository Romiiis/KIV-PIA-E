package com.romiiis.repository.mongo;

import com.romiiis.model.ProjectDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing ProjectDB entities in MongoDB.
 * This interface extends MongoRepository to provide CRUD operations and custom queries for ProjectDB entities.
 *
 * @author Roman Pejs
 */
@Repository
public interface MongoProjectRepository extends MongoRepository<ProjectDB, UUID> {

}
