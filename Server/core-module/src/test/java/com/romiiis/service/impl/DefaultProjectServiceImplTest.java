package com.romiiis.service.impl;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.service.interfaces.IFileSystemService;
import com.romiiis.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

class DefaultProjectServiceImplTest {

    @Mock
    private IUserService userService;
    @Mock
    private IProjectRepository projectRepository;
    @Mock
    private IFileSystemService fsService;

    @InjectMocks
    private DefaultProjectServiceImpl projectService;

    private UUID userId;
    String userName = "Mock User";
    String userEmail = "mock@gmail.com";
    private User mockUser;
    private ResourceHeader sourceFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = User.createCustomer(userName, userEmail);
        userId = mockUser.getId();
        sourceFile = new ResourceHeader("test.txt", "Hello world".getBytes());
    }

    @DisplayName("createProject should create project successfully")
    @Test
    void createProject_shouldCreateProjectSuccessfully() throws Exception {
        // given
        when(userService.getUserById(userId)).thenReturn(mockUser);

        when(projectRepository.findById(any())).thenAnswer(inv -> Project.builder().id(inv.getArgument(0)).customer(mockUser).build());
        // when
        Project result = projectService.createProject(userId, Locale.ENGLISH, sourceFile);

        // then
        assert result != null;
        assert result.getCustomer().equals(mockUser);
        verify(fsService).saveOriginalFile(eq(result.getId()), eq(sourceFile.resourceData()));
        verify(projectRepository).save(any(Project.class));
    }

    @DisplayName("createProject should throw UserNotFoundException when user does not exist")
    @Test
    void createProject_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // given
        when(userService.getUserById(userId)).thenReturn(null);

        // when & then
        try {
            projectService.createProject(userId, Locale.ENGLISH, sourceFile);
            assert false; // should not reach here
        } catch (Exception e) {
            assert e instanceof com.romiiis.exception.UserNotFoundException;
        }
    }

    @DisplayName("createProject should throw IllegalArgumentException when user is not a customer")
    @Test
    void createProject_shouldThrowIllegalArgumentException_whenUserIsNotCustomer() {
        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.ENGLISH);
        locales.add(Locale.FRENCH);

        // given
        User translatorUser = User.createTranslator("Translator", "translator@gmail.com", locales);
        when(userService.getUserById(userId)).thenReturn(translatorUser);

        // when & then
        try {
            projectService.createProject(userId, Locale.ENGLISH, sourceFile);
            assert false; // should not reach here
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException;
        }
    }

    @DisplayName("getAllProjects should return list of projects (Empty List)")
    @Test
    void getAllProjects_shouldReturnListOfProjects() {
        // given
        var filter = new com.romiiis.filter.ProjectsFilter();
        when(projectRepository.getAll(filter)).thenReturn(java.util.List.of());

        // when
        var result = projectService.getAllProjects(filter);

        // then
        assert result != null;
        assert result.isEmpty();
        verify(projectRepository).getAll(filter);
    }

    @DisplayName("getAllProjects should return list of projects (Non-Empty List)")
    @Test
    void getAllProjects_shouldReturnListOfProjects_NonEmpty() {
        // given
        var filter = new com.romiiis.filter.ProjectsFilter();
        var mockProject = Project.builder().id(UUID.randomUUID()).customer(mockUser).build();
        when(projectRepository.getAll(filter)).thenReturn(java.util.List.of(mockProject));

        // when
        var result = projectService.getAllProjects(filter);

        // then
        assert result != null;
        assert result.size() == 1;
        assert result.getFirst().equals(mockProject);
        verify(projectRepository).getAll(filter);
    }


    @DisplayName("getProjectById should return project when found")
    @Test
    void getProjectById_shouldReturnProject_whenFound() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        var mockProject = Project.builder().id(projectId).customer(mockUser).build();
        when(projectRepository.findById(projectId)).thenReturn(mockProject);

        // when
        var result = projectService.getProjectById(projectId);

        // then
        assert result != null;
        assert result.equals(mockProject);
        verify(projectRepository).findById(projectId);
    }

    @DisplayName("getProjectById should throw ProjectNotFoundException when not found")
    @Test
    void getProjectById_shouldThrowProjectNotFoundException_whenNotFound() {
        // given
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(null);
        // when & then
        try {
            projectService.getProjectById(projectId);
            assert false; // should not reach here
        } catch (Exception e) {
            assert e instanceof com.romiiis.exception.ProjectNotFoundException;
        }
    }

    @DisplayName("getOriginalFile should retrieve original file from file system service")
    @Test
    void getOriginalFile_shouldRetrieveOriginalFile() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        ResourceHeader mockResource = new ResourceHeader("original.txt", "Original content".getBytes());
        when(fsService.getOriginalFile(projectId)).thenReturn(mockResource);
        when(projectRepository.findById(projectId)).thenReturn(Project.builder().id(projectId).originalFileName("original.txt").build());
        // when
        ResourceHeader result = projectService.getOriginalFile(projectId);

        // then
        assert result != null;
        assert result.equals(mockResource);
        verify(fsService).getOriginalFile(projectId);
    }

    @DisplayName("getOriginalFile should throw ProjectNotFoundException when project not found")
    @Test
    void getOriginalFile_shouldThrowProjectNotFoundException_whenProjectNotFound() {
        // given
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(null);
        // when & then
        try {
            projectService.getOriginalFile(projectId);
            assert false; // should not reach here
        } catch (Exception e) {
            assert e instanceof com.romiiis.exception.ProjectNotFoundException;
        }
    }

    @DisplayName("getTranslatedFile should retrieve translated file from file system service")
    @Test
    void getTranslatedFile_shouldRetrieveTranslatedFile() throws Exception {
        // given
        UUID projectId = UUID.randomUUID();
        ResourceHeader mockResource = new ResourceHeader("translated.txt", "Translated content".getBytes());
        when(fsService.getTranslatedFile(projectId)).thenReturn(mockResource);
        when(projectRepository.findById(projectId)).thenReturn(Project.builder().id(projectId).translatedFileName("translated.txt").build());


        // when
        ResourceHeader result = projectService.getTranslatedFile(projectId);

        // then
        assert result != null;
        assert result.equals(mockResource);
        verify(fsService).getTranslatedFile(projectId);
    }


    @DisplayName("getTranslatedFile should throw ProjectNotFoundException when project not found")
    @Test
    void getTranslatedFile_shouldThrowProjectNotFoundException_whenProjectNotFound() {
        // given
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(null);
        // when & then
        try {
            projectService.getTranslatedFile(projectId);
            assert false; // should not reach here
        } catch (Exception e) {
            assert e instanceof com.romiiis.exception.ProjectNotFoundException;
        }
    }
}
