package com.romiiis.filter;

import com.romiiis.domain.ProjectState;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Filter class for querying projects based on various criteria.
 * Fluent interface design pattern is used for easy chaining of filter criteria.
 */
@Getter
public class ProjectsFilter {
    private ProjectState status = null;
    private String languageCode = null;
    private boolean hasFeedback = false;

    // Additional filters can be added here
    @Setter
    private UUID translatorId = null;

    @Setter
    private UUID customerId = null;

    public ProjectsFilter() {

    }

    /**
     * Sets the status filter for projects.
     * @param status the desired project state
     * @return the updated ProjectsFilter instance
     */
    public ProjectsFilter setStatus(ProjectState status) {
        this.status = status;
        return this;
    }

    /**
     * Sets the language code filter for projects.
     * @param languageCode the desired language code
     * @return the updated ProjectsFilter instance
     */
    public ProjectsFilter setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }


    /**
     * Sets the feedback presence filter for projects.
     * @param hasFeedback whether to filter projects that have feedback
     * @return the updated ProjectsFilter instance
     */
    public ProjectsFilter setHasFeedback(Object hasFeedback) {
        if (hasFeedback == null) {
            this.hasFeedback = false;
        } else {
            this.hasFeedback = Boolean.parseBoolean(hasFeedback.toString());
        }
        return this;
    }

}
