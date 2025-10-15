package com.romiiis.service.impl;


import com.romiiis.domain.Project;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.service.interfaces.IProjectService;
import com.romiiis.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DefaultProjectServiceImpl implements IProjectService {

    private final IUserService IUserService;
    private final IProjectRepository IProjectRepository;

    @Override
    public Project createProject(Locale targetLanguage, byte[] sourceFile) {
        return null;
    }

    @Override
    public List<Project> getAllProjects() {
        List<Project> projects = IProjectRepository.getAll();

        return projects;
    }
}
