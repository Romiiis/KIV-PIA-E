package com.romiiis.repository.mongo;

import com.romiiis.model.ProjectDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MongoProjectRepository extends MongoRepository<ProjectDB, UUID> {

}
