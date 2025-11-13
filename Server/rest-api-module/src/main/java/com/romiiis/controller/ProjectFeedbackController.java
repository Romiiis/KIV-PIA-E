package com.romiiis.controller;

import com.romiiis.mapper.FeedbackMapper;
import com.romiiis.model.ProjectFeedbackDTO;
import com.romiiis.service.api.IFeedbackService;
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
public class ProjectFeedbackController extends AbstractController implements ProjectsFeedbackApi {

    private final IFeedbackService feedbackService;
    private final FeedbackMapper feedbackMapper;

    @Override
    public ResponseEntity<ProjectFeedbackDTO> getProjectFeedback(UUID id) {
        var feedback = feedbackService.getFeedbackByProjectId(id);
        return ResponseEntity.ok(feedbackMapper.domainToDto(feedback));
    }

}
