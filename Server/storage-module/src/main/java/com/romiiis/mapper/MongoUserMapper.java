package com.romiiis.mapper;

import com.romiiis.domain.User;
import com.romiiis.model.UserDB;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MongoUserMapper {
    /**
     * Maps a UserDB object to a User object.
     *
     * @param userDB the userDB object to be mapped
     * @return the mapped User object
     */
    User mapDBToDomain(UserDB userDB);

    /**
     * Maps a User object to a UserDB object.
     *
     * @param user the user object to be mapped
     * @return the mapped UserDB object
     */
    UserDB mapDomainToDB(User user);
}
