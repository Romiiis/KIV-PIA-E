package com.romiiis.controller;

import com.romiiis.configuration.ResourceHeader;
import com.romiiis.domain.Feedback;
import com.romiiis.domain.Project;
import com.romiiis.domain.User;
import com.romiiis.repository.IFeedbackRepository;
import com.romiiis.repository.IProjectRepository;
import com.romiiis.repository.IUserRepository;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.service.api.IProjectService;
import com.romiiis.service.api.IProjectWFService;
import com.romiiis.service.api.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for ProjectFeedbackController.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ProjectFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProjectRepository projectRepository;

    @Autowired
    private IFeedbackRepository feedbackRepository;

    @Autowired
    private IExecutionContextProvider callerContextProvider;

    private User customer;
    private Project project;
    private Feedback feedback;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectWFService iProjectWFService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        projectRepository.deleteAll();
        feedbackRepository.deleteAll();

        customer = userService.createNewCustomer("John Feedbacker", "johnf@test.com", "pass123");
        User translator = userService.createNewTranslator("Jane Translator", "jt@gmail.com", Set.of(Locale.ENGLISH), "transPass");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customer.getId().toString(), null)
        );
        callerContextProvider.setCaller(customer);

        project = projectService.createProject( Locale.ENGLISH, new ResourceHeader("test.txt", "text/plain".getBytes()));


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(translator.getId().toString(), null)
        );
        callerContextProvider.setCaller(translator);

        iProjectWFService.uploadTranslatedFile(project.getId(), new ResourceHeader("translated.txt", "Translated content".getBytes()));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customer.getId().toString(), null)
        );
        callerContextProvider.setCaller(customer);

        iProjectWFService.rejectProject(project.getId(), "Needs improvement");

    }

    @DisplayName("GET /projects/{id}/feedback returns feedback successfully")
    @Test
    void getProjectFeedback_ok() throws Exception {
        mockMvc.perform(get("/projects/" + project.getId() + "/feedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(project.getId().toString()))
                .andExpect(jsonPath("$.text").value("Needs improvement"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @DisplayName("GET /projects/{id}/feedback returns 404 when feedback not found")
    @Test
    void getProjectFeedback_notFound() throws Exception {
        feedbackRepository.deleteAll();

        mockMvc.perform(get("/projects/" + project.getId() + "/feedback")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
