package com.romiiis.service;

import com.romiiis.configuration.JwtProperties;
import com.romiiis.service.interfaces.IJwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the IJwtService interface.
 * JWT (JSON Web Token) service for generating, validating, and managing JWT tokens.
 * JTI (JWT ID) blacklist is maintained to invalidate tokens when needed.
 * @author Roman Pejs
 */
@Slf4j
public class DefaultJwtServiceImpl implements IJwtService {

    private final SecretKey secretKey;
    private final JwtProperties props;

    /**
     * Blacklist of invalidated tokens (jti -> expiration time)
     */
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();


    /**
     * Constructor initializing the JWT service with the given properties.
     * @param props JWT configuration properties
     */
    public DefaultJwtServiceImpl(JwtProperties props) {
        this.props = props;
        this.secretKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an access token for the given user ID.
     *
     * @param userId the user ID
     * @return the generated JWT access token
     */
    @Override
    public String generateToken(UUID userId) {

        String jti = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(props.getAccessExpirationMs());

        return generateAccessToken(userId.toString(), jti, expiresAt);
    }


    /**
     * Generates an access token with the specified parameters.
     *
     * @param subject   Subject of the token (e.g., user ID)
     * @param jti       Unique token identifier
     * @param expiresAt Expiration time of the token
     * @return the generated JWT access token
     */
    private String generateAccessToken(String subject, String jti, Instant expiresAt) {
        return Jwts.builder()
                .setSubject(subject)
                .setId(jti)
                .setIssuer(props.getIssuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiresAt))
                .claim("type", "access")
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a refresh token with the specified parameters.
     *
     * @param subject   Subject of the token (e.g., user ID)
     * @param jti       Unique token identifier
     * @param parentJti JTI of the parent access token (can be null)
     * @param expiresAt Expiration time of the token
     * @return the generated JWT refresh token
     */
    @Override
    public String generateRefreshToken(String subject, String jti, String parentJti, Instant expiresAt) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setId(jti)
                .setIssuer(props.getIssuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiresAt))
                .claim("type", "refresh");

        if (parentJti != null)
            builder.claim("parent", parentJti);

        return builder.signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }

    /**
     * Validates the given JWT token.
     *
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = parse(token);
            String jti = claims.getBody().getId();

            if (jti != null && isTokenInvalidated(jti)) {
                log.warn("Token invalidated: jti={}", jti);
                return false;
            }
            return claims.getBody().getExpiration().toInstant().isAfter(Instant.now());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Parses the JWT token and returns the claims.
     *
     * @param token the token to parse
     * @return the parsed JWS claims
     */
    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer(props.getIssuer())
                .build()
                .parseClaimsJws(token);
    }


    /**
     * Retrieves the subject from the JWT token.
     *
     * @param token the JWT token
     * @return the subject of the token
     */
    @Override
    public String getSubjectFromToken(String token) {
        return parse(token).getBody().getSubject();
    }

    /**
     * Retrieves the JTI (JWT ID) from the JWT token.
     *
     * @param token the JWT token
     * @return an Optional containing the JTI if present, otherwise an empty Optional
     */
    @Override
    public Optional<String> getJti(String token) {
        try {
            return Optional.ofNullable(parse(token).getBody().getId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves the expiration time from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration time as an Instant
     */
    @Override
    public Instant getExpiration(String token) {
        return parse(token).getBody().getExpiration().toInstant();
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    @Override
    public boolean isExpired(String token) {
        return getExpiration(token).isBefore(Instant.now());
    }


    /**
     * Invalidates the given JWT token by adding its JTI to the blacklist.
     *
     * @param token the JWT token to invalidate
     */
    @Override
    public void invalidateToken(String token) {
        getJti(token).ifPresent(jti -> {
            Instant exp = getExpiration(token);
            blacklist.put(jti, exp);
            log.info("Token invalidated: jti={}, exp={}", jti, exp);
        });
    }

    /**
     * Checks if the token with the given JTI is invalidated.
     *
     * @param jti the JTI of the token
     * @return true if the token is invalidated, false otherwise
     */
    @Override
    public boolean isTokenInvalidated(String jti) {
        Instant exp = blacklist.get(jti);
        if (exp == null) return false;

        if (exp.isBefore(Instant.now())) {
            blacklist.remove(jti);
            return false;
        }

        return true;
    }

    /**
     * Retrieves the remaining lifetime of the JWT token in milliseconds.
     *
     * @param token the JWT token
     * @return the remaining lifetime in milliseconds, or 0 if the token is invalid
     */
    @Override
    public long getRemainingLifetime(String token) {
        try {
            return Math.max(0, getExpiration(token).toEpochMilli() - Instant.now().toEpochMilli());
        } catch (Exception e) {
            return 0;
        }
    }
}
