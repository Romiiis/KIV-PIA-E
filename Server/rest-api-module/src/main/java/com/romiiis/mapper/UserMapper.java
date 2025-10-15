package com.romiiis.mapper;

import com.romiiis.domain.User;
import com.romiiis.model.UserDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between User domain objects and UserDTOs.
 *
 * @author Roman Pejs
 */
@Mapper(componentModel = "spring", uses=CommonMapper.class)
public interface UserMapper {

    /**
     * Maps a domain User object to a UserDTO.
     * @param domain the User domain object to be mapped
     * @return the mapped User domain object
     */
    UserDTO mapDomainToDTO(User domain);


}
