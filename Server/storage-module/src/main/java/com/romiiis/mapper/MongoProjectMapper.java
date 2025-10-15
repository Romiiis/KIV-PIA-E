package com.romiiis.mapper;

import com.romiiis.domain.Project;
import com.romiiis.model.ProjectDB;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between Project and ProjectDB objects.
 * Uses MongoUserMapper for nested user mappings.
 */
@Mapper(componentModel = "spring", uses = {MongoUserMapper.class})
public interface MongoProjectMapper {

    /**
     * Maps a ProjectDB object to a Project object.
     * @param projectDB the projectDB object to be mapped
     * @return the mapped Project object
     */
    Project mapDBToDomain(ProjectDB projectDB);

    /**
     * Maps a Project object to a ProjectDB object.
     * @param project the project object to be mapped
     * @return the mapped ProjectDB object
     */
    ProjectDB mapDomainToDB(Project project);


}
