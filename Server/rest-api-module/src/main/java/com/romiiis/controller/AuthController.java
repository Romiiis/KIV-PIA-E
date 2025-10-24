package com.romiiis.controller;


import com.romiiis.mapper.CommonMapper;
import com.romiiis.model.*;
import com.romiiis.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

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
    private final CommonMapper commonMapper;

    /**
     * Handles user login requests.
     *
     * @param loginUserRequestDTO the login request data transfer object containing user credentials
     * @return a ResponseEntity containing the login response with a JWT token if successful
     */
    @Override
    public ResponseEntity<AuthJWTResponseDTO> loginUser(LoginUserRequestDTO loginUserRequestDTO) {

        String token = authService.login(
                loginUserRequestDTO.getEmailAddress(),
                loginUserRequestDTO.getPassword()
        );

        var responseDTO = new AuthJWTResponseDTO().accessToken(token);
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
        String token;

        if (registerUserRequestDTO instanceof RegisterCustomerRequestDTO data) {
            token = authService.registerCustomer(
                    data.getName(),
                    data.getEmailAddress(),
                    data.getPassword()
            );

        } else if (registerUserRequestDTO instanceof RegisterTranslatorRequestDTO data) {
            token = authService.registerTranslator(
                    data.getName(),
                    data.getEmailAddress(),
                    commonMapper.mapListStringToSetLocale(data.getLanguages()),
                    data.getPassword()
            );

        } else {
            log.warn("Unknown registration type: {}", registerUserRequestDTO.getClass().getName());
            throw new IllegalArgumentException("Unknown registration type: " + registerUserRequestDTO.getClass().getSimpleName());
        }

        var responseDTO = new AuthJWTResponseDTO().accessToken(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Override
    public ResponseEntity<AuthJWTResponseDTO> refreshToken() {
        //TODO implement refresh token logic
        return AuthApi.super.refreshToken();
    }

    @Override
    public ResponseEntity<Void> logoutUser() {
        //TODO implement logout logic
        return AuthApi.super.logoutUser();
    }


}
