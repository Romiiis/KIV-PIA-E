package com.romiiis.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
