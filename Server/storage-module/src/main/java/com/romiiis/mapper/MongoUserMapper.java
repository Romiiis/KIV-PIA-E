package com.romiiis.mapper;

import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.model.UserDB;
import com.romiiis.model.UserRoleDB;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between User and UserDB objects.
 * Uses MapStruct for automatic mapping.
 */
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

    /**
     * Maps a list of UserDB objects to a list of User objects.
     *
     * @param userDBs the list of UserDB objects to be mapped
     * @return the mapped list of User objects
     */
    List<User> mapDBListToDomain(List<UserDB> userDBs);

    /**
     * Maps a UserRoleDB object to a UserRole object.
     *
     * @param userRoleDB the UserRoleDB object to be mapped
     * @return the mapped UserRole object
     */
    UserRole mapDBToDomain(UserRoleDB userRoleDB);


    /**
     * Maps a UserRole object to a UserRoleDB object.
     *
     * @param userRole the UserRole object to be mapped
     * @return the mapped UserRoleDB object
     */
    UserRoleDB mapDomainToDB(UserRole userRole);




}
