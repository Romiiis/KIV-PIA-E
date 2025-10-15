package com.romiiis.repository.mongo;

import com.romiiis.model.UserDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MongoUserRepository extends MongoRepository<UserDB, UUID> {
}
