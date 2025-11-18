package com.romiiis.service.impl;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.exception.FileNotFoundException;
import com.romiiis.exception.FileStorageException;
import com.romiiis.exception.NoAccessToOperateException;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.port.IFileSystemService;
import com.romiiis.service.api.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

    @Mock private IUserService userService;
    @Mock private IProjectRepository projectRepository;
    @Mock private IFileSystemService fsService;
    @Mock private IExecutionContextProvider callerContextProvider;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User admin;
    private User customer;
    private User translator;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = User.createAdmin("Admin", "admin@test.com");
        customer = User.createCustomer("John", "john@test.com");
        translator = User.createTranslator("Eva", "eva@test.com", java.util.Set.of(Locale.ENGLISH));

        project = Project.builder()
                .id(UUID.randomUUID())
                .customer(customer)
                .translator(translator)
                .originalFileName("orig.txt")
                .translatedFileName("trans.txt")
                .build();
    }

    private void asUser(User user) {
        when(callerContextProvider.getCaller()).thenReturn(user);
    }


    // ---------------------------------------------------------
    // getProjectById
    // ---------------------------------------------------------
    @DisplayName("Customer can get own project")
    @Test
    void customerCanGetOwnProject() throws Exception {
        asUser(customer);
        when(projectRepository.findById(project.getId())).thenReturn(project);

        Project result = projectService.getProjectById(project.getId());
        assert result.equals(project);
    }

    @DisplayName("Translator can get assigned project")
    @Test
    void translatorCanGetAssignedProject() throws Exception {
        asUser(translator);
        when(projectRepository.findById(project.getId())).thenReturn(project);

        Project result = projectService.getProjectById(project.getId());
        assert result.equals(project);
    }

    @DisplayName("Customer cannot get another user's project")
    @Test
    void customerCannotGetOtherProject() {
        asUser(User.createCustomer("Other", "o@test.com"));
        when(projectRepository.findById(project.getId())).thenReturn(project);

        try {
            projectService.getProjectById(project.getId());
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    @DisplayName("Admin can get any project")
    @Test
    void adminCanGetAnyProject() throws Exception {
        asUser(admin);
        when(projectRepository.findById(project.getId())).thenReturn(project);

        Project result = projectService.getProjectById(project.getId());
        assert result.equals(project);
    }

    // ---------------------------------------------------------
    // getOriginalFile
    // ---------------------------------------------------------
    @DisplayName("Customer can access own original file")
    @Test
    void customerCanAccessOwnOriginalFile() throws Exception {
        asUser(customer);
        when(projectRepository.findById(project.getId())).thenReturn(project);
        when(fsService.getOriginalFile(project.getId()))
                .thenReturn(new ResourceHeader("orig.txt", "data".getBytes()));

        var result = projectService.getOriginalFile(project.getId());
        assert result != null;
        verify(fsService).getOriginalFile(project.getId());
    }

    @DisplayName("Translator can access assigned original file")
    @Test
    void translatorCanAccessAssignedOriginalFile() throws Exception {
        asUser(translator);
        when(projectRepository.findById(project.getId())).thenReturn(project);
        when(fsService.getOriginalFile(project.getId()))
                .thenReturn(new ResourceHeader("orig.txt", "data".getBytes()));

        var result = projectService.getOriginalFile(project.getId());
        assert result != null;
        verify(fsService).getOriginalFile(project.getId());
    }

    @DisplayName("Customer cannot access another user's original file")
    @Test
    void customerCannotAccessOthersOriginalFile() {
        asUser(User.createCustomer("Other", "other@test.com"));
        when(projectRepository.findById(project.getId())).thenReturn(project);

        try {
            projectService.getOriginalFile(project.getId());
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    @DisplayName("Admin can access any original file")
    @Test
    void adminCanAccessAnyOriginalFile() throws Exception {
        asUser(admin);
        when(projectRepository.findById(project.getId())).thenReturn(project);
        when(fsService.getOriginalFile(project.getId()))
                .thenReturn(new ResourceHeader("orig.txt", "data".getBytes()));

        var result = projectService.getOriginalFile(project.getId());
        assert result != null;
    }

    // ---------------------------------------------------------
    // getTranslatedFile
    // ---------------------------------------------------------
    @DisplayName("Translator can access own translated file")
    @Test
    void translatorCanAccessOwnTranslatedFile() throws Exception {
        asUser(translator);
        when(projectRepository.findById(project.getId())).thenReturn(project);
        when(fsService.getTranslatedFile(project.getId()))
                .thenReturn(new ResourceHeader("trans.txt", "translated".getBytes()));

        var result = projectService.getTranslatedFile(project.getId());
        assert result != null;
        verify(fsService).getTranslatedFile(project.getId());
    }

    @DisplayName("Customer can access own translated file")
    @Test
    void customerCanAccessOwnTranslatedFile() throws Exception {
        asUser(customer);
        when(projectRepository.findById(project.getId())).thenReturn(project);
        when(fsService.getTranslatedFile(project.getId()))
                .thenReturn(new ResourceHeader("trans.txt", "translated".getBytes()));

        var result = projectService.getTranslatedFile(project.getId());
        assert result != null;
    }

    @DisplayName("Customer cannot access others translated file")
    @Test
    void customerCannotAccessOthersTranslatedFile() {
        asUser(User.createCustomer("Other", "o@test.com"));
        when(projectRepository.findById(project.getId())).thenReturn(project);

        try {
            projectService.getTranslatedFile(project.getId());
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    @DisplayName("Throws FileNotFoundException when translated file missing")
    @Test
    void throwsFileNotFoundWhenTranslatedMissing() {
        asUser(customer);
        var p = Project.builder().id(UUID.randomUUID()).customer(customer).build();
        when(projectRepository.findById(p.getId())).thenReturn(p);

        try {
            projectService.getTranslatedFile(p.getId());
            assert false;
        } catch (Exception e) {
            assert e instanceof FileNotFoundException;
        }
    }

    @DisplayName("Throws FileStorageException when original file missing")
    @Test
    void throwsFileStorageExceptionWhenOriginalMissing() {
        asUser(customer);
        var p = Project.builder().id(UUID.randomUUID()).customer(customer).build();
        when(projectRepository.findById(p.getId())).thenReturn(p);

        try {
            projectService.getOriginalFile(p.getId());
            assert false;
        } catch (Exception e) {
            assert e instanceof FileStorageException;
        }
    }
}
