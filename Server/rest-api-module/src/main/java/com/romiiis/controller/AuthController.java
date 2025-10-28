package com.romiiis.controller;


import com.romiiis.domain.User;
import com.romiiis.mapper.CommonMapper;
import com.romiiis.model.AuthJWTResponseDTO;
import com.romiiis.model.LoginUserRequestDTO;
import com.romiiis.model.RegisterUserRequestDTO;
import com.romiiis.security.TokenPair;
import com.romiiis.service.interfaces.IAuthService;
import com.romiiis.service.interfaces.IJwtService;
import com.romiiis.util.CookiesUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.UUID;

/**
 * Controller for authentication-related operations such as user login and registration.
 * <p>
 * This controller implements the AuthApi interface to handle HTTP requests related to authentication.
 * It uses the IAuthService to perform business logic and the CommonMapper for data mapping.
 *
 * @author Roman Pejs
 * @see AuthApi
 * @see IAuthService
 * @see CommonMapper
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    /**
     * Services & Mappers
     */
    private final IAuthService authService;
    private final IJwtService jwtService;
    private final CookiesUtil cookiesUtil;


    /**
     * Handles user login requests.
     *
     * @param loginUserRequestDTO the login request data transfer object containing user credentials
     * @return a ResponseEntity containing the login response with a JWT token if successful
     */
    @Override
    public ResponseEntity<AuthJWTResponseDTO> loginUser(LoginUserRequestDTO loginUserRequestDTO) {

        User user = authService.login(
                loginUserRequestDTO.getEmailAddress(),
                loginUserRequestDTO.getPassword()
        );

        var tokens = generateTokenPair(user.getId(), user.getRole().name());

        HttpServletResponse response = getCurrentResponse();
        cookiesUtil.setCookies(response, tokens);


        var responseDTO = new AuthJWTResponseDTO()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken());

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Handles user registration requests.
     * <p>
     * This method supports registration for both customers and translators.
     * It determines the type of registration based on the instance of the provided DTO
     * and calls the appropriate service method to register the user.
     *
     * @param registerUserRequestDTO the registration request data transfer object, which can be
     *                               either RegisterCustomerRequestDTO or RegisterTranslatorRequestDTO
     * @return a ResponseEntity containing the registration response with a JWT token if successful,
     * or an error status if registration fails
     */
    @Override
    public ResponseEntity<AuthJWTResponseDTO> registerUser(RegisterUserRequestDTO registerUserRequestDTO) {
        User user = authService.registerUser(
                registerUserRequestDTO.getName(),
                registerUserRequestDTO.getEmailAddress(),
                registerUserRequestDTO.getPassword()
        );

        var tokens = generateTokenPair(user.getId(), user.getRole().name());

        HttpServletResponse response = getCurrentResponse();
        cookiesUtil.setCookies(response, tokens);

        var responseDTO = new AuthJWTResponseDTO()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken());

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<AuthJWTResponseDTO> refreshToken() {
        HttpServletRequest request = getCurrentRequest();
        HttpServletResponse response = getCurrentResponse();

        String refreshToken = cookiesUtil.extractCookie(request, "refresh_token");
        if (refreshToken == null || !jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(jwtService.getSubjectFromToken(refreshToken));
        String role = jwtService.getRoleFromToken(refreshToken).orElse(null);

        var tokens = generateTokenPair(userId, role);

        cookiesUtil.setCookies(response, tokens);

        var dto = new AuthJWTResponseDTO()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken());

        return ResponseEntity.ok(dto);
    }


    @Override
    public ResponseEntity<Void> logoutUser() {
        HttpServletRequest request = getCurrentRequest();
        HttpServletResponse response = getCurrentResponse();

        String accessToken = cookiesUtil.extractCookie(request, "access_token");
        String refreshToken = cookiesUtil.extractCookie(request, "refresh_token");

        if (accessToken != null) jwtService.invalidateToken(accessToken);
        if (refreshToken != null) jwtService.invalidateToken(refreshToken);

        cookiesUtil.clearCookies(response);

        return ResponseEntity.noContent().build();
    }


    /**
     * Generates a pair of access and refresh tokens for the given user ID and role.
     *
     * @param userId UUID of the user
     * @param role   Role of the user
     * @return TokenPair containing access and refresh tokens
     */
    public TokenPair generateTokenPair(UUID userId, String role) {
        String accessToken = jwtService.generateToken(userId, role);
        String refreshToken = jwtService.generateRefreshToken(userId, role);

        return new TokenPair(accessToken, refreshToken);
    }

    private HttpServletResponse getCurrentResponse() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("No active HTTP request context");
        }
        return attrs.getResponse();
    }

    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }


}
