package com.romiiis.controller;


import com.romiiis.domain.User;
import com.romiiis.mapper.CommonMapper;
import com.romiiis.model.AuthJWTResponseDTO;
import com.romiiis.model.LoginUserRequestDTO;
import com.romiiis.model.RegisterUserRequestDTO;
import com.romiiis.port.IJwtService;
import com.romiiis.service.api.IAuthService;
import com.romiiis.util.AuthCookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

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
public class AuthController extends AbstractController implements AuthApi {

    /**
     * Services & Mappers
     */
    private final IAuthService authService;
    private final IJwtService jwtService;
    private final AuthCookieUtil cookiesUtil;


    /**
     * Handles user login requests.
     *
     * @param loginUserRequestDTO the login request data transfer object containing user credentials
     * @return a ResponseEntity containing the login response with a JWT token if successful
     */
    @Override
    public ResponseEntity<Void> loginUser(LoginUserRequestDTO loginUserRequestDTO) {

        User user = authService.login(
                loginUserRequestDTO.getEmailAddress(),
                loginUserRequestDTO.getPassword()
        );

        var tokens = jwtService.generateTokenPair(user.getId());

        HttpServletResponse response = getCurrentResponse();
        cookiesUtil.setCookies(response, tokens);


        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> registerUser(RegisterUserRequestDTO registerUserRequestDTO) {
        User user = authService.registerUser(
                registerUserRequestDTO.getName(),
                registerUserRequestDTO.getEmailAddress(),
                registerUserRequestDTO.getPassword()
        );

        var tokens = jwtService.generateTokenPair(user.getId());

        HttpServletResponse response = getCurrentResponse();
        cookiesUtil.setCookies(response, tokens);


        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Handles token refresh requests.
     *
     * @return a ResponseEntity containing the new JWT tokens if successful,
     * or an unauthorized status if the refresh token is invalid
     */
    @Override
    public ResponseEntity<Void> refreshToken() {
        HttpServletRequest request = getCurrentRequest();
        HttpServletResponse response = getCurrentResponse();

        String refreshToken = cookiesUtil.extractRefreshToken(request);
        if (refreshToken == null || !jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(jwtService.getSubjectFromToken(refreshToken));

        var tokens = jwtService.generateTokenPair(userId);

        cookiesUtil.setCookies(response, tokens);


        return ResponseEntity.ok().build();
    }


    /**
     * Handles user logout requests.
     *
     * @return a ResponseEntity with no content status
     */
    @Override
    public ResponseEntity<Void> logoutUser() {

        HttpServletRequest request = getCurrentRequest();
        HttpServletResponse response = getCurrentResponse();

        String accessToken = cookiesUtil.extractAccessToken(request);
        String refreshToken = cookiesUtil.extractRefreshToken(request);

        if (accessToken != null) jwtService.invalidateToken(accessToken);
        if (refreshToken != null) jwtService.invalidateToken(refreshToken);

        cookiesUtil.clearCookies(response);

        return ResponseEntity.noContent().build();
    }


}
