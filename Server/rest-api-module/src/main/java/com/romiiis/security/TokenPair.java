package com.romiiis.security;

import java.time.Instant;

public record TokenPair(String accessToken, String refreshToken) {}
