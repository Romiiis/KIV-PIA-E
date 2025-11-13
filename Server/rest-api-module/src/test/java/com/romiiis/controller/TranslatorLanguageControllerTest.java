package com.romiiis.controller;

import com.romiiis.domain.User;
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

import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for TranslatorLanguageController.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class TranslatorLanguageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IExecutionContextProvider callerContextProvider;

    private User translator;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        translator = userService.createNewTranslator(
                "Jane Translator",
                "jane.translator@test.com",
                Set.of(Locale.ENGLISH, Locale.GERMAN),
                "translatorPass"
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(translator.getId().toString(), null)
        );
        callerContextProvider.setCaller(translator);
    }

    @DisplayName("GET /users/{id}/languages returns list of translator languages")
    @Test
    void listUserLanguages_ok() throws Exception {
        mockMvc.perform(get("/users/" + translator.getId() + "/languages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(translator.getLanguages())
                .extracting(Locale::getLanguage)
                .containsExactlyInAnyOrder("en", "de");
    }

    @DisplayName("PUT /users/{id}/languages replaces translator languages successfully")
    @Test
    void replaceUserLanguages_ok() throws Exception {
        String jsonBody = "[\"fr\", \"es\"]";

        var mvcResult = mockMvc.perform(put("/users/" + translator.getId() + "/languages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains("fr");
        assertThat(response).contains("es");

        User updated = userRepository.getUserById(translator.getId()).orElseThrow();
        Set<Locale> updatedLangs = updated.getLanguages();
        assertThat(updatedLangs)
                .extracting(Locale::getLanguage)
                .containsExactlyInAnyOrder("fr", "es");
    }


    @DisplayName("GET /users/{id}/languages returns 404 when translator not found")
    @Test
    void listUserLanguages_notFound() throws Exception {
        userRepository.deleteAll();

        mockMvc.perform(get("/users/" + translator.getId() + "/languages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
