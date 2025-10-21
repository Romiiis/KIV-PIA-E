package com.romiiis.service.interfaces;

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
     * Generates a JWT access token for the given user ID (no role assigned).
     *
     * @param userId the user ID
     * @return signed JWT string
     */
    String generateToken(UUID userId);

    /**
     * Generates a JWT access token for the given user ID and role.
     *
     * @param userId the user ID
     * @param role   single user role (e.g. "ADMIN", "CUSTOMER", "TRANSLATOR")
     * @return signed JWT string
     */
    String generateToken(UUID userId, String role);

    /**
     * Generates a long-lived refresh token for the given subject.
     *
     * @param subject   the subject (user identifier)
     * @param jti       unique identifier of the refresh token
     * @param parentJti optional ID of the parent refresh token (if rotating)
     * @param expiresAt expiration timestamp (UTC)
     * @return signed JWT refresh token string
     */
    String generateRefreshToken(String subject, String jti, String parentJti, Instant expiresAt);

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

    /**
     * Extracts the unique JWT ID (jti) from the token.
     *
     * @param token the JWT token
     * @return JWT ID (jti) if present, empty otherwise
     */
    Optional<String> getJti(String token);

    /**
     * Extracts the single user role stored in the token.
     *
     * @param token JWT token
     * @return user role (e.g. "ADMIN"), or empty if not present
     */
    Optional<String> getRoleFromToken(String token);

    /**
     * Extracts the expiration time of the token.
     *
     * @param token the JWT token
     * @return expiration timestamp
     */
    Instant getExpiration(String token);

    /**
     * Checks whether a token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired
     */
    boolean isExpired(String token);

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
     * Returns the remaining time-to-live (TTL) of the token in milliseconds.
     *
     * @param token the JWT token
     * @return remaining lifetime in milliseconds
     */
    long getRemainingLifetime(String token);
}
