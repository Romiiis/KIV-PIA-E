package com.romiiis.controller;

import com.romiiis.mapper.UserMapper;
import com.romiiis.model.UserDTO;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MeController implements MeApi {

    /**
     * Services
     */
    private final IUserService userService;
    private final UserMapper userMapper;

    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @return A ResponseEntity containing the UserDTO of the current user.
     */
    @Override
    public ResponseEntity<UserDTO> getCurrentUser() {

        String userIdString = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UUID uuid = UUID.fromString(userIdString);

        return ResponseEntity.ok(userMapper.mapDomainToDTO(userService.getUserById(uuid)));
    }


}
