package com.romiiis.controller;

import com.romiiis.exception.BaseException;
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
    public ResponseEntity<LoginUser200ResponseDTO> loginUser(LoginUserRequestDTO loginUserRequestDTO) {
        String token;
        try {

            // authenticate user and generate token
            token = authService.login(loginUserRequestDTO.getEmailAddress(), loginUserRequestDTO.getPassword());
        } catch (BaseException e) {

            // handle known exceptions
            return new ResponseEntity<>(HttpStatus.valueOf(e.getHttpStatus().getCode()));
        } catch (Exception e) {
            // handle unexpected exceptions
            log.error("Error during user login (500): {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // return the generated token in the response
        var responseDTO = new LoginUser200ResponseDTO().token(token);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
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
    public ResponseEntity<LoginUser200ResponseDTO> registerUser(RegisterUserRequestDTO registerUserRequestDTO) {

        // variable to hold the generated token
        String token;
        try {

            // determine the type of registration and call the appropriate service method
            if (registerUserRequestDTO instanceof RegisterCustomerRequestDTO data) {

                // register customer
                token = authService.registerCustomer(data.getName(), data.getEmailAddress(), data.getPassword());

            } else if (registerUserRequestDTO instanceof RegisterTranslatorRequestDTO data) {

                // register translator
                token = authService.registerTranslator(data.getName(), data.getEmailAddress(), commonMapper.mapListLanguages(data.getLanguages()), data.getPassword());

            } else {

                // unknown type
                log.error("Unknown registration type (400): {}", registerUserRequestDTO.getClass().getName());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch(BaseException e) {
            // handle known exceptions
            return new ResponseEntity<>(HttpStatus.valueOf(e.getHttpStatus().getCode()));
        }
        catch (Exception e) {
            // handle unexpected exceptions
            log.error("Error during user registration (500): {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // return the generated token in the response
        var responseDTO = new LoginUser200ResponseDTO().token(token);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
