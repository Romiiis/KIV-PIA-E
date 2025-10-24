package com.romiiis.service.impl;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Project;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.service.interfaces.IFileSystemService;
import com.romiiis.service.interfaces.IProjectService;
import com.romiiis.service.interfaces.IProjectWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DefaultProjectWorkflowServiceImpl implements IProjectWorkflowService {

    private final IFileSystemService fileSystemService;
    private final IProjectService projectService;

    @Override
    public void uploadTranslatedFile(UUID projectId, ResourceHeader resHeader) throws ProjectNotFoundException {

        Project project = projectService.getProjectById(projectId);

        if (project == null) {
            log.error("Project with ID {} not found", projectId);
            throw new ProjectNotFoundException("Project not found");
        }

        project.complete(resHeader.resourceName());

        fileSystemService.saveTranslatedFile(project.getId(), resHeader.resourceData());

        // Update project in the database
        projectService.updateProject(project);

        log.info("Project with ID {} completed", projectId);
    }
}
