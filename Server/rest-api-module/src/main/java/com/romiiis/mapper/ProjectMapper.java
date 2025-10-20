package com.romiiis.mapper;

import com.romiiis.domain.Project;
import com.romiiis.model.ProjectDTO;
import com.romiiis.model.ProjectStateDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for Project-related data transformations.
 *
 * This interface is intended to define methods for mapping between
 * Project entities and their corresponding Data Transfer Objects (DTOs).
 *
 * @author Roman Pejs
 */
@Mapper(componentModel = "spring", uses = {CommonMapper.class, UserMapper.class})
public interface ProjectMapper {


    /**
     * Converts a list of Project entities to a list of ProjectDTOs.
     *
     * @param projects the list of Project entities to be converted
     * @return the corresponding list of ProjectDTOs
     */
    List<ProjectDTO> mapDomainListToDTO(List<Project> projects);

    /**
     * Converts a Project entity to a ProjectDTO.
     *
     * @param project the Project entity to be converted
     * @return the corresponding ProjectDTO
     */
    ProjectDTO mapDomainToDTO(Project project);


    /**
     * Converts a ProjectDTO to a Project entity.
     *
     * @param projectDTO the ProjectDTO to be converted
     * @return the corresponding Project entity
     */
    Project mapDTOToDomain(ProjectDTO projectDTO);
}
