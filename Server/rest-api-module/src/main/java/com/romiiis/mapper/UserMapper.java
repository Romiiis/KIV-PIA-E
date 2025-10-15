package com.romiiis.mapper;

import com.romiiis.domain.User;
import com.romiiis.model.UserDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses=CommonMapper.class)
public interface UserMapper {




    UserDTO mapDomainToDTO(User domain);


}
