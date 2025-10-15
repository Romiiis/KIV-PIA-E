package com.romiiis.service.interfaces;

import com.romiiis.exception.EmailInUseException;
import com.romiiis.exception.InvalidAuthCredentialsException;

import java.util.Locale;
import java.util.Set;

/**
 * Service interface for authentication-related operations.
 * Includes methods for registering customers and translators.
 * Token management also
 *
 * @author Roman Pejs
 */
public interface IAuthService {

    /**
     * Registers a new customer and returns a JWT token upon successful registration.
     * @param name Name of the customer
     * @param email Email of the customer
     * @param hashPassword Hashed password of the customer
     * @return JWT token as a String
     */
    String registerCustomer(String name, String email, String hashPassword) throws InvalidAuthCredentialsException, EmailInUseException;

    /**
     * Registers a new translator and returns a JWT token upon successful registration.
     * @param name Name of the translator
     * @param email Email of the translator
     * @param langs Set of languages the translator is proficient in
     * @param hashPassword Hashed password of the translator
     * @return JWT token as a String
     */
    String registerTranslator(String name, String email, Set<Locale> langs, String hashPassword) throws InvalidAuthCredentialsException, EmailInUseException;


    /**
     * Authenticates a user and returns a JWT token if the credentials are valid.
     * @param email Email of the user
     * @param hashPassword Plain text password of the user
     * @return JWT token as a String
     */
    String login(String email, String hashPassword);

}
