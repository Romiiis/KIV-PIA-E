package com.romiiis.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * User domain object
 * Represents a user of the system, either a customer or a translator
 *
 */
@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class User {

    private UUID id;
    private String name;
    private String emailAddress;
    private UserRole role;
    @Setter
    private Set<Locale> languages;
    private Instant createdAt;

    private String hashedPassword;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    // constructor used when referencing the object in other domain objects where only ID is known
    public User(UUID id) {
        this.id = id;
    }


    // constructor used when referencing the full object
    private User(String name, String emailAddress, UserRole role, Set<Locale> languages) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.emailAddress = emailAddress;
        this.role = role;
        this.languages = languages;
        this.createdAt = Instant.now();
    }


    /**
     * Creates a new customer user.
     * <br><br>
     * REQUIREMENTS:
     * <ol>
     *     <li>name must not be empty</li>
     *     <li>emailAddress must not be empty and it must be a valid email address</li>
     * </ol>
     *
     * @param name         Name of the customer
     * @param emailAddress Email address of the customer
     * @return New customer user
     */
    public static User createCustomer(String name, String emailAddress) {

        validateName(name, emailAddress);

        return new User(name, emailAddress, UserRole.CUSTOMER, Collections.emptySet());
    }

    /**
     * Creates a new translator user.
     * <br><br>
     * REQUIREMENTS:
     * <ol>
     *     <li>name must not be empty</li>
     *     <li>emailAddress must not be empty and it must be a valid email address</li>
     *     <li>there must be at least one language</li>
     * </ol>
     *
     * @param name         Name of the translator
     * @param emailAddress Email address of the translator
     * @param languages    Set of languages the translator can translate to
     * @return New translator user
     */
    public static User createTranslator(String name, String emailAddress, Set<Locale> languages) {

        validateName(name, emailAddress);

        if (languages == null || languages.isEmpty()) {
            log.error("Languages are empty");
            throw new IllegalArgumentException("At least one language must be specified");
        }

        return new User(name, emailAddress, UserRole.TRANSLATOR, languages);
    }

    /**
     * Creates a new administrator user.
     * <br><br>
     * REQUIREMENTS:
     * <ol>
     *     <li>name must not be empty</li>
     *     <li>emailAddress must not be empty and it must be a valid email address</li>
     * </ol>
     *
     * @param name         Name of the administrator
     * @param emailAddress Email address of the administrator
     * @return New administrator user
     */
    public static User createAdmin(String name, String emailAddress) {
        validateName(name, emailAddress);

        return new User(name, emailAddress, UserRole.ADMINISTRATOR, Collections.emptySet());
    }

    private static void validateName(String name, String emailAddress) {
        if (name == null || name.isBlank()) {
            log.error("Name is empty");
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (emailAddress == null || emailAddress.isBlank() || !VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress).matches()) {
            log.error("Email address is empty or invalid");
            throw new IllegalArgumentException("Email address cannot be empty or invalid");
        }
    }


    /**
     * Sets the hashed password for the user.
     *
     * @param hashedPassword Hashed password
     * @return User with the hashed password set
     */
    public User withHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
        return this;
    }


}
