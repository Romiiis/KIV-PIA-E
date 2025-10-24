package com.romiiis.service.impl;

import com.romiiis.domain.Feedback;
import com.romiiis.exception.FeedbackNotFoundException;
import com.romiiis.repository.IFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;

class DefaultFeedbackServiceImplTest {

    @Mock
    private IFeedbackRepository feedbackRepository;

    @InjectMocks
    private DefaultFeedbackServiceImpl feedbackService;

    private UUID projectId;
    private Feedback mockFeedback;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        projectId = UUID.randomUUID();

        mockFeedback = Feedback.builder()
                .id(UUID.randomUUID())
                .projectId(projectId)
                .text("Great translation!")
                .build();
    }

    @DisplayName("getFeedbackByProjectId should return feedback when found")
    @Test
    void getFeedbackByProjectId_shouldReturnFeedback_whenFound() {
        // given
        when(feedbackRepository.getFeedbackByProjectId(projectId)).thenReturn(mockFeedback);

        // when
        Feedback result = feedbackService.getFeedbackByProjectId(projectId);

        // then
        assert result != null;
        assert result.equals(mockFeedback);
        verify(feedbackRepository, times(2)).getFeedbackByProjectId(projectId);
    }

    @DisplayName("getFeedbackByProjectId should throw FeedbackNotFoundException when not found")
    @Test
    void getFeedbackByProjectId_shouldThrow_whenNotFound() {
        // given
        when(feedbackRepository.getFeedbackByProjectId(projectId)).thenReturn(null);

        // when & then
        try {
            feedbackService.getFeedbackByProjectId(projectId);
            assert false; // should not reach here
        } catch (Exception e) {
            assert e instanceof FeedbackNotFoundException;
            assert e.getMessage().contains(projectId.toString());
        }

        verify(feedbackRepository, times(1)).getFeedbackByProjectId(projectId);
    }
}
