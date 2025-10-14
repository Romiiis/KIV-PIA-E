package com.romiiis.mappers;

import com.romiiis.Feedback;
import com.romiiis.model.FeedbackDB;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    /**
     * Maps a FeedbackDB object to a Feedback object.
     * @param feedbackDB the feedbackDB object to be mapped
     * @return the mapped Feedback object
     */
    Feedback mapDBToDomain(FeedbackDB feedbackDB);

    /**
     * Maps a Feedback object to a FeedbackDB object.
     * @param feedback the feedback object to be mapped
     * @return the mapped FeedbackDB object
     */
    FeedbackDB mapDomainToDB(Feedback feedback);

}
