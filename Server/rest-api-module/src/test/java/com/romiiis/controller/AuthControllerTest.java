package com.romiiis.controller;

import com.romiiis.domain.User;
import com.romiiis.filter.UsersFilter;
import com.romiiis.repository.IUserRepository;
import com.romiiis.security.CallerContextProvider;
import com.romiiis.service.interfaces.IAuthService;
import com.romiiis.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private IAuthService authService;

    @Autowired
    private CallerContextProvider callerContextProvider;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

    }


    @DisplayName("POST /auth/register registers new customer and returns JWT")
    @Test
    void registerCustomer_ok() throws Exception {
        String json = """
                {
                  "type": "customer",
                  "name": "John Doe",
                  "emailAddress": "john.doe@test.com",
                  "password": "secure123"
                }
                """;

        var mvcResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("accessToken");

        // Verify user created in DB
        assertThat(userRepository.getAllUsers(new UsersFilter()))
                .extracting(User::getEmailAddress)
                .contains("john.doe@test.com");
    }


    @DisplayName("POST /auth/register registers new translator and returns JWT")
    @Test
    void registerTranslator_ok() throws Exception {
        String json = """
                {
                  "type": "translator",
                  "name": "Jane Translator",
                  "emailAddress": "jane.translator@test.com",
                  "password": "translatorPass",
                  "languages": ["en", "de"]
                }
                """;

        var mvcResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("accessToken");

        var user = userRepository.getAllUsers(new UsersFilter()).stream()
                .filter(u -> u.getEmailAddress().equals("jane.translator@test.com"))
                .findFirst()
                .orElseThrow();

        assertThat(user.getLanguages())
                .extracting(Locale::getLanguage)
                .containsExactlyInAnyOrder("en", "de");
    }


    @DisplayName("POST /auth/login logs in existing user and returns JWT")
    @Test
    void loginUser_ok() throws Exception {
        // Prepare user first // Register
        String json = """
                {
                  "type": "translator",
                  "name": "Jane Translator",
                  "emailAddress": "jane.translator@test.com",
                  "password": "translatorPass",
                  "languages": ["en", "de"]
                }
                """;

        var mvcResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("accessToken");


        json = """
                {
                  "emailAddress": "jane.translator@test.com",
                  "password": "translatorPass"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @DisplayName("POST /auth/login returns 401 for wrong password")
    @Test
    void loginUser_invalidPassword() throws Exception {
        // Prepare user
        userService.createNewCustomer("John Wrong", "wrong@test.com", "rightpass");

        String json = """
                {
                  "emailAddress": "wrong@test.com",
                  "password": "badpass"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("POST /auth/register returns 400 for invalid request type")
    @Test
    void registerUser_invalidType() throws Exception {
        String json = """
                {
                  "type": "unknown",
                  "name": "Unknown User",
                  "emailAddress": "unknown@test.com",
                  "password": "nopass"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError());
    }
}
