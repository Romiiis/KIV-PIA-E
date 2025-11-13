package com.romiiis.controller;

import com.romiiis.mapper.CommonMapper;
import com.romiiis.service.api.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TranslatorLanguageController extends AbstractController implements TranslatorsLanguageApi {

    /**
     * Services
     */
    private final IUserService userService;
    private final CommonMapper commonMapper;

    /**
     * Lists all language codes for the given translator.
     *
     * @param id The UUID of the translator.
     * @return A ResponseEntity containing a list of language codes.
     */
    @Override
    public ResponseEntity<List<String>> listUserLanguages(UUID id) {
        List<Locale> langs = userService.getUsersLanguages(id);
        return ResponseEntity.ok(commonMapper.mapListLocale(langs));
    }

    /**
     * Replaces the language codes for the given translator with the provided list.
     *
     * @param id          The UUID of the translator.
     * @param requestBody A list of language codes to set for the translator.
     * @return A ResponseEntity containing the updated list of language codes.
     */
    @Override
    public ResponseEntity<List<String>> replaceUserLanguages(UUID id, List<String> requestBody) {
        Set<Locale> locales = commonMapper.mapListStringToSetLocale(requestBody);
        return ResponseEntity.ok(commonMapper.mapSetLocaleToListString(userService.updateUserLanguages(id, locales)));

    }
}
