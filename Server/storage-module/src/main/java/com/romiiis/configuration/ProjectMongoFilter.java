package com.romiiis.configuration;

import com.romiiis.filter.ProjectsFilter;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Utility class for converting ProjectsFilter to MongoDB Criteria.
 */
public class ProjectMongoFilter {

    /**
     * Converts a ProjectsFilter object to a MongoDB Criteria object.
     *
     * @param filter the ProjectsFilter object containing filter criteria
     * @return the corresponding MongoDB Criteria object
     */
    public static Criteria toCriteria(ProjectsFilter filter) {

        // If no filter criteria are provided, return an empty Criteria
        Criteria finalCriteria = new Criteria();

        // Apply filters based on the provided ProjectsFilter object


        if (filter.getStatus() != null) {
            finalCriteria = finalCriteria.and("state").is(filter.getStatus());
        }

        if (filter.getLanguageCode() != null) {
            finalCriteria = finalCriteria.and("targetLanguage").is(filter.getLanguageCode());
        }

        // TODO - This is not in project model
//        if (filter.isHasFeedback()) {
//            finalCriteria = finalCriteria.and("feedback").exists(true);
//        }

        return finalCriteria;
    }
}
