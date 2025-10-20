package com.romiiis.controller;

import com.romiiis.model.ProjectFeedbackRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


/**
 * Controller for handling project workflow-related endpoints.
 */
@RequiredArgsConstructor
@Slf4j
@RestController
public class ProjectsWorkflowController implements ProjectsWorkflowApi {

    /**
     * Approves the translated content for a project identified by its UUID.
     *
     * @param id The UUID of the project.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Override
    public ResponseEntity<Void> approveTranslatedContent(UUID id) {
        // TODO Implement logic for approving translated content
        return ProjectsWorkflowApi.super.approveTranslatedContent(id);
    }

    /**
     * Closes the project identified by its UUID.
     *
     * @param id The UUID of the project.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Override
    public ResponseEntity<Void> closeProject(UUID id) {
        // TODO Implement logic for closing the project
        return ProjectsWorkflowApi.super.closeProject(id);
    }

    /**
     * Rejects the translated content for a project identified by its UUID, providing feedback.
     *
     * @param id                        The UUID of the project.
     * @param projectFeedbackRequestDTO The feedback details for the rejection.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Override
    public ResponseEntity<Void> rejectTranslatedContent(UUID id, ProjectFeedbackRequestDTO projectFeedbackRequestDTO) {
        // TODO Implement logic for rejecting translated content with feedback
        return ProjectsWorkflowApi.super.rejectTranslatedContent(id, projectFeedbackRequestDTO);
    }

    /**
     * Uploads the translated content for a project identified by its UUID.
     *
     * @param id   The UUID of the project.
     * @param body The translated content as a Resource.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @Override
    public ResponseEntity<Void> uploadTranslatedContent(UUID id, Resource body) {
        // TODO Implement logic for uploading translated content
        return ProjectsWorkflowApi.super.uploadTranslatedContent(id, body);
    }
}
