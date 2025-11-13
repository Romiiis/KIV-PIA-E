package com.romiiis.util;

import com.romiiis.security.TokenPair;
import com.romiiis.port.IJwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Utility class for managing HTTP cookies related to authentication tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthCookieUtil {

    private final IJwtService jwtService;

    @Value("${accesstoken.name}")
    private String accessTokenCookieName;

    @Value("${refreshtoken.name}")
    private String refreshTokenCookieName;

    /**
     * Sets the access and refresh tokens as HTTP-only cookies in the response.
     *
     * @param response HttpServletResponse to add cookies to
     * @param tokens   TokenPair containing access and refresh tokens
     */
    public void setCookies(HttpServletResponse response, TokenPair tokens) {

        long accessLifetime = jwtService.getRemainingLifetime(tokens.accessToken());
        long refreshLifetime = jwtService.getRemainingLifetime(tokens.refreshToken());

        long accessCookieMaxAge = (accessLifetime * 2) / 1000;
        long refreshCookieMaxAge = (refreshLifetime + accessLifetime) / 1000;

        log.debug("accessLifetime: {}, refreshLifetime: {}", accessLifetime, refreshLifetime);
        log.debug("accessCookieMaxAge: {}, refreshCookieMaxAge: {}", accessCookieMaxAge, refreshCookieMaxAge);

        ResponseCookie accessCookie = ResponseCookie.from(accessTokenCookieName, tokens.accessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(accessCookieMaxAge)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(refreshTokenCookieName, tokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(refreshCookieMaxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }


    /**
     * Clears the access and refresh token cookies from the response.
     *
     * @param response HttpServletResponse to clear cookies from
     */
    public void clearCookies(HttpServletResponse response) {
        ResponseCookie clearAccess = ResponseCookie.from(accessTokenCookieName, "").path("/").maxAge(0).build();
        ResponseCookie clearRefresh = ResponseCookie.from(refreshTokenCookieName, "").path("/").maxAge(0).build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
    }

    /**
     * Extracts the access token from the request cookies.
     *
     * @param request HttpServletRequest containing the cookies
     * @return the access token value, or null if not found
     */
    public String extractAccessToken(HttpServletRequest request) {
        return extractCookie(request, accessTokenCookieName);
    }

    /**
     * Extracts the refresh token from the request cookies.
     *
     * @param request HttpServletRequest containing the cookies
     * @return the refresh token value, or null if not found
     */
    public String extractRefreshToken(HttpServletRequest request) {
        return extractCookie(request, refreshTokenCookieName);
    }

    /**
     * Helper method to extract a cookie value by name from the request.
     *
     * @param request HttpServletRequest containing the cookies
     * @param name    the name of the cookie to extract
     * @return the cookie value, or null if not found
     */
    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
