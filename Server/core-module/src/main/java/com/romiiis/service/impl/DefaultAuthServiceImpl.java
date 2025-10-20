package com.romiiis.service.impl;

import com.romiiis.exception.EmailInUseException;
import com.romiiis.exception.InvalidAuthCredentialsException;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.interfaces.IAuthService;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

/**
 * Default implementation of the IAuthService interface.
 *
 * @author Roman Pejs
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultAuthServiceImpl implements IAuthService {

    /** Repositories & Services */
    private final IUserService userService;
    private final IUserRepository userRepository;

    @Override
    public String login(String email, String hashPassword) throws EmailInUseException {

        // validate credentials
        validateEmailForm(email);


        boolean exists = userRepository.userExists(email, hashPassword);

        if (!exists) {

            log.warn("Auth failed: Invalid credentials for email {}", email);
            throw new InvalidAuthCredentialsException();
        } else {
            return "JWT_TOKEN";
        }
    }

    /**
     * Registers a new customer and returns a JWT token.
     *
     * @param name         the name of the customer
     * @param email        the email address of the customer
     * @param hashPassword hashed password
     * @return JWT token for the registered customer
     */
    @Override
    public String registerCustomer(String name, String email, String hashPassword) throws InvalidAuthCredentialsException, EmailInUseException {
        validateEmailForm(email);
        validateEmailUsage(email);
        userService.createNewCustomer(name, email, hashPassword);
        return "JWT_TOKEN";

    }

    /**
     * Registers a new translator and returns a JWT token.
     *
     * @param name         the name of the translator
     * @param email        the email address of the translator
     * @param langs        the set of languages the translator is proficient in
     * @param hashPassword hashed password
     * @return JWT token for the registered translator
     */
    @Override
    public String registerTranslator(String name, String email, Set<Locale> langs, String hashPassword) {
        validateEmailForm(email);
        validateEmailUsage(email);
        userService.createNewTranslator(name, email, langs, hashPassword);
        return "JWT_TOKEN";
    }


    /**
     * Validates the email format and checks if it's already in use.
     *
     * @param email the email address to validate
     * @throws InvalidAuthCredentialsException if the email format is invalid
     * @throws EmailInUseException                     if the email is already in use
     */
    private void validateEmailForm(String email) throws InvalidAuthCredentialsException, EmailInUseException {
        if (!email.contains("@") || email.trim().isEmpty()) {
            log.warn("Auth failed: Email {} is not valid", email);
            throw new InvalidAuthCredentialsException();
        }
    }

    private void validateEmailUsage(String email) throws EmailInUseException {
        if (userRepository.emailInUse(email)) {
            log.warn("Registration failed: Email {} is already in use", email);
            throw new EmailInUseException();
        }
    }



}
