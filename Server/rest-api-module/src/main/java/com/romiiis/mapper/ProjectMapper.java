package com.romiiis.mapper;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Project;
import com.romiiis.domain.WrapperProjectFeedback;
import com.romiiis.model.ProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * Mapper interface for Project-related data transformations.
 * This interface is intended to define methods for mapping between
 * Project entities and their corresponding Data Transfer Objects (DTOs).
 *
 * @author Roman Pejs
 */
@Mapper(componentModel = "spring", uses = {CommonMapper.class, UserMapper.class, FeedbackMapper.class})
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
     * Converts a WrapperProjectFeedback to a ProjectDTO.
     *
     * @param wrapper the WrapperProjectFeedback to be converted
     * @return the corresponding ProjectDTO
     */
    @Mapping(target = "id", source = "project.id")
    @Mapping(target = "targetLanguage", source = "project.targetLanguage")
    @Mapping(target = "originalFileName", source = "project.originalFileName")
    @Mapping(target = "translatedFileName", source = "project.translatedFileName")
    @Mapping(target = "customer", source = "project.customer")
    @Mapping(target = "translator", source = "project.translator")
    @Mapping(target = "state", source = "project.state")
    @Mapping(target = "createdAt", source = "project.createdAt")
    ProjectDTO mapWrapperProjectWithFeedbackToDTO(WrapperProjectFeedback wrapper);

    /**
     * Converts a list of WrapperProjectFeedback to a list of ProjectDTOs.
     *
     * @param aggregatedResults the list of WrapperProjectFeedback to be converted
     * @return the corresponding list of ProjectDTOs
     */
    List<ProjectDTO> mapListWrapperProjectFeedbackToDTO(List<WrapperProjectFeedback> aggregatedResults);

    /**
     * Converts a ProjectDTO to a Project entity.
     *
     * @param projectDTO the ProjectDTO to be converted
     * @return the corresponding Project entity
     */
    Project mapDTOToDomain(ProjectDTO projectDTO);

    // --- Resource → ResourceHeader ---
    @Mapping(target = "resourceName", expression = "java(resource.getFilename())")
    @Mapping(target = "resourceData", expression = "java(readBytes(resource))")
    ResourceHeader resourceToHeader(Resource resource);

    // --- ResourceHeader → Resource ---
    default Resource headerToResource(ResourceHeader header) {
        return new ByteArrayResource(header.resourceData()) {
            @Override
            public String getFilename() {
                return header.resourceName();
            }
        };
    }

    default byte[] readBytes(Resource resource) {
        try (var input = resource.getInputStream()) {
            return input.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource bytes for " + resource.getFilename(), e);
        }
    }


}
