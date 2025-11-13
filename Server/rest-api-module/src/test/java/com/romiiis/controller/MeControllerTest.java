package com.romiiis.controller;

import com.romiiis.domain.User;
import com.romiiis.repository.IUserRepository;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.service.api.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for MeController.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class MeControllerTest {

    @Autowired
    private IUserService userService;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    IExecutionContextProvider callerContextProvider;

    @Autowired
    private MockMvc mockMvc;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = userService.createNewCustomer("John Doe", "jd@gmail.com", "password123");
        userId = user.getId();

        // simulate authenticated user in context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null)
        );

        callerContextProvider.setCaller(user);
    }

    @DisplayName("GET /me return actual user")
    @Test
    void getCurrentUser_ok() throws Exception {

        mockMvc.perform(get("/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.emailAddress").value("jd@gmail.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }
}
