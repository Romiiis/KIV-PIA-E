package com.romiiis.service.impl;


import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.filter.UsersFilter;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Default implementation of the IUserService interface.
 *
 * @author Roman Pejs
 */
@RequiredArgsConstructor
@Slf4j
public class DefaultUserServiceImpl implements IUserService {

    /**
     * Repository for user data access
     */
    private final IUserRepository userRepository;

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return the User with the given email, or null if not found
     */
    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {

        Optional<User> userOpt = userRepository.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("User with email {} not found", email);
            throw new UserNotFoundException("User with email " + email + " not found");

        }
        return userOpt.get();
    }


    /**
     * Creates a new customer with the given details.
     * Saves the customer to the database and returns the created User object.
     *
     * @param name     the name of the customer
     * @param email    the email address of the customer
     * @param password hashed password
     * @return the created User object
     */
    @Override
    public User createNewCustomer(String name, String email, String password) throws UserNotFoundException {

        User newUser = User.createCustomer(name, email).withHashedPassword(password);
        userRepository.save(newUser);

        // Try to fetch the user back to ensure it was saved correctly
        Optional<User> savedUserOpt = userRepository.getUserByEmail(email);

        if (savedUserOpt.isEmpty()) {
            log.error("Failed to retrieve newly created customer with email {}", email);
            throw new UserNotFoundException("Failed to retrieve newly created customer");
        }
        log.info("New customer created: {}", newUser.getEmailAddress());
        return savedUserOpt.get();
    }


    /**
     * Creates a new translator with the given details.
     * Saves the translator to the database and returns the created User object.
     *
     * @param name     the name of the translator
     * @param email    the email address of the translator
     * @param langs    the set of languages the translator is proficient in
     * @param password hashed password
     * @return the created User object
     */
    @Override
    public User createNewTranslator(String name, String email, Set<Locale> langs, String password) throws UserNotFoundException {
        User newUser = User.createTranslator(name, email, langs).withHashedPassword(password);
        userRepository.save(newUser);

        Optional<User> savedUserOpt = userRepository.getUserByEmail(email);
        if (savedUserOpt.isEmpty()) {
            log.error("Failed to retrieve newly created translator with email {}", email);
            throw new UserNotFoundException("Failed to retrieve newly created translator");
        }
        log.info("New translator created: {}", newUser.getEmailAddress());
        return savedUserOpt.get();
    }


    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the UUID of the user to retrieve
     * @return the User with the given ID, or null if not found
     */
    @Override
    public User getUserById(UUID userId) {
        Optional<User> userOpt = userRepository.getUserById(userId);
        if (userOpt.isEmpty()) {
            log.warn("User with ID {} not found", userId);
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        return userOpt.get();

    }


    /**
     * Fetches all users with optional filtering.
     *
     * @param filter the filter criteria for fetching users
     * @return a list of users matching the filter criteria
     */
    @Override
    public List<User> getAllUsers(UsersFilter filter) {
        return userRepository.getAllUsers(filter);
    }

    /**
     * Retrieves the list of languages associated with a user.
     *
     * @param userId the UUID of the user
     * @return a list of language codes associated with the user
     */
    @Override
    public List<Locale> getUsersLanguages(UUID userId) throws IllegalArgumentException {

        if (userRepository.getRoleById(userId) != UserRole.TRANSLATOR) {
            log.error("User with ID {} is not a translator and has no associated languages", userId);
            throw new IllegalArgumentException("User with ID " + userId + " is not a translator");
        }

        return userRepository.getUsersLanguages(userId);
    }

    /**
     * Updates the set of languages associated with a user.
     *
     * @param userId    the UUID of the user
     * @param languages the new set of languages to associate with the user
     * @return the updated set of languages
     * @throws UserNotFoundException if the user with the given ID does not exist
     */
    @Override
    public Set<Locale> updateUserLanguages(UUID userId, Set<Locale> languages) throws UserNotFoundException {
        User user = getUserById(userId);

        if (user == null) {
            log.error("User with ID {} not found for language update", userId);
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        user.setLanguages(languages);
        userRepository.save(user);
        log.info("Updated languages for user ID {}: {}", userId, languages);

        user = getUserById(userId);
        return user.getLanguages();
    }
}
