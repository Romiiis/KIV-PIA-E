package com.romiiis.controller;

import com.romiiis.model.ProjectFeedbackDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for handling project feedback related requests.
 *
 * @author Roman Pejs
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProjectFeedbackController implements ProjectsFeedbackApi {

    @Override
    public ResponseEntity<ProjectFeedbackDTO> getProjectFeedback(UUID id) {
        // TODO: Implement the logic to retrieve project feedback by ID
        return ProjectsFeedbackApi.super.getProjectFeedback(id);
    }


}
