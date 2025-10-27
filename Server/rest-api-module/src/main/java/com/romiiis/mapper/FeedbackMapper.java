package com.romiiis.mapper;

import com.romiiis.domain.Feedback;
import com.romiiis.model.ProjectFeedbackDTO;
import org.mapstruct.Mapper;

/**
 * Mapper interface for Feedback entities.
 */
@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface FeedbackMapper {

    /**
     * Converts a Feedback domain object to a ProjectFeedbackDTO.
     *
     * @param feedback the Feedback domain object
     * @return the corresponding ProjectFeedbackDTO
     */
    ProjectFeedbackDTO domainToDto(Feedback feedback);

    /**
     * Converts a ProjectFeedbackDTO to a Feedback domain object.
     *
     * @param feedbackDTO the ProjectFeedbackDTO
     * @return the corresponding Feedback domain object
     */
    Feedback dtoToDomain(ProjectFeedbackDTO feedbackDTO);
}
