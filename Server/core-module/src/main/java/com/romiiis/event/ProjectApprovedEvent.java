package com.romiiis.event;

import com.romiiis.domain.Project;

/**
 * Event representing the approval of a project.
 *
 * @param project the project that has been approved
 */
public record ProjectApprovedEvent (Project project){};

