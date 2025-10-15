package com.romiiis.repository;

import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;

import java.util.UUID;

public interface IUserRepository {

    /**
     * Fetches a user by their ID
     * @param id user ID
     * @return user with the given ID, or null if not found
     */
    User getUserById(UUID id);

    /**
     * Fetches a user by their email
     * @param email user email
     * @return user with the given email, or null if not found
     */
    User getUserByEmail(String email);

    /**
     * Saves a user to the repository
     * @param user user to save
     */
    void saveUser(User user);


    /**
     * Fetches a user role by its ID
     * @param id role ID
     * @return user role with the given ID, or null if not found
     */
    UserRole getRoleById(UUID id);

}
