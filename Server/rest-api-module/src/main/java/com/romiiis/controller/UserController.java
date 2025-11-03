package com.romiiis.controller;

import com.romiiis.filter.UsersFilter;
import com.romiiis.mapper.CommonMapper;
import com.romiiis.mapper.UserMapper;

import com.romiiis.model.InitializeUserRequestDTO;
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


    /**
     * Retrieves user details by their unique identifier.
     *
     * @param id The UUID of the user.
     * @return A ResponseEntity containing the UserDTO.
     */
    @Override
    public ResponseEntity<UserDTO> getUserDetails(UUID id) {

        var user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.mapDomainToDTO(user));
    }

    /**
     * Lists all users with optional filtering by role and languages.
     *
     * @param role      Filter users by their role. (optional)
     * @param languages Comma-separated list of language codes to filter users by. (optional)
     * @return A ResponseEntity containing a list of UserDTOs.
     */
    @Override
    public ResponseEntity<List<UserDTO>> listAllUsers(UserRoleDTO role, String languages) {
        var filter = new UsersFilter().setRole(commonMapper.mapUserRoleDTOToDomain(role)).setLanguageCode(languages);
        var users = userService.getAllUsers(filter);
        return ResponseEntity.ok(userMapper.mapDomainListToDTO(users));
    }



    /**
     * Changes the role and languages of a user.
     *
     * @param id                         The UUID of the user to be updated.
     * @param initializeUserRequestDTO   The request DTO containing the new role and languages.
     * @return A ResponseEntity containing the updated UserDTO.
     */
    @Override
    public ResponseEntity<Void> changeUserRole(UUID id, InitializeUserRequestDTO initializeUserRequestDTO) {
        userService.initializeUser(
                id,
                commonMapper.mapUserRoleDTOToDomain(initializeUserRequestDTO.getRole()),
                commonMapper.mapListStringToSetLocale(initializeUserRequestDTO.getLanguages()));

        return ResponseEntity.ok().build();
    }
}
