package com.romiiis.event;

import com.romiiis.domain.Project;

/**
 * Event representing the completion of a project.
 *
 * @param project the project that has been completed
 */
public record ProjectCompletedEvent(Project project){}
