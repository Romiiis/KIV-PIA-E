package com.romiiis.security;

import com.romiiis.domain.User;
import com.romiiis.port.IJwtService;
import com.romiiis.service.api.IAuthService;
import com.romiiis.util.AuthCookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final IAuthService authService;

    private final IJwtService jwtService;

    private final AuthCookieUtil cookiesUtil;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // Fallback pro GitHub, který 'name' někdy nemá, ale má 'login'
        if (name == null) {
            name = oauth2User.getAttribute("login");
        }

        if (email == null) {
            throw new ServletException("Email od OAuth providera nebyl nalezen.");
        }

        User user = authService.findOrCreateUserAfterOauth(email, name);

        var tokens = jwtService.generateTokenPair(user.getId());

        cookiesUtil.setCookies(response, tokens);
    }
}