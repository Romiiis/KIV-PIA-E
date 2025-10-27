package com.romiiis.controller;

import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.filter.ProjectsFilter;
import com.romiiis.model.ProjectDTO;
import com.romiiis.model.ProjectStateDTO;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.IUserRepository;
import com.romiiis.repository.mongo.MongoProjectRepository;
import com.romiiis.repository.mongo.MongoUserRepository;
import com.romiiis.security.CallerContextProvider;
import com.romiiis.service.interfaces.IProjectService;
import com.romiiis.service.interfaces.IUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProjectController.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProjectRepository projectRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private CallerContextProvider callerContextProvider;

    private User customer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        projectRepository.deleteAll();

        customer = userService.createNewCustomer("John Customer","customer@test.com", "securePassword");
        customerId = customer.getId();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customerId.toString(), null)
        );
        callerContextProvider.setCaller(customer);
    }

    @DisplayName("GET /projects should return empty list initially")
    @Test
    void listAllProjects_empty() throws Exception {
        mockMvc.perform(get("/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @DisplayName("POST /projects creates a new project successfully")
    @Test
    void createProject_shouldCreateProject() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "content",
                "example.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello test project!".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/projects")
                        .file(file)
                        .param("languageCode", "en")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customer.id").value(customerId.toString()))
                .andExpect(jsonPath("$.targetLanguage").value("en"));

        List<Project> all = projectRepository.getAll(new ProjectsFilter());
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getCustomer().getId()).isEqualTo(customerId);
    }

    @DisplayName("GET /projects/{id} returns project details")
    @Test
    void getProjectDetails_shouldReturnProject() throws Exception {
        // Arrange – create a project directly in DB
        Project project = new Project(customer, Locale.ENGLISH, "doc.txt");
        projectRepository.save(project);

        // Act & Assert
        mockMvc.perform(get("/projects/" + project.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId().toString()))
                .andExpect(jsonPath("$.customer.id").value(customerId.toString()))
                .andExpect(jsonPath("$.originalFileName").value("doc.txt"))
                .andExpect(jsonPath("$.targetLanguage").value("en"));
    }

    @Value("${fs.root}")
    String fsRoot;
    @AfterEach
    void cleanFs() throws IOException {
        FileSystemUtils.deleteRecursively(Paths.get(fsRoot));
    }
}
