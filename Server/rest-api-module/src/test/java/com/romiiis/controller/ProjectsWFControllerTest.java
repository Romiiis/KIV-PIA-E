package com.romiiis.controller;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.IUserRepository;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.service.api.IProjectService;
import com.romiiis.service.api.IProjectWFService;
import com.romiiis.service.api.IUserService;
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
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for ProjectsWFController.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ProjectsWFControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProjectRepository projectRepository;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IProjectWFService projectWFService;

    @Autowired
    private IExecutionContextProvider callerContextProvider;


    private User customer;
    private User translator;
    private Project project;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        projectRepository.deleteAll();

        customer = userService.createNewCustomer("John Workflow", "johnwf@test.com", "custPass");
        translator = userService.createNewTranslator("Jane Workflow", "janewf@test.com", Set.of(Locale.ENGLISH), "transPass");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customer.getId().toString(), null)
        );
        callerContextProvider.setCaller(customer);

        project = projectService.createProject(Locale.ENGLISH,
                new ResourceHeader("orig.txt", "Original content".getBytes(StandardCharsets.UTF_8)));

    }

    @DisplayName("PUT /projects/{id}/translated - translator uploads translated file")
    @Test
    void uploadTranslatedContent_ok() throws Exception {
        // Translator uploads translation
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(translator.getId().toString(), null)
        );
        callerContextProvider.setCaller(translator);

        MockMultipartFile translatedFile = new MockMultipartFile(
                "file",
                "translated.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Translated content here".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/projects/" + project.getId() + "/translated")
                        .file(translatedFile)
                        .with(req -> { req.setMethod("PUT"); return req; })) // multipart → PUT
                .andExpect(status().isOk());

        // Check state
        Project updated = projectRepository.findById(project.getId());
        assertThat(updated.getTranslatedFileName()).isEqualTo("translated.txt");
    }

    @DisplayName("POST /projects/{id}/reject - customer rejects translation with feedback")
    @Test
    void rejectTranslatedContent_ok() throws Exception {
        // Translator uploads translation first
        uploadTranslatedFileAsTranslator();

        // Customer rejects translation
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customer.getId().toString(), null)
        );
        callerContextProvider.setCaller(customer);

        String feedbackJson = "{ \"text\": \"Please fix terminology.\" }";

        mockMvc.perform(post("/projects/" + project.getId() + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(feedbackJson))
                .andExpect(status().isOk());

        // Verify feedback persisted
        Project updated = projectRepository.findById(project.getId());
        assertThat(updated.getState().toString()).isEqualTo("ASSIGNED");
    }

    @DisplayName("POST /projects/{id}/approve - customer approves translation")
    @Test
    void approveTranslatedContent_ok() throws Exception {
        uploadTranslatedFileAsTranslator();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customer.getId().toString(), null)
        );
        callerContextProvider.setCaller(customer);

        mockMvc.perform(post("/projects/" + project.getId() + "/approve"))
                .andExpect(status().isOk());

        Project updated = projectRepository.findById(project.getId());
        assertThat(updated.getState().toString()).isEqualTo("APPROVED");
    }

    @DisplayName("POST /projects/{id}/close - admin closes project")
    @Test
    void closeProject_ok() throws Exception {
        uploadTranslatedFileAsTranslator();

        // Customer approves first
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customer.getId().toString(), null)
        );
        callerContextProvider.setCaller(customer);

        projectWFService.approveProject(project.getId());

        // Run close as system (simulate admin)
        User admin = userService.createNewAdmin("admin", "a@gmail.com", "adminPass");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(admin.getId().toString(), null)
        );
        callerContextProvider.setCaller(admin);


        mockMvc.perform(post("/projects/" + project.getId() + "/close"))
                .andExpect(status().isOk());

        Project closed = projectRepository.findById(project.getId());
        assertThat(closed.getState().toString()).isEqualTo("CLOSED");
    }

    private void uploadTranslatedFileAsTranslator() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(translator.getId().toString(), null)
        );
        callerContextProvider.setCaller(translator);

        projectWFService.uploadTranslatedFile(
                project.getId(),
                new ResourceHeader("translated.txt", "Translated".getBytes(StandardCharsets.UTF_8))
        );
    }


    @Value("${fs.root}")
    String fsRoot;
    @AfterEach
    void cleanFs() throws IOException {
        FileSystemUtils.deleteRecursively(Paths.get(fsRoot));
    }


}
