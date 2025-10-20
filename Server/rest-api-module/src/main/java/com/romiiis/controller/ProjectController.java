package com.romiiis.controller;

import com.romiiis.domain.Project;
import com.romiiis.filter.ProjectsFilter;
import com.romiiis.mapper.CommonMapper;
import com.romiiis.mapper.ProjectMapper;
import com.romiiis.model.ProjectDTO;
import com.romiiis.model.ProjectStateDTO;
import com.romiiis.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Controller for project-related endpoints
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProjectController implements ProjectsApi {

    /**
     * Services
     */
    private final IProjectService projectService;
    private final ProjectMapper projectMapper;
    private final CommonMapper commonMapper;


    /**
     * Lists all projects with optional filtering by state, language code, and feedback presence.
     *
     * @param state        Filter projects by their state. (optional)
     * @param languageCode (optional)
     * @param hasFeedback  (optional)
     * @return A ResponseEntity containing a list of ProjectDTOs.
     */
    @Override
    public ResponseEntity<List<ProjectDTO>> listAllProjects(ProjectStateDTO state, String languageCode, Boolean hasFeedback) {
        ProjectsFilter filter = new ProjectsFilter()
                .setHasFeedback(hasFeedback)
                .setLanguageCode(languageCode)
                .setStatus(commonMapper.mapProjectStateDTOToDomain(state));

        var projects = projectService.getAllProjects(filter);
        return ResponseEntity.ok(projectMapper.mapDomainListToDTO(projects));
    }

    /**
     * Creates a new project with the provided language code, content file, and customer ID.
     *
     * @param languageCode Target language code for the project.
     * @param content      Multipart file containing the project content.
     * @param customerId   UUID of the customer creating the project.
     * @return A ResponseEntity containing the created ProjectDTO.
     */
    @Override
    public ResponseEntity<ProjectDTO> createProject(String languageCode, MultipartFile content, UUID customerId) {
        Locale language = Locale.forLanguageTag(languageCode);
        var newProject = projectService.createProject(customerId, language, content.getResource());
        var projectDTO = projectMapper.mapDomainToDTO(newProject);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectDTO);
    }


    /**
     * Retrieves the details of a specific project by its ID.
     *
     * @param id UUID of the project to retrieve.
     * @return A ResponseEntity containing the ProjectDTO.
     */
    @Override
    public ResponseEntity<ProjectDTO> getProjectDetails(UUID id) {
        var project = projectService.getProjectById(id);
        var projectDTO = projectMapper.mapDomainToDTO(project);
        return ResponseEntity.ok(projectDTO);
    }


    /**
     * Downloads the original content of a project by its ID.
     *
     * @param id (required)
     * @return Original project content as a Resource.
     */
    public ResponseEntity<Resource> downloadOriginalContent(UUID id) {
        Resource resource = projectService.getOriginalFile(id);
        Project project = projectService.getProjectById(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + project.getOriginalFileName() + "\"")
                .body(resource);
    }

    /**
     * Downloads the translated content of a project by its ID.
     *
     * @param id (required)
     * @return Translated project content as a Resource.
     */
    @Override
    public ResponseEntity<Resource> downloadTranslatedContent(UUID id) {
        Resource resource = projectService.getTranslatedFile(id);
        Project project = projectService.getProjectById(id);

        if (project.getTranslatedFileName().isEmpty()) {
            log.error("Translated file for project ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + project.getTranslatedFileName() + "\"")
                .body(resource);
    }
}
