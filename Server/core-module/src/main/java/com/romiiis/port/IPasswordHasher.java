package com.romiiis.port;

/**
 * Service interface for hashing passwords.
 *
 * @author Roman Pejs
 */
public interface IPasswordHasher {
    /**
     * Hashes a raw password.
     *
     * @param rawPassword the raw password to be hashed
     * @return the hashed password as a String
     */
    String hash(String rawPassword);

    /**
     * Verifies a raw password against a hashed password.
     *
     * @param rawPassword    the raw password to verify
     * @param hashedPassword the hashed password to compare against
     * @return true if the passwords match, false otherwise
     */
    boolean verify(String rawPassword, String hashedPassword);
}
