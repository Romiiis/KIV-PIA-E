package com.romiiis.service.impl;

import com.romiiis.domain.Feedback;
import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.exception.*;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.service.api.IProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Locale;
import java.util.UUID;

import static org.mockito.Mockito.*;

class FeedbackServiceImplTest {

    @Mock
    private IFeedbackRepository feedbackRepository;
    @Mock
    private IProjectService projectService;
    @Mock
    private IExecutionContextProvider callerContextProvider;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private UUID projectId;
    private Feedback mockFeedback;
    private Project mockProject;
    private User customer;
    private User translator;
    private User admin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        projectId = UUID.randomUUID();
        customer = User.createCustomer("Customer", "customer@gmail.com");
        translator = User.createTranslator("Translator", "translator@gmail.com", java.util.Set.of(Locale.ENGLISH));
        admin = User.createAdmin("Admin", "admin@gmail.com");

        mockProject = Project.builder()
                .id(projectId)
                .customer(customer)
                .translator(translator)
                .build();

        mockFeedback = Feedback.builder()
                .id(UUID.randomUUID())
                .projectId(projectId)
                .text("Excellent translation!")
                .build();
    }

    // -------------------------------------------------------------------
    // getFeedbackByProjectId
    // -------------------------------------------------------------------

    @DisplayName("getFeedbackByProjectId should return feedback when found (admin)")
    @Test
    void getFeedbackByProjectId_shouldReturnFeedback_whenFound_admin() {
        when(callerContextProvider.getCaller()).thenReturn(admin);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);
        when(feedbackRepository.getFeedbackByProjectId(projectId)).thenReturn(mockFeedback);

        Feedback result = feedbackService.getFeedbackByProjectId(projectId);

        assert result != null;
        assert result.equals(mockFeedback);
        verify(feedbackRepository, times(2)).getFeedbackByProjectId(projectId);
    }

    @DisplayName("CUSTOMER (project owner) should access their feedback")
    @Test
    void getFeedbackByProjectId_customerAccessOwnFeedback() {
        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);
        when(feedbackRepository.getFeedbackByProjectId(projectId)).thenReturn(mockFeedback);

        Feedback result = feedbackService.getFeedbackByProjectId(projectId);

        assert result != null;
        verify(feedbackRepository, times(2)).getFeedbackByProjectId(projectId);
    }

    @DisplayName("TRANSLATOR assigned to project should access feedback")
    @Test
    void getFeedbackByProjectId_translatorAccessFeedback() {
        when(callerContextProvider.getCaller()).thenReturn(translator);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);
        when(feedbackRepository.getFeedbackByProjectId(projectId)).thenReturn(mockFeedback);

        Feedback result = feedbackService.getFeedbackByProjectId(projectId);

        assert result != null;
        verify(feedbackRepository, times(2)).getFeedbackByProjectId(projectId);
    }

    @DisplayName("TRANSLATOR NOT assigned to project should NOT access feedback")
    @Test
    void getFeedbackByProjectId_translatorNoAccessFeedback() {
        User anotherTranslator = User.createTranslator("Another Translator", "another@gmail.com", java.util.Set.of(Locale.ENGLISH));

        when(callerContextProvider.getCaller()).thenReturn(anotherTranslator);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);
        when(feedbackRepository.getFeedbackByProjectId(projectId)).thenReturn(mockFeedback);

        try {
            feedbackService.getFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
        verify(feedbackRepository, never()).getFeedbackByProjectId(any());

    }

    @DisplayName("CUSTOMER (not owner) should NOT access feedback")
    @Test
    void getFeedbackByProjectId_unauthorizedCustomerShouldFail() {
        User stranger = User.createCustomer("Stranger", "stranger@gmail.com");

        when(callerContextProvider.getCaller()).thenReturn(stranger);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        try {
            feedbackService.getFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
        verify(feedbackRepository, never()).getFeedbackByProjectId(any());
    }



    @DisplayName("getFeedbackByProjectId should throw FeedbackNotFoundException when feedback missing")
    @Test
    void getFeedbackByProjectId_shouldThrow_whenFeedbackNotFound() {
        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);
        when(feedbackRepository.getFeedbackByProjectId(projectId)).thenReturn(null);

        try {
            feedbackService.getFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof FeedbackNotFoundException;
        }
        verify(feedbackRepository, times(1)).getFeedbackByProjectId(projectId);
    }

    @DisplayName("getFeedbackByProjectId should throw UserNotFoundException when caller missing")
    @Test
    void getFeedbackByProjectId_shouldThrow_whenCallerMissing() {
        when(callerContextProvider.getCaller()).thenReturn(null);

        try {
            feedbackService.getFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }

    @DisplayName("getFeedbackByProjectId should throw ProjectNotFoundException when project not found")
    @Test
    void getFeedbackByProjectId_shouldThrow_whenProjectNotFound() {
        when(callerContextProvider.getCaller()).thenReturn(admin);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        try {
            feedbackService.getFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof ProjectNotFoundException;
        }
    }

    @DisplayName("getFeedbackByProjectId should throw NoAccessToOperateException when unauthorized user")
    @Test
    void getFeedbackByProjectId_shouldThrow_whenUnauthorized() {
        User stranger = User.createCustomer("Stranger", "stranger@gmail.com");
        when(callerContextProvider.getCaller()).thenReturn(stranger);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        try {
            feedbackService.getFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    // -------------------------------------------------------------------
    // saveFeedback
    // -------------------------------------------------------------------

    @DisplayName("saveFeedback should save feedback when called by project owner")
    @Test
    void saveFeedback_shouldSave_whenOwner() {
        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        feedbackService.saveFeedback(mockFeedback);

        verify(feedbackRepository).save(mockFeedback);
    }

    @DisplayName("saveFeedback should throw SecurityException when user not project owner")
    @Test
    void saveFeedback_shouldThrow_whenNotOwner() {
        when(callerContextProvider.getCaller()).thenReturn(translator);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        try {
            feedbackService.saveFeedback(mockFeedback);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
        verify(feedbackRepository, never()).save(any());
    }

    @DisplayName("saveFeedback should throw SecurityException when user missing")
    @Test
    void saveFeedback_shouldThrow_whenUserMissing() {
        when(callerContextProvider.getCaller()).thenReturn(null);

        try {
            feedbackService.saveFeedback(mockFeedback);
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }

    @DisplayName("saveFeedback should throw NoProjectFound when project not found")
    @Test
    void saveFeedback_shouldThrow_whenProjectNotFound() {
        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        try {
            feedbackService.saveFeedback(mockFeedback);
            assert false;
        } catch (Exception e) {
            assert e instanceof ProjectNotFoundException;
        }
    }

    // -------------------------------------------------------------------
    // deleteProjectFeedback
    // -------------------------------------------------------------------

    @DisplayName("deleteProjectFeedback should delete when called by project owner")
    @Test
    void deleteProjectFeedback_shouldDelete_whenOwner() {
        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        feedbackService.deleteProjectFeedbackByProjectId(projectId);

        verify(feedbackRepository).deleteForProject(projectId);
    }

    @DisplayName("deleteProjectFeedback should throw NoAccessToOperateException when not owner")
    @Test
    void deleteProjectFeedback_shouldThrow_whenNotOwner() {
        when(callerContextProvider.getCaller()).thenReturn(translator);
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        try {
            feedbackService.deleteProjectFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
        verify(feedbackRepository, never()).deleteForProject(any());
    }

    @DisplayName("deleteProjectFeedback should throw UserNotFoundException when user missing")
    @Test
    void deleteProjectFeedback_shouldThrow_whenUserMissing() {
        when(callerContextProvider.getCaller()).thenReturn(null);

        try {
            feedbackService.deleteProjectFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }

    @DisplayName("deleteProjectFeedback should throw ProjectNotFoundException when project not found")
    @Test
    void deleteProjectFeedback_shouldThrow_whenProjectNotFound() {
        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        try {
            feedbackService.deleteProjectFeedbackByProjectId(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof ProjectNotFoundException;
        }
    }
}
