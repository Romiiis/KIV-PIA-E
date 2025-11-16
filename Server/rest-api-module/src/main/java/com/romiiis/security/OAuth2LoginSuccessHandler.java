package com.romiiis.security;

import com.romiiis.domain.User;
import com.romiiis.exception.LoggedByDifferentMethodException;
import com.romiiis.port.IJwtService;
import com.romiiis.service.api.IAuthService;
import com.romiiis.util.AuthCookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final IAuthService authService;
    private final IJwtService jwtService;
    private final AuthCookieUtil cookiesUtil;

    @Value("${app.frontend.default-redirect-url}")
    private String defaultTargetUrl;

    @Value("${app.frontend.auth-error-url}")
    private String authErrorUrl;

    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String ERROR_PARAM = "error";

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    public OAuth2LoginSuccessHandler(IAuthService authService, IJwtService jwtService, AuthCookieUtil cookiesUtil) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.cookiesUtil = cookiesUtil;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(request, response);

        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");

            if (name == null) {
                name = oauth2User.getAttribute("login");
            }

            if (email == null) {
                log.error("Email not found from OAuth2 provider for user: {}", name);
                handleFailure(request, response, "email_missing");
                return;
            }

            User user = authService.findOrCreateUserAfterOauth(email, name);
            var tokens = jwtService.generateTokenPair(user.getId());
            cookiesUtil.setCookies(response, tokens);

            redirectStrategy.sendRedirect(request, response, targetUrl);
        } catch (LoggedByDifferentMethodException e) {
            log.error("OAuth2 login failed due to different authentication method", e);
            handleFailure(request, response, "different_auth_method");

        } catch (Exception e) {
            log.error("OAuth2 authentication failed", e);
            handleFailure(request, response, "internal_error");
        }
    }

    /**
     * Stanoví cílovou URL podle priorit: SavedRequest > Query Param > Default.
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            requestCache.removeRequest(request, response);
            return savedRequest.getRedirectUrl();
        }

        String redirectUri = request.getParameter(REDIRECT_URI_PARAM);
        if (redirectUri != null) {
            return redirectUri;
        }

        return defaultTargetUrl;
    }

    /**
     * Zpracuje chybu přesměrováním na autentizační stránku frontendu s chybovým parametrem.
     */
    protected void handleFailure(HttpServletRequest request, HttpServletResponse response, String errorCode)
            throws IOException {

        String failureUrl = authErrorUrl + "?" + ERROR_PARAM + "=" + errorCode;

        String originalRedirectUri = request.getParameter(REDIRECT_URI_PARAM);
        if (originalRedirectUri != null) {
            failureUrl += "&" + REDIRECT_URI_PARAM + "=" + originalRedirectUri;
        }

        redirectStrategy.sendRedirect(request, response, failureUrl);
    }
}