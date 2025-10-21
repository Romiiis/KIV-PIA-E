package com.romiiis.service;

import com.romiiis.configuration.JwtProperties;
import com.romiiis.service.interfaces.IJwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
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

    /**
     * Blacklist of invalidated tokens (jti -> expiration time)
     */
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public DefaultJwtServiceImpl(JwtProperties props) {
        this.props = props;
        this.secretKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates an access token for the given user ID and role.
     *
     * @param userId user identifier (UUID)
     * @param role   single role (e.g. "ADMIN", "CUSTOMER", "TRANSLATOR")
     * @return signed JWT access token
     */
    public String generateToken(UUID userId, String role) {
        String jti = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusMillis(props.getAccessExpirationMs());
        return generateAccessToken(userId.toString(), jti, expiresAt, role);
    }

    /**
     * Backward-compatible method (no role).
     */
    @Override
    public String generateToken(UUID userId) {
        return generateToken(userId, null);
    }

    /**
     * Generates an access token with the specified parameters.
     *
     * @param subject   Subject of the token (user ID)
     * @param jti       Unique token identifier
     * @param expiresAt Expiration time
     * @param role      single user role (nullable)
     */
    private String generateAccessToken(String subject, String jti, Instant expiresAt, String role) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setId(jti)
                .setIssuer(props.getIssuer())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiresAt))
                .claim("type", "access");

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }

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

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer(props.getIssuer())
                .build()
                .parseClaimsJws(token);
    }

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
    public Optional<String> getRoleFromToken(String token) {
        try {
            Claims claims = parse(token).getBody();
            return Optional.ofNullable(claims.get("role", String.class));
        } catch (Exception e) {
            log.warn("Cannot extract role from token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getJti(String token) {
        try {
            return Optional.ofNullable(parse(token).getBody().getId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Instant getExpiration(String token) {
        return parse(token).getBody().getExpiration().toInstant();
    }

    @Override
    public boolean isExpired(String token) {
        return getExpiration(token).isBefore(Instant.now());
    }

    @Override
    public void invalidateToken(String token) {
        getJti(token).ifPresent(jti -> {
            Instant exp = getExpiration(token);
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

    @Override
    public long getRemainingLifetime(String token) {
        try {
            return Math.max(0, getExpiration(token).toEpochMilli() - Instant.now().toEpochMilli());
        } catch (Exception e) {
            return 0;
        }
    }
}
