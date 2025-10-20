package com.romiiis.mapper;

import com.romiiis.domain.User;
import com.romiiis.model.UserDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for converting between User domain objects and UserDTOs.
 *
 * @author Roman Pejs
 */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface UserMapper {

    /**
     * Maps a domain User object to a UserDTO.
     *
     * @param domain the User domain object to be mapped
     * @return the mapped User domain object
     */
    UserDTO mapDomainToDTO(User domain);


    /**
     * Maps a list of domain User objects to a list of UserDTOs.
     *
     * @param users the list of User domain objects to be mapped
     * @return the list of mapped UserDTOs
     */
    List<UserDTO> mapDomainListToDTO(List<User> users);
}
