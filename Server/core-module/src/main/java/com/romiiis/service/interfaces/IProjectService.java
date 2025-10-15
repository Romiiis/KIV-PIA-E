package com.romiiis.service.interfaces;


import com.romiiis.domain.Project;

import java.util.List;
import java.util.Locale;

public interface IProjectService {
    /**
     * Creates a new project for the current user
     * @param targetLanguage target language for translation
     * @param sourceFile source file to translate
     * @return newly created project
     */
    Project createProject(Locale targetLanguage, byte[] sourceFile);

    /**
     * Fetches all projects, no matter their state
     *
     * @return all projects
     */
    List<Project> getAllProjects();

    // other service methods here
}
