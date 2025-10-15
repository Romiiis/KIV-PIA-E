package com.romiiis.repository.impl;

import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.mapper.MongoUserMapper;
import com.romiiis.repository.IUserRepository;
import com.romiiis.repository.mongo.MongoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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


    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the UUID of the user to retrieve
     * @return the User with the given ID, or null if not found
     */
    @Override
    public User getUserById(UUID id) {
        return null;
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return the User with the given email, or null if not found
     */
    @Override
    public User getUserByEmail(String email) {
        return mongoRepo.findAll()
                .stream()
                .map(mapper::mapDBToDomain)
                .filter(user -> user.getEmailAddress().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
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
}
