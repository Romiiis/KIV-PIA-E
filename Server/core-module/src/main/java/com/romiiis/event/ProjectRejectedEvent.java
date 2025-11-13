package com.romiiis.event;

import com.romiiis.domain.Project;

/**
 * Event representing the rejection of a project.
 *
 * @param project the project that has been rejected
 * @param reason the reason for rejection
 */
public record ProjectRejectedEvent (Project project, String reason){}
