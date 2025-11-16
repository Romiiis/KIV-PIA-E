package com.romiiis.event;

import com.romiiis.domain.Project;
import com.romiiis.domain.User;

/**
 * Event representing the assignment of a translator to a project.
 *
 * @param newProject the project to which the translator has been assigned
 */
public record TranslatorAssignedToProjectEvent(Project project){}
