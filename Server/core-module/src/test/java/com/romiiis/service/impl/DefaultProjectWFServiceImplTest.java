package com.romiiis.service.impl;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Feedback;
import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.exception.NoAccessToOperateException;
import com.romiiis.exception.ProjectNotFoundException;
import com.romiiis.security.CallerContextProvider;
import com.romiiis.service.interfaces.IFeedbackService;
import com.romiiis.service.interfaces.IFileSystemService;
import com.romiiis.service.interfaces.IProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Locale;
import java.util.UUID;

import static org.mockito.Mockito.*;

class DefaultProjectWFServiceImplTest {

    @Mock
    private IFileSystemService fileSystemService;
    @Mock
    private IProjectService projectService;
    @Mock
    private IFeedbackService feedbackService;
    @Mock
    private CallerContextProvider callerContextProvider;

    @InjectMocks
    private DefaultProjectWFServiceImpl wfService;

    private User customer;
    private User translator;
    private User admin;
    private Project project;
    private UUID projectId;
    private ResourceHeader resourceHeader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = User.createCustomer("Customer", "customer@gmail.com");
        translator = User.createTranslator("Translator", "translator@gmail.com", java.util.Set.of(Locale.ENGLISH));
        admin = User.createAdmin("Admin", "admin@gmail.com");

        projectId = UUID.randomUUID();
        project = new Project(customer, Locale.ENGLISH, "source.txt");

        resourceHeader = new ResourceHeader("translated.txt", "Translated content".getBytes());
    }


    @DisplayName("uploadTranslatedFile should upload translated file successfully by translator")
    @Test
    void uploadTranslatedFile_shouldUploadSuccessfully() throws Exception {
        when(callerContextProvider.getCaller()).thenReturn(translator);
        project.assignTranslator(translator);
        when(projectService.getProjectById(projectId)).thenReturn(project);


        Project result = wfService.uploadTranslatedFile(projectId, resourceHeader);

        assert result != null;
        verify(fileSystemService).saveTranslatedFile(eq(projectId), eq(resourceHeader.resourceData()));
        verify(projectService, atLeastOnce()).updateProject(any(Project.class));
    }

    @DisplayName("uploadTranslatedFile should throw NoAccessToOperateException when caller is not translator")
    @Test
    void uploadTranslatedFile_shouldThrow_whenCallerNotTranslator() {
        project.assignTranslator(translator);

        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        try {
            wfService.uploadTranslatedFile(projectId, resourceHeader);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
        verify(fileSystemService, never()).saveTranslatedFile(any(), any());
    }

    @DisplayName("uploadTranslatedFile should throw ProjectNotFoundException when project does not exist")
    @Test
    void uploadTranslatedFile_shouldThrow_whenProjectNotFound() {
        when(callerContextProvider.getCaller()).thenReturn(translator);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        try {
            wfService.uploadTranslatedFile(projectId, resourceHeader);
            assert false;
        } catch (Exception e) {
            assert e instanceof ProjectNotFoundException;
        }
    }


    @DisplayName("closeProject should allow admin to close project")
    @Test
    void closeProject_shouldAllowAdmin() throws Exception {
        when(callerContextProvider.getCaller()).thenReturn(admin);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        Project result = wfService.closeProject(projectId);

        assert result != null;
        verify(projectService).updateProject(any(Project.class));
    }

    @DisplayName("closeProject should throw NoAccessToOperateException for non-admin user")
    @Test
    void closeProject_shouldThrowForNonAdmin() {
        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        try {
            wfService.closeProject(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
        verify(projectService, never()).updateProject(any());
    }


    @DisplayName("approveProject should allow project owner to approve")
    @Test
    void approveProject_shouldAllowOwner() {

        project.assignTranslator(translator);
        project.complete("translated.txt");

        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        Project result = wfService.approveProject(projectId);

        assert result != null;
        verify(projectService).updateProject(any(Project.class));
    }

    @DisplayName("approveProject should throw NoAccessToOperateException for non-owner")
    @Test
    void approveProject_shouldThrowForNonOwner() {
        when(callerContextProvider.getCaller()).thenReturn(translator);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        try {
            wfService.approveProject(projectId);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
        verify(projectService, never()).updateProject(any());
    }


    @DisplayName("rejectProject should allow project owner to reject and store feedback")
    @Test
    void rejectProject_shouldAllowOwner() throws Exception {
        Project mockProject = mock(Project.class);

        when(mockProject.getId()).thenReturn(projectId);
        when(mockProject.getCustomer()).thenReturn(customer);
        when(mockProject.reject(any())).thenReturn(new Feedback(projectId, "Bad translation"));
        when(projectService.getProjectById(projectId)).thenReturn(mockProject);

        when(callerContextProvider.getCaller()).thenReturn(customer);

        Project result = wfService.rejectProject(projectId, "Bad translation");

        assert result != null;
        verify(feedbackService).deleteProjectFeedbackByProjectId(projectId);
        verify(feedbackService).saveFeedback(any(Feedback.class));
        verify(projectService).updateProject(mockProject);
    }

    @DisplayName("rejectProject should throw NoAccessToOperateException for non-owner")
    @Test
    void rejectProject_shouldThrowForNonOwner() {
        when(callerContextProvider.getCaller()).thenReturn(translator);
        when(projectService.getProjectById(projectId)).thenReturn(project);

        try {
            wfService.rejectProject(projectId, "Bad translation");
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
        verify(feedbackService, never()).saveFeedback(any());
    }

    @DisplayName("rejectProject should throw ProjectNotFoundException when project not found")
    @Test
    void rejectProject_shouldThrow_whenProjectNotFound() {
        when(callerContextProvider.getCaller()).thenReturn(customer);
        when(projectService.getProjectById(projectId)).thenReturn(null);

        try {
            wfService.rejectProject(projectId, "Bad translation");
            assert false;
        } catch (Exception e) {
            assert e instanceof ProjectNotFoundException;
        }
    }
}
