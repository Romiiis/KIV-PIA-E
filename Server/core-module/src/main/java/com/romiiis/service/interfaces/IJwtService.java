package com.romiiis.service.interfaces;

import com.romiiis.domain.UserRole;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for handling JWT (JSON Web Token) operations.
 *
 * <p>Provides generation, validation, parsing, and revocation
 * of access and refresh tokens in a secure and extensible way.</p>
 *
 * <p>Each token can contain exactly one user role.</p>
 *
 * @author Roman Pejs
 */
public interface IJwtService {

    /**
     * Generates a JWT access token for the given user ID and role.
     *
     * @param userId the user ID
     * @return signed JWT string
     */
    String generateToken(UUID userId);


    /**
     * Generates a long-lived refresh token for the given subject.
     *
     * @param subject the subject (user identifier)
     * @return signed JWT refresh token string
     */
    String generateRefreshToken(UUID subject);

    /**
     * Validates a token (signature, structure, expiration, etc.).
     *
     * @param token the token to validate
     * @return true if the token is cryptographically valid and not expired
     */
    boolean validateToken(String token);

    /**
     * Extracts the subject (user identifier) from a valid token.
     *
     * @param token the JWT token
     * @return subject (user ID, email, etc.)
     */
    String getSubjectFromToken(String token);


    long getRemainingLifetime(String token);

    /**
     * Extracts the single user role stored in the token.
     *
     * @param token JWT token
     * @return user role (e.g. "ADMIN"), or empty if not present
     */
    Optional<UserRole> getRoleFromToken(String token);


    /**
     * Invalidates a token by adding it to the server-side blacklist.
     *
     * @param token the JWT token to invalidate
     */
    void invalidateToken(String token);

    /**
     * Checks whether the given token has been invalidated.
     *
     * @param jti the unique token identifier
     * @return true if token has been invalidated (blacklisted)
     */
    boolean isTokenInvalidated(String jti);


    /**
     * Gets the token expiration time in milliseconds.
     *
     * @return token expiration time in milliseconds
     */
    Instant getTokenExpirationMs(String token);


    /**
     * Checks if the given token is a refresh token.
     *
     * @param token the JWT token
     * @return true if the token is a refresh token, false otherwise
     */
    boolean isRefreshToken(String token);



}
