package com.romiiis.controller;

import com.romiiis.model.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MeController implements MeApi {

    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @return A ResponseEntity containing the UserDTO of the current user.
     */
    @Override
    public ResponseEntity<UserDTO> getCurrentUser() {
        // TODO: implement logic of retrieving current user details from security context
        return MeApi.super.getCurrentUser();
    }


}
