package com.romiiis.service.interfaces;


import com.romiiis.domain.Project;

import java.util.List;
import java.util.Locale;

/**
 * Service interface for managing projects.
 *
 * @author Roman Pejs
 */
public interface IProjectService {

    /**
     * Creates a new project for the current user
     * @param targetLanguage target language for translation
     * @param sourceFile source file to translate
     * @return newly created project
     */
    Project createProject(Locale targetLanguage, byte[] sourceFile);
}
