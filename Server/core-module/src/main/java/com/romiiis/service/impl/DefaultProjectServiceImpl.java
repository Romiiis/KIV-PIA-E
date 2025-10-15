package com.romiiis.service.impl;


import com.romiiis.domain.Project;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.service.interfaces.IProjectService;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Default implementation of the IProjectService interface.
 *
 * @author Roman Pejs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultProjectServiceImpl implements IProjectService {

    /** Repositories & Services */
    private final IUserService IUserService;
    private final IProjectRepository IProjectRepository;

    /**
     * Creates a new project for the current user
     * @param targetLanguage target language for translation
     * @param sourceFile source file to translate
     * @return newly created project
     */
    @Override
    public Project createProject(Locale targetLanguage, byte[] sourceFile) {
        return null;
    }

}
