package com.romiiis.service.interfaces;

import com.romiiis.configuration.UsersFilter;
import com.romiiis.domain.User;
import com.romiiis.exception.UserNotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for managing users.
 *
 * @author Roman Pejs
 */
public interface IUserService {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return the User with the given email, or null if not found
     */
    User getUserByEmail(String email) throws UserNotFoundException;

    /**
     * Creates a new customer with the given details.
     * Saves the customer to the database and returns the created User object.
     *
     * @param name     the name of the customer
     * @param email    the email address of the customer
     * @param password hashed password
     */
    User createNewCustomer(String name, String email, String password) throws UserNotFoundException;


    /**
     * Creates a new user with the given details.
     * Saves the user to the database and returns the created User object.
     *
     * @param name     the name of the user
     * @param email    the email address of the user
     * @param langs    the set of languages the user is proficient in
     * @param password hashed password
     */
    User createNewTranslator(String name, String email, Set<Locale> langs, String password) throws UserNotFoundException;

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the UUID of the user to retrieve
     * @return the User with the given ID, or null if not found
     */
    User getUserById(UUID userId) throws UserNotFoundException;


    /**
     * Fetches all users with optional filtering.
     *
     * @param filter the filter criteria for fetching users
     * @return a list of users matching the filter criteria
     */
    List<User> getAllUsers(UsersFilter filter);

}
