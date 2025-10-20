package com.romiiis.configuration;

import org.springframework.data.mongodb.core.query.Criteria;

public class UserMongoFilter {

    public static Criteria toCriteria(UsersFilter filter) {

        // If no filter criteria are provided, return an empty Criteria
        Criteria finalCriteria = new Criteria();

        // Apply filters based on the provided ProjectsFilter object


        if (filter.getRole() != null) {
            finalCriteria = finalCriteria.and("role").is(filter.getRole());
        }

        if (filter.getLanguageCode() != null) {
            finalCriteria = finalCriteria.and("languages").is(filter.getLanguageCode());
        }


        return finalCriteria;
    }
}
