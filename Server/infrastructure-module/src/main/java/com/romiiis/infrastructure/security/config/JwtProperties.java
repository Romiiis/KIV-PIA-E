package com.romiiis.infrastructure.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for JWT (JSON Web Token) settings.
 * This class maps properties prefixed with "app.jwt" from the application configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {


    // Secret key for signing JWT tokens
    private String secret;

    // Expiration time for access tokens in milliseconds
    private long accessExpirationMs;

    // Expiration time for refresh tokens in milliseconds
    private long refreshExpirationMs;

    // Issuer of the JWT tokens
    private String issuer;
}
