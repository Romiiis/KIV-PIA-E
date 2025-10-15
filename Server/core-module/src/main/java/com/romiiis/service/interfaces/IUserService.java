package com.romiiis.service.interfaces;

import com.romiiis.domain.User;

import java.util.Locale;
import java.util.Set;

public interface IUserService {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return the User with the given email, or null if not found
     */
    User getUserByEmail(String email);

    /**
     * Creates a new customer with the given details.
     *
     * @param name     the name of the customer
     * @param email    the email address of the customer
     * @param password hashed password
     */
    User createNewCustomer(String name, String email, String password);


    /**
     * Creates a new user with the given details.
     *
     * @param name     the name of the user
     * @param email    the email address of the user
     * @param langs    the set of languages the user is proficient in
     * @param password hashed password
     */
    User createNewTranslator(String name, String email, Set<Locale> langs, String password);

}
