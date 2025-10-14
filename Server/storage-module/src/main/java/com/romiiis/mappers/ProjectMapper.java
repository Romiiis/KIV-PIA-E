package com.romiiis.mappers;

import com.romiiis.Project;
import com.romiiis.model.ProjectDB;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

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
