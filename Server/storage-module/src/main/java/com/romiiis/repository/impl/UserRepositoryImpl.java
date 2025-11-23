package com.romiiis.repository.impl;

import com.mongodb.client.MongoClient;
import com.romiiis.configuration.UserMongoFilter;
import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.filter.UsersFilter;
import com.romiiis.mapper.MongoUserMapper;
import com.romiiis.model.UserDB;
import com.romiiis.model.UserRoleDB;
import com.romiiis.repository.IUserRepository;
import com.romiiis.repository.mongo.MongoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Implementation of the IUserRepository interface using MongoDB.
 *
 * @author Roman Pejs
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {

    /**
     * MongoDB repository for user data access
     */
    private final MongoUserRepository mongoRepo;
    private final MongoUserMapper mapper;
    private final MongoTemplate mongoTemplate;



    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the UUID of the user to retrieve
     * @return the User with the given ID, or null if not found
     */
    @Override
    public Optional<User> getUserById(UUID id) {
        UserDB userDB = mongoRepo.findById(id).orElse(null);
        if (userDB == null) {
            return Optional.empty();
        }
        return Optional.of(mapper.mapDBToDomain(userDB));
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return the User with the given email, or null if not found
     */
    @Override
    public Optional<User> getUserByEmail(String email) {
        Optional<UserDB> userDBOpt = mongoRepo.findByEmailAddress(email);
        return userDBOpt.map(mapper::mapDBToDomain);
    }

    /**
     * Saves a user to the database.
     *
     * @param user the User to save
     */
    @Override
    public void save(User user) {
        mongoRepo.save(mapper.mapDomainToDB(user));

    }

    /**
     * Retrieves a user role by its unique identifier.
     *
     * @param id the UUID of the user role to retrieve
     * @return the UserRole with the given ID, or null if not found
     */
    @Override
    public UserRole getRoleById(UUID id) {
        Optional<UserDB> userDBOpt = mongoRepo.findById(id);
        return userDBOpt.map(db -> mapper.mapDBToDomain(db.getRole())).orElse(null);
    }


    @Override
    public Optional<String> getUserPasswordHash(String email) {
        Optional<UserDB> userDBOpt = mongoRepo.findByEmailAddress(email);
        return userDBOpt.map(UserDB::getHashedPassword);
    }

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address of the user
     * @return true if a user exists with the given email address, false otherwise
     */
    @Override
    public boolean emailInUse(String email) {
        return mongoRepo.existsByEmailAddress(email);
    }

    /**
     * Fetches all users with optional filtering.
     *
     * @param filter the filter criteria for fetching users
     * @return a list of users matching the filter criteria
     */
    @Override
    public List<User> getAllUsers(UsersFilter filter) {
        Criteria criteria = UserMongoFilter.toCriteria(filter);
        Query query = new Query(criteria);

        List<UserDB> dbProjects = mongoTemplate.find(query, UserDB.class);
        return mapper.mapDBListToDomain(dbProjects);
    }

    /**
     * Get the list of languages associated with a user.
     * MUST be a translator.
     *
     * @param userId the UUID of the user
     * @return a list of language codes associated with the user
     */
    @Override
    public List<Locale> getUsersLanguages(UUID userId) {
        Query query = new Query(Criteria.where("id").is(userId)
                .and("role").is(UserRoleDB.TRANSLATOR));
        query.fields().include("languages");

        UserDB user = mongoTemplate.findOne(query, UserDB.class);
        return user != null ? new ArrayList<>(user.getLanguages()) : List.of();
    }


    /**
     * Get the list of translator IDs proficient in a specific language.
     *
     * @param language the target language
     * @return a list of translator UUIDs proficient in the specified language
     */
    @Override
    public List<UUID> getTranslatorsIdsByLanguage(Locale language) {
        Query query = new Query();
        query.addCriteria(Criteria.where("role").is(UserRoleDB.TRANSLATOR));
        query.addCriteria(Criteria.where("languages").is(language));

        query.fields().include("_id");

        List<UserDB> users = mongoTemplate.find(query, UserDB.class, "users");

        return users.stream()
                .map(UserDB::getId)
                .toList();

    }

    /**
     * Deletes all users from the database.
     */
    @Override
    public void deleteAll() {
       mongoRepo.deleteAll();
    }

    @Override
    public boolean loggedUsingOAuth(String email) {
        // Check if user with given email exists and his password hash is null
        Query query = new Query(Criteria.where("emailAddress").is(email)
                .and("hashedPassword").is(null));
        return mongoTemplate.exists(query, UserDB.class);
    }
}
