package com.romiiis.repository;

import com.romiiis.filter.UsersFilter;
import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for user-related operations
 */
public interface IUserRepository {

    /**
     * Fetches a user by their ID
     * @param id user ID
     * @return user with the given ID, or null if not found
     */
    Optional<User> getUserById(UUID id);

    /**
     * Fetches a user by their email
     * @param email user email
     * @return user with the given email, or null if not found
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Saves a user to the repository
     * @param user user to save
     */
    void save(User user);


    /**
     * Fetches a user role by its ID
     * @param id role ID
     * @return user role with the given ID, or null if not found
     */
    UserRole getRoleById(UUID id);

    /**
     * Retrieves the password hash for a user by their email.
     *
     * @param email email of the user
     * @return password hash of the user
     */
    Optional<String> getUserPasswordHash(String email);

    /**
     * Checks if an email is already in use by another user
     * @param email email to check
     * @return true if the email is already in use, false otherwise
     */
    boolean emailInUse(String email);


    /**
     * Fetches all users with optional filtering
     * @param filter filter criteria
     * @return list of users matching the filter
     */
    List<User> getAllUsers(UsersFilter filter);


    /**
     * Retrieves the list of languages associated with a user.
     *
     * @param userId the UUID of the user
     * @return a list of language codes associated with the user
     */
    List<Locale> getUsersLanguages(UUID userId);
}
