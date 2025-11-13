package com.romiiis.controller;

import com.romiiis.domain.User;
import com.romiiis.filter.UsersFilter;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.api.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IUserService userService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    // =====================================================================
    // REGISTER
    // =====================================================================
    @DisplayName("POST /auth/register - creates new user and returns JWT tokens + cookies")
    @Test
    void registerCustomer_setsCookiesAndReturnsTokens() throws Exception {
        String json = """
                {
                  "name": "John Cookie",
                  "emailAddress": "john.cookie@test.com",
                  "password": "superpass"
                }
                """;

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        var response = result.getResponse();
        Cookie[] cookies = response.getCookies();

        assertThat(Arrays.asList(cookies))
                .extracting(Cookie::getName)
                .contains("accessToken", "refreshToken");

        assertThat(Arrays.stream(cookies)
                .filter(Cookie::isHttpOnly)
                .count()).isEqualTo(2);

        assertThat(userRepository.getAllUsers(new UsersFilter()))
                .extracting(User::getEmailAddress)
                .contains("john.cookie@test.com");
    }


    @DisplayName("POST /auth/login - logs in existing user and sets JWT cookies")
    @Test
    void loginUser_setsJwtCookies() throws Exception {
        String json = """
                {
                  "name": "Login Tester",
                  "emailAddress": "login@test.com",
                  "password": "mypassword"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();




        String loginJson = """
                {
                  "emailAddress": "login@test.com",
                  "password": "mypassword"
                }
                """;

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        Cookie[] cookies = result.getResponse().getCookies();

        assertThat(Arrays.asList(cookies))
                .extracting(Cookie::getName)
                .contains("accessToken", "refreshToken");
    }

    @DisplayName("POST /auth/refresh - issues new tokens and cookies from refresh_token")
    @Test
    void refreshToken_ok() throws Exception {
        String registerJson = """
                {
                  "name": "Refresh Tester",
                  "emailAddress": "refresh@test.com",
                  "password": "password123"
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andReturn();

        Cookie[] initialCookies = registerResult.getResponse().getCookies();

        MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
                        .cookie(initialCookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        Cookie[] refreshedCookies = refreshResult.getResponse().getCookies();
        assertThat(Arrays.asList(refreshedCookies))
                .extracting(Cookie::getName)
                .contains("accessToken", "refreshToken");
    }


    @DisplayName("POST /auth/logout - clears cookies and invalidates tokens")
    @Test
    void logout_clearsCookies() throws Exception {
        // 1️⃣ registruj uživatele
        String registerJson = """
                {
                  "name": "Logout Tester",
                  "emailAddress": "logout@test.com",
                  "password": "logout123"
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andReturn();

        Cookie[] cookies = registerResult.getResponse().getCookies();

        // 2️⃣ logout request
        MvcResult logoutResult = mockMvc.perform(post("/auth/logout")
                        .cookie(cookies))
                .andExpect(status().isNoContent())
                .andReturn();

        Cookie[] clearedCookies = logoutResult.getResponse().getCookies();

        // 3️⃣ ověř, že cookies byly smazány
        assertThat(clearedCookies).isNotEmpty();
        for (var cookie : clearedCookies) {
            assertThat(cookie.getMaxAge())
                    .as("Cookie %s should be cleared", cookie.getName())
                    .isEqualTo(0);
        }
    }
}
