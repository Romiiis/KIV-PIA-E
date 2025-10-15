package com.romiiis.repository.mongo;

import com.romiiis.model.UserDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * MongoDB repository interface for UserDB entities.
 * Provides CRUD operations and custom queries for UserDB objects.
 *
 * @author Roman Pejs
 */
@Repository
public interface MongoUserRepository extends MongoRepository<UserDB, UUID> {

    /**
     * Checks if a user exists with the given email and hashed password.
     * (AUTO IMPLEMENTED by Spring Data MongoDB)
     * @param emailAddress the email address of the user
     * @param hashedPassword the hashed password of the user
     * @return true if a user exists with the given email and hashed password, false otherwise
     */
    boolean existsByEmailAddressAndHashedPassword(String emailAddress, String hashedPassword);

    /**
     * Checks if a user exists with the given email address.
     * (AUTO IMPLEMENTED by Spring Data MongoDB)
     * @param emailAddress the email address of the user
     * @return true if a user exists with the given email address, false otherwise
     */
    boolean existsByEmailAddress(String emailAddress);

}
