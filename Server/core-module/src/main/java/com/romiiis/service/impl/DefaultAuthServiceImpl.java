package com.romiiis.service.impl;

import com.romiiis.domain.User;
import com.romiiis.exception.EmailInUseException;
import com.romiiis.exception.InvalidAuthCredentialsException;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.interfaces.IAuthService;
import com.romiiis.service.interfaces.IJwtService;
import com.romiiis.service.interfaces.IPasswordHasher;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

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
    private final IJwtService jwtService;
    private final IPasswordHasher passwordHasher;

    /**
     * Logs in a user with the given email and hashed password.
     *
     * @param email    the email address of the user
     * @param password the password of the user
     * @return JWT token for the logged-in user
     * @throws InvalidAuthCredentialsException if the credentials are invalid
     * @throws EmailInUseException             if the email is already in use
     */
    @Override
    public String login(String email, String password) throws EmailInUseException {

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
            User user = userService.getUserByEmail(email);
            return jwtService.generateToken(user.getId(), user.getRole().name());
        }
    }

    /**
     * Registers a new customer and returns a JWT token.
     *
     * @param name     the name of the customer
     * @param email    the email address of the customer
     * @param password hashed password
     * @return JWT token for the registered customer
     */
    @Override
    public String registerCustomer(String name, String email, String password) throws InvalidAuthCredentialsException, EmailInUseException, UserNotFoundException {
        validateEmailForm(email);
        validateEmailUsage(email);

        // hash password
        String hashPassword = passwordHasher.hash(password);

        User u = userService.createNewCustomer(name, email, hashPassword);
        return jwtService.generateToken(u.getId(), u.getRole().name());
    }

    /**
     * Registers a new translator and returns a JWT token.
     *
     * @param name         the name of the translator
     * @param email        the email address of the translator
     * @param langs        the set of languages the translator is proficient in
     * @param password hashed password
     * @return JWT token for the registered translator
     */
    @Override
    public String registerTranslator(String name, String email, Set<Locale> langs, String password) throws InvalidAuthCredentialsException, EmailInUseException, UserNotFoundException {
        validateEmailForm(email);
        validateEmailUsage(email);

        String hashPassword = passwordHasher.hash(password);

        User u = userService.createNewTranslator(name, email, langs, hashPassword);
        return jwtService.generateToken(u.getId(), u.getRole().name());
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
}
