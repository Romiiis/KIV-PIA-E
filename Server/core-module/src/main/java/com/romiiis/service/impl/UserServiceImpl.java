package com.romiiis.service.impl;


import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.exception.MyIllegalParametersException;
import com.romiiis.exception.NoAccessToOperateException;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.filter.UsersFilter;
import com.romiiis.repository.IUserRepository;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.service.api.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Default implementation of the IUserService interface.
 *
 * @author Roman Pejs
 */
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

    /**
     * Repository for user data access
     */
    private final IUserRepository userRepository;
    private final IExecutionContextProvider callerContextProvider;

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address to search for
     * @return the User with the given email, or null if not found
     */
    @Override
    @Transactional(readOnly = false)
    public User getUserByEmail(String email) throws UserNotFoundException, NoAccessToOperateException {
        if (!callerContextProvider.isSystem()) {

            User caller = fetchUserFromContext();

            if (caller.getRole() != UserRole.ADMINISTRATOR) {

                // Non-admin users can only fetch their own data and translators can fetch data of customers assigned to their projects
                if (!caller.getEmailAddress().equalsIgnoreCase(email)) {
                    log.error("User with email {} is not authorized to access data of email {}", caller.getEmailAddress(), email);
                    throw new NoAccessToOperateException("User is not authorized to access data of email " + email);
                }
            }

        }

        Optional<User> userOpt = userRepository.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("User with email {} not found", email);
            throw new UserNotFoundException("User with email " + email + " not found");

        }
        return userOpt.get();
    }

    @Override
    @Transactional(readOnly = false)
    public User createNewUser(String name, String email, String password) throws UserNotFoundException {
        User newUser = User.createUser(name, email, password);
        userRepository.save(newUser);
        log.info("New user created: {}", newUser.getEmailAddress());
        return newUser;
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
    @Transactional(readOnly = false)
    public User createNewCustomer(String name, String email, String password) throws UserNotFoundException {

        User newUser = User.createCustomer(name, email).withHashedPassword(password);
        userRepository.save(newUser);
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
    @Transactional(readOnly = false)
    public User createNewTranslator(String name, String email, Set<Locale> langs, String password) throws UserNotFoundException {
        User newUser = User.createTranslator(name, email, langs).withHashedPassword(password);
        userRepository.save(newUser);
        log.info("New translator created: {}", newUser.getEmailAddress());
        return newUser;
    }


    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the UUID of the user to retrieve
     * @return the User with the given ID, or null if not found
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        if (!callerContextProvider.isSystem()) {

            User caller = fetchUserFromContext();

            if (caller.getRole() != UserRole.ADMINISTRATOR) {

                // Non-admin users can only fetch their own data
                if (!caller.getId().equals(userId)) {
                    log.error("User with ID {} is not authorized to access data of user ID {}", caller.getId(), userId);
                    throw new NoAccessToOperateException("User is not authorized to access data of user ID " + userId);
                }
            }

        }

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
    @Transactional(readOnly = true)
    public List<User> getAllUsers(UsersFilter filter) {
        if (!callerContextProvider.isSystem()) {
            User caller = fetchUserFromContext();

            if (caller.getRole() != UserRole.ADMINISTRATOR) {
                log.error("User with ID {} is not authorized to fetch all users", caller.getId());
                throw new NoAccessToOperateException("User is not authorized to fetch all users");
            }
        }
        return userRepository.getAllUsers(filter);
    }

    /**
     * Retrieves the list of languages associated with a user.
     *
     * @param userId the UUID of the user
     * @return a list of language codes associated with the user
     */
    @Override
    @Transactional(readOnly = true)
    public List<Locale> getUsersLanguages(UUID userId) throws UserNotFoundException {

        if (!callerContextProvider.isSystem()) {
            User caller = fetchUserFromContext();

            if (caller.getRole() != UserRole.ADMINISTRATOR && !caller.getId().equals(userId)) {
                log.error("User with ID {} is not authorized to access languages of user ID {}", caller.getId(), userId);
                throw new NoAccessToOperateException("User is not authorized to access languages of user ID " + userId);
            }
        }

        if (userRepository.getRoleById(userId) != UserRole.TRANSLATOR) {
            log.error("User with ID {} is not a translator and has no associated languages", userId);
            throw new UserNotFoundException("User with ID " + userId + " is not a translator");
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
    @Transactional(readOnly = false)
    public Set<Locale> updateUserLanguages(UUID userId, Set<Locale> languages) throws UserNotFoundException {

        // Only translator themselves
        if (!callerContextProvider.isSystem()) {
            User caller = fetchUserFromContext();

            if (caller.getRole() != UserRole.TRANSLATOR || !caller.getId().equals(userId)) {
                log.error("User with ID {} is not authorized to update languages of user ID {}", caller.getId(), userId);
                throw new NoAccessToOperateException("User is not authorized to update languages of user ID " + userId);
            }
        }

        if (languages == null || languages.isEmpty()) {
            log.error("Languages set cannot be null or empty for user ID {}", userId);
            throw new MyIllegalParametersException("Languages set cannot be null or empty");
        }

        User user = getUserById(userId);

        user.setLanguages(languages);
        userRepository.save(user);
        log.info("Updated languages for user ID {}: {}", userId, languages);

        user = getUserById(userId);
        return user.getLanguages();
    }

    /**
     * Retrieves a list of translator IDs proficient in the specified language.
     *
     * @param language the target language
     * @return a list of translator UUIDs proficient in the specified language
     */
    @Override
    @Transactional(readOnly = true)
    public List<UUID> getTranslatorIdsByLanguage(Locale language) {
        return userRepository.getTranslatorsIdsByLanguage(language);
    }

    /**
     * Retrieves the role of a user by their unique identifier.
     *
     * @param userId the UUID of the user
     * @return the UserRole of the user
     */
    @Override
    @Transactional(readOnly = true)
    public UserRole getUserRole(UUID userId) throws UserNotFoundException {
        return userRepository.getRoleById(userId);
    }


    /**
     * Fetches the caller user from the context.
     *
     * @return the caller User
     * @throws UserNotFoundException if the caller is not found in the context
     */
    private User fetchUserFromContext() throws UserNotFoundException {
        User caller = callerContextProvider.getCaller();
        if (caller == null) {
            log.error("Caller not found in context");
            throw new UserNotFoundException("Caller not found");
        }
        return caller;
    }

    /**
     * Creates a new administrator with the given details.
     * Saves the administrator to the database and returns the created User object.
     *
     * @param name     the name of the administrator
     * @param email    the email address of the administrator
     * @param password hashed password
     * @return the created User object
     */
    @Override
    @Transactional(readOnly = false)
    public User createNewAdmin(String name, String email, String password) throws UserNotFoundException {
        User newUser = User.createAdmin(name, email).withHashedPassword(password);
        userRepository.save(newUser);
        log.info("New administrator created: {}", newUser.getEmailAddress());
        return newUser;
    }


    /**
     * Initializes a user with the given role and languages.
     *
     * @param userId the UUID of the user to initialize
     * @param role   the role to assign to the user
     * @param langs  the set of languages to associate with the user
     * @throws UserNotFoundException if the user with the given ID does not exist
     */
    @Override
    @Transactional(readOnly = false)
    public User initializeUser(UUID userId, UserRole role, Set<Locale> langs) throws UserNotFoundException {
        if (callerContextProvider.isSystem()) {
            log.error("System context is not authorized to initialize user data");
            throw new NoAccessToOperateException("System context is not authorized to initialize user data");
        }

        User caller = fetchUserFromContext();
        User user = getUserById(userId);

        // Only user themselves can initialize their data
        if (!caller.getId().equals(userId)) {
            log.error("User with ID {} is not authorized to initialize data of user ID {}", caller.getId(), userId);
            throw new NoAccessToOperateException("User is not authorized to initialize data of user ID " + userId);
        }

        user.initializeUser(role, langs);
        userRepository.save(user);
        log.info("Initialized user ID {} with role {} and languages {}", userId, role, langs);

        return user;
    }
}
