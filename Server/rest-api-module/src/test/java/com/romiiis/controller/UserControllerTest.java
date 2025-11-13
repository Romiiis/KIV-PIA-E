package com.romiiis.controller;

import com.romiiis.domain.User;
import com.romiiis.filter.UsersFilter;
import com.romiiis.repository.IUserRepository;
import com.romiiis.port.IExecutionContextProvider;
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

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IExecutionContextProvider callerContextProvider;

    private User adminUser;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        adminUser = userService.createNewAdmin("Admin", "admin@test.com", "adminPass");
        adminId = adminUser.getId();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(adminId.toString(), null)
        );
        callerContextProvider.setCaller(adminUser);
    }

    @DisplayName("GET /users should return empty list when no users exist")
    @Test
    void listAllUsers_empty() throws Exception {
        userRepository.deleteAll();


        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @DisplayName("GET /users should return all users (admin view)")
    @Test
    void listAllUsers_shouldReturnUsers() throws Exception {
        User u1 = userService.createNewCustomer("John Doe", "john@test.com", "pass1234");
        User u2 = userService.createNewTranslator("Jane Translator", "jane@test.com",Set.of(Locale.ENGLISH, Locale.GERMAN), "pass1234");

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[?(@.emailAddress == 'john@test.com')]").exists())
                .andExpect(jsonPath("$[?(@.emailAddress == 'jane@test.com')]").exists());

        List<User> dbUsers = userRepository.getAllUsers(new UsersFilter());
        assertThat(dbUsers).hasSize(3); // +1 admin
    }

    @DisplayName("GET /users?role=TRANSLATOR should filter translators only")
    @Test
    void listAllUsers_filterByRole() throws Exception {
        userService.createNewCustomer("John Doe", "john@test.com", "pass1234");
        userService.createNewTranslator("Jane Translator", "jane@test.com", Set.of(Locale.ENGLISH),  "pass1234");

        mockMvc.perform(get("/users")
                        .param("role", "TRANSLATOR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].emailAddress").value("jane@test.com"));
    }

    @DisplayName("GET /users/{id} should return correct user details")
    @Test
    void getUserDetails_shouldReturnUser() throws Exception {
        User u = userService.createNewCustomer("Customer 1", "customer1@test.com", "securePass");

        mockMvc.perform(get("/users/" + u.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(u.getId().toString()))
                .andExpect(jsonPath("$.emailAddress").value("customer1@test.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }
}
