package com.romiiis.service.impl;

import com.romiiis.domain.User;
import com.romiiis.exception.EmailInUseException;
import com.romiiis.exception.InvalidAuthCredentialsException;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.repository.IUserRepository;
import com.romiiis.security.CallerContextProvider;
import com.romiiis.service.interfaces.IAuthService;
import com.romiiis.service.interfaces.IPasswordHasher;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Default implementation of the IAuthService interface.
 *
 * @author Roman Pejs
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAuthServiceImpl implements IAuthService {

    /**
     * Repositories & Services
     */
    private final IUserService userService;
    private final IUserRepository userRepository;
    private final IPasswordHasher passwordHasher;
    private final CallerContextProvider callerContextProvider;

    /**
     * Logs in a user with the given email and hashed password.
     *
     * @param email    the email address of the user
     * @param password the password of the user
     * @return the authenticated User
     * @throws InvalidAuthCredentialsException if the credentials are invalid
     * @throws EmailInUseException             if the email is already in use
     */
    @Override
    public User login(String email, String password) throws EmailInUseException {

        // validate credentials
        validateEmailForm(email);

        Optional<String> passwordFromDb = userRepository.getUserPasswordHash(email);

        if (passwordFromDb.isEmpty()) {
            log.warn("Auth failed: Email {} not found", email);
            throw new InvalidAuthCredentialsException();
        }

        boolean exists = passwordHasher.verify(password, passwordFromDb.get());

        if (!exists) {
            log.warn("Auth failed: Invalid credentials for email {}", email);
            throw new InvalidAuthCredentialsException();
        } else {
            return callerContextProvider.runAsSystem(()-> userService.getUserByEmail(email));
        }
    }


    /**
     * Validates the email format and checks if it's already in use.
     *
     * @param email the email address to validate
     * @throws InvalidAuthCredentialsException if the email format is invalid
     * @throws EmailInUseException             if the email is already in use
     */
    private void validateEmailForm(String email) throws InvalidAuthCredentialsException {
        if (!email.contains("@") || email.trim().isEmpty()) {
            log.warn("Auth failed: Email {} is not valid", email);
            throw new InvalidAuthCredentialsException();
        }
    }

    /**
     * Validates if the email is already in use.
     *
     * @param email the email address to check
     * @throws EmailInUseException if the email is already in use
     */
    private void validateEmailUsage(String email) throws EmailInUseException {
        if (userRepository.emailInUse(email)) {
            log.warn("Registration failed: Email {} is already in use", email);
            throw new EmailInUseException();
        }
    }


    @Override
    public User registerUser(String name, String email, String password) throws InvalidAuthCredentialsException, EmailInUseException, UserNotFoundException {
        validateEmailForm(email);
        validateEmailUsage(email);

        // hash password
        String hashPassword = passwordHasher.hash(password);

        return userService.createNewUser(name, email, hashPassword);

    }
}
