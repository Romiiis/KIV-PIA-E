package com.romiiis.service.api;

import com.romiiis.domain.User;
import com.romiiis.exception.EmailInUseException;
import com.romiiis.exception.InvalidAuthCredentialsException;
import com.romiiis.exception.UserNotFoundException;

/**
 * Service interface for authentication-related operations.
 * Includes methods for registering customers and translators.
 * Token management also
 *
 * @author Roman Pejs
 */
public interface IAuthService {


    User registerUser(String name, String email, String password) throws InvalidAuthCredentialsException, EmailInUseException, UserNotFoundException;

    /**
     * Authenticates a user and returns a JWT token if the credentials are valid.
     *
     * @param email    Email of the user
     * @param password Plain text password of the user
     * @return JWT token as a String
     * @throws EmailInUseException if the email is already in use
     */
    User login(String email, String password) throws EmailInUseException;

}
