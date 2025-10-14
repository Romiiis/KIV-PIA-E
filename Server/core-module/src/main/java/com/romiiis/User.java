package com.romiiis;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
public class User {
    private UUID id;
    private String name;
    private String emailAddress;
    private UserRole role;
    private Set<Locale> languages;
    private Instant createdAt;

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
     * @param name Name of the customer
     * @param emailAddress Email address of the customer
     * @return New customer user
     */
    public static User createCustomer(String name, String emailAddress) {
        // TODO: check that name is not empty
        // TODO: check that emailAddress is not empty and it is a valid email address

        if (name == null || name.isBlank()) {
            log.error("Name is empty");
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (emailAddress == null || emailAddress.isBlank() || !VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress).matches()) {
            log.error("Email address is empty or invalid");
            throw new IllegalArgumentException("Email address cannot be empty or invalid");
        }

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
     * @param name Name of the translator
     * @param emailAddress Email address of the translator
     * @param languages Set of languages the translator can translate to
     * @return New translator user
     */
    public static User createTranslator(String name, String emailAddress, Set<Locale> languages) {
        // TODO: check that name is not empty
        // TODO: check that emailAddress is not empty and it is a valid email address
        // TODO: check that there is at least one language

        if (name == null || name.isBlank()) {
            log.error("Name is empty");
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (emailAddress == null || emailAddress.isBlank() || !VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress).matches()) {
            log.error("Email address is empty or invalid");
            throw new IllegalArgumentException("Email address cannot be empty or invalid");
        }

        if (languages == null || languages.isEmpty()) {
            log.error("Languages are empty");
            throw new IllegalArgumentException("At least one language must be specified");
        }

        return new User(name, emailAddress, UserRole.TRANSLATOR, languages);
    }

    /**
     * Creates a new project.
     * <br><br>
     * REQUIREMENTS:
     * <ol>
     *     <li>this user must have the CUSTOMER role</li>
     *     <li>sourceFile must not be empty and it must not be too big</li>
     * </ol>
     *
     * @param targetLanguage Language to translate to
     * @param sourceFile File to be translated
     * @return New project
     */
    public Project createProject(Locale targetLanguage, byte[] sourceFile) {
        // TODO: check that this user is a CUSTOMER
        // TODO: check that sourceFile is not empty but also not too big

        if (this.getRole() != UserRole.CUSTOMER) {
            log.error("User is not a customer");
            throw new IllegalArgumentException("Only customers can create projects");
        }

        if (sourceFile == null || sourceFile.length == 0 || sourceFile.length > Project.MAX_FILE_SIZE) {
            log.error("Source file is empty");
            throw new IllegalArgumentException("Source file cannot be empty");
        }

        return new Project(this, targetLanguage, sourceFile);
    }

    //<editor-fold desc="getters" defaultstate="collapsed">
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public UserRole getRole() {
        return role;
    }

    public Set<Locale> getLanguages() {
        return languages;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    //</editor-fold>
}
