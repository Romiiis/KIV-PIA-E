package com.romiiis.service.impl;


import com.romiiis.domain.User;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

/**
 * Default implementation of the IUserService interface.
 *
 * @author Roman Pejs
 */
@Service
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
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
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
    public User createNewCustomer(String name, String email, String password) {

        User newUser = User.createCustomer(name, email).withHashedPassword(password);
        userRepository.saveUser(newUser);
        log.info("New customer created: {}", newUser.getEmailAddress());
        return newUser;
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
    public User createNewTranslator(String name, String email, Set<Locale> langs, String password) {
        User newUser = User.createTranslator(name, email, langs).withHashedPassword(password);
        userRepository.saveUser(newUser);
        log.info("New translator created: {}", newUser.getEmailAddress());
        return newUser;
    }


}
