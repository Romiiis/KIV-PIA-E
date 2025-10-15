package com.romiiis.controller;

import com.romiiis.mapper.CommonMapper;
import com.romiiis.model.*;
import com.romiiis.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final IAuthService authService;
    private final CommonMapper commonMapper;

    @Override
    public ResponseEntity<LoginUser200ResponseDTO> loginUser(LoginUserRequestDTO loginUserRequestDTO) {
        return AuthApi.super.loginUser(loginUserRequestDTO);
    }

    @Override
    public ResponseEntity<LoginUser200ResponseDTO> registerUser(RegisterUserRequestDTO registerUserRequestDTO) {
        String token;
        try {
            if (registerUserRequestDTO instanceof RegisterCustomerRequestDTO data) {
                token = authService.registerCustomer(data.getName(), data.getEmailAddress(), data.getPassword());
                return ResponseEntity.ok(new LoginUser200ResponseDTO().token(token));
            }
            else if (registerUserRequestDTO instanceof RegisterTranslatorRequestDTO data) {
                token = authService.registerTranslator(data.getName(), data.getEmailAddress(), commonMapper.mapListLanguages(data.getLanguages()),data.getPassword());
                return ResponseEntity.ok(new LoginUser200ResponseDTO().token(token));
            }
            else {
                log.error("Unknown registration type: {}", registerUserRequestDTO.getClass().getName());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
