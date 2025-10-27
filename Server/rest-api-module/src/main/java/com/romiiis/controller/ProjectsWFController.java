package com.romiiis.controller;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.mapper.ProjectMapper;
import com.romiiis.model.ProjectDTO;
import com.romiiis.model.ProjectFeedbackRequestDTO;
import com.romiiis.service.interfaces.IProjectWFService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


/**
 * Controller for handling project workflow-related endpoints.
 */
@RequiredArgsConstructor
@Slf4j
@RestController
public class ProjectsWFController implements ProjectsWorkflowApi {


    private final IProjectWFService projectWorkflowService;
    private final ProjectMapper projectMapper;

    /**
     * Approves the translated content for a project identified by its UUID.
     *
     * @param id The UUID of the project.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Override
    public ResponseEntity<ProjectDTO> approveTranslatedContent(UUID id) {
        projectWorkflowService.approveProject(
                id
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Closes the project identified by its UUID.
     *
     * @param id The UUID of the project.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Override
    public ResponseEntity<ProjectDTO> closeProject(UUID id) {
        projectWorkflowService.closeProject(
                id
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Rejects the translated content for a project identified by its UUID, providing feedback.
     *
     * @param id                        The UUID of the project.
     * @param projectFeedbackRequestDTO The feedback details for the rejection.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Override
    public ResponseEntity<ProjectDTO> rejectTranslatedContent(UUID id, ProjectFeedbackRequestDTO projectFeedbackRequestDTO) {
        projectWorkflowService.rejectProject(
                id,
                projectFeedbackRequestDTO.getText()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Uploads the translated content for a project identified by its UUID.
     *
     * @param id   The UUID of the project.
     * @param file The translated file to be uploaded.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Override
    public ResponseEntity<ProjectDTO> uploadTranslatedContent(UUID id, MultipartFile file) {

        ResourceHeader resHeader = projectMapper.resourceToHeader(file.getResource());

        projectWorkflowService.uploadTranslatedFile(id, resHeader);

        return ResponseEntity.ok().build();
    }
}
