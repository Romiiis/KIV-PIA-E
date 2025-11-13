package com.romiiis.infrastructure.security;

import com.romiiis.port.IPasswordHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Default implementation of IPasswordHasher using Spring Security's PasswordEncoder.
 */
@Slf4j
@RequiredArgsConstructor
public class PasswordHasherImpl implements IPasswordHasher {

    /**
     * Password encoder for hashing passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Hashes the given password.
     *
     * @param password The password to hash.
     * @return The hashed password.
     */
    @Override
    public String hash(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verify(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
