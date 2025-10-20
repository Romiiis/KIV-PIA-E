package com.romiiis.repository.impl;

import com.romiiis.configuration.ProjectMongoFilter;
import com.romiiis.configuration.UserMongoFilter;
import com.romiiis.configuration.UsersFilter;
import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.mapper.MongoUserMapper;
import com.romiiis.model.ProjectDB;
import com.romiiis.model.UserDB;
import com.romiiis.repository.IUserRepository;
import com.romiiis.repository.mongo.MongoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Optional<User> getUserById(UUID id){
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
    public void saveUser(User user) {
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
        return null;
    }


    /**
     * Checks if a user exists with the given email and hashed password.
     *
     * @param email          the email address of the user
     * @param hashedPassword the hashed password of the user
     * @return true if a user exists with the given email and hashed password, false otherwise
     */
    @Override
    public boolean userExists(String email, String hashedPassword) {
        return mongoRepo.existsByEmailAddressAndHashedPassword(email, hashedPassword);
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
}
