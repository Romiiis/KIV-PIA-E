package com.romiiis.mapper;

import com.romiiis.domain.Project;
import com.romiiis.domain.WrapperProjectFeedback;
import com.romiiis.model.ProjectDB;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between Project and ProjectDB objects.
 * Uses MongoUserMapper for nested user mappings.
 */
@Mapper(componentModel = "spring", uses = {MongoUserMapper.class, MongoFeedbackMapper.class})
public interface MongoProjectMapper {

    /**
     * Maps a ProjectDB object to a Project object.
     *
     * @param projectDB the projectDB object to be mapped
     * @return the mapped Project object
     */
    Project mapDBToDomain(ProjectDB projectDB);

    /**
     * Maps a Project object to a ProjectDB object.
     *
     * @param project the project object to be mapped
     * @return the mapped ProjectDB object
     */
    ProjectDB mapDomainToDB(Project project);


    /**
     * Maps a list of ProjectDB objects to a list of Project objects.
     *
     * @param projectDBs the list of ProjectDB objects to be mapped
     * @return the list of mapped Project objects
     */
    List<Project> mapDBListToDomain(List<ProjectDB> projectDBs);

}
