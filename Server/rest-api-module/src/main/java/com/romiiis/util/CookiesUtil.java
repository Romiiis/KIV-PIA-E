package com.romiiis.util;

import com.romiiis.security.TokenPair;
import com.romiiis.service.interfaces.IJwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CookiesUtil {

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
        ResponseCookie accessCookie = ResponseCookie.from(accessTokenCookieName, tokens.accessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtService.getRemainingLifetime(tokens.accessToken()) / 1000)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(refreshTokenCookieName, tokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtService.getRemainingLifetime(tokens.refreshToken()) / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    public void clearCookies(HttpServletResponse response) {
        ResponseCookie clearAccess = ResponseCookie.from(accessTokenCookieName, "").path("/").maxAge(0).build();
        ResponseCookie clearRefresh = ResponseCookie.from(refreshTokenCookieName, "").path("/").maxAge(0).build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
    }

    public String extractAccessToken(HttpServletRequest request) {
        return extractCookie(request, accessTokenCookieName);
    }

    public String extractRefreshToken(HttpServletRequest request) {
        return extractCookie(request, refreshTokenCookieName);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
