package com.romiiis.controller;

import com.romiiis.configuration.UsersFilter;
import com.romiiis.exception.BaseException;
import com.romiiis.mapper.CommonMapper;
import com.romiiis.mapper.UserMapper;
import com.romiiis.model.UserDTO;
import com.romiiis.model.UserRoleDTO;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

/**
 * Controller for handling user-related API requests.
 *
 * @author Roman Pejs
 */
@Controller
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final IUserService userService;
    private final UserMapper userMapper;
    private final CommonMapper commonMapper;

    @Override
    public ResponseEntity<UserDTO> getCurrentUser() {
        return UsersApi.super.getCurrentUser();
    }

    @Override
    public ResponseEntity<UserDTO> getUserDetails(UUID id) {
        try {
            var user = userService.getUserById(id);
            return ResponseEntity.ok(userMapper.mapDomainToDTO(user));

        } catch (BaseException e) {

            return ResponseEntity.status(e.getHttpStatus().getCode()).build();

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<List<UserDTO>> listAllUsers(UserRoleDTO role, String languages) {
        try {
            var filter = new UsersFilter().setRole(commonMapper.mapUserRoleDTOToDomain(role)).setLanguageCode(languages);
            var users = userService.getAllUsers(filter);
            return ResponseEntity.ok(userMapper.mapDomainListToDTO(users));

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<List<String>> listUserLanguages(UUID id) {
        return UsersApi.super.listUserLanguages(id);
    }

    @Override
    public ResponseEntity<List<String>> replaceUserLanguages(UUID id, List<String> requestBody) {
        return UsersApi.super.replaceUserLanguages(id, requestBody);
    }
}
