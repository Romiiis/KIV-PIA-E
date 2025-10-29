package com.romiiis.service;

import com.romiiis.configuration.JwtProperties;
import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.security.CallerContextProvider;
import com.romiiis.service.interfaces.IJwtService;
import com.romiiis.service.interfaces.IUserService;
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
 * Supports a single user role per JWT token.
 *
 * @author Roman Pejs
 */
@Slf4j
public class DefaultJwtServiceImpl implements IJwtService {

    private final SecretKey secretKey;
    private final JwtProperties props;
    private final IUserService userService;
    private final CallerContextProvider callerContextProvider;

    /**
     * Blacklist of invalidated tokens (jti -> expiration time)
     */
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    /**
     * Constructor
     *
     * @param props JWT properties
     */
    public DefaultJwtServiceImpl(JwtProperties props, IUserService userService, CallerContextProvider callerContextProvider) {
        this.callerContextProvider = callerContextProvider;
        this.userService = userService;
        this.props = props;
        this.secretKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an access token for the given user ID and role.
     *
     * @param userId user identifier (UUID)
     * @return signed JWT access token
     */
    public String generateToken(UUID userId) {
        String jti = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(props.getAccessExpirationMs());
        return generateAccessToken(userId.toString(), jti, expiresAt);
    }


    /**
     * Generates an access token with the specified parameters.
     *
     * @param subject   Subject of the token (user ID)
     * @param jti       Unique token identifier
     * @param expiresAt Expiration time
     */
    private String generateAccessToken(String subject, String jti, Instant expiresAt) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setId(jti)
                .setIssuer(props.getIssuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiresAt))
                .claim("type", "access");


        return builder.signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }


    /**
     * Generates a refresh token for the given user ID and role.
     *
     * @param subject user identifier (UUID)
     * @return signed JWT refresh token
     */
    @Override
    public String generateRefreshToken(UUID subject) {
        String jti = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(props.getRefreshExpirationMs());
        return generateRefreshToken(subject.toString(), jti, expiresAt);
    }

    /**
     * Generates a refresh token with the specified parameters.
     *
     * @param subject   Subject of the token (user ID)
     * @param jti       Unique token identifier
     * @param expiresAt Expiration time
     */
    private String generateRefreshToken(String subject, String jti, Instant expiresAt) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setId(jti)
                .setIssuer(props.getIssuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiresAt))
                .claim("type", "refresh");


        return builder.signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }


    /**
     * Validates the given JWT token.
     *
     * @param token JWT token
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
     * @param token JWT token
     * @return Jws<Claims> object containing the token claims
     */
    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer(props.getIssuer())
                .build()
                .parseClaimsJws(token);
    }


    /**
     * Extracts the subject (user ID) from a JWT token.
     *
     * @param token JWT token
     * @return the subject (user ID)
     */
    @Override
    public String getSubjectFromToken(String token) {
        return parse(token).getBody().getSubject();
    }

    /**
     * Extracts the user role from a JWT token.
     *
     * @param token JWT token
     * @return the role (e.g. "ADMIN"), or empty if missing
     */
    public Optional<UserRole> getRoleFromToken(String token) {
        String userId = getSubjectFromToken(token);
        User user = callerContextProvider.runAsSystem(() -> userService.getUserById(UUID.fromString(userId)));
        return Optional.ofNullable(user.getRole());
    }


    /**
     * Extracts the JWT ID (jti) from a JWT token.
     *
     * @param token JWT token
     * @return Optional containing the jti if present, otherwise empty
     */
    private Optional<String> getJti(String token) {
        try {
            return Optional.ofNullable(parse(token).getBody().getId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Extracts the expiration time from a JWT token.
     *
     * @param token JWT token
     * @return expiration time as Instant
     */
    public Instant getTokenExpirationMs(String token) {
        return parse(token).getBody().getExpiration().toInstant();
    }

    /**
     * Extracts the expiration time from a JWT token.
     *
     * @param token JWT token
     * @return expiration time as Instant
     */
    public boolean isExpired(String token) {
        return getTokenExpirationMs(token).isBefore(Instant.now());
    }


    /**
     * Invalidates the given JWT token by adding its jti to the blacklist.
     *
     * @param token the JWT token to invalidate
     */
    @Override
    public void invalidateToken(String token) {
        getJti(token).ifPresent(jti -> {
            Instant exp = getTokenExpirationMs(token);
            blacklist.put(jti, exp);
            log.info("Token invalidated: jti={}, exp={}", jti, exp);
        });
    }

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
     * Parses the claims from the given JWT token.
     *
     * @param token JWT token
     * @return Optional containing the Claims if parsing is successful, otherwise empty
     */
    @Override
    public long getRemainingLifetime(String token) {
        try {
            return Math.max(0, getTokenExpirationMs(token).toEpochMilli() - Instant.now().toEpochMilli());
        } catch (Exception e) {
            return 0;
        }
    }


    @Override
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parse(token).getBody();
            String type = claims.get("type", String.class);
            return "refresh".equals(type);
        } catch (Exception e) {
            log.warn("Cannot determine token type: {}", e.getMessage());
            return false;
        }
    }
}
