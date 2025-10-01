package com.romiiis;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of {@link User} domain class.
 */
class UserTest {
    @Nested
    class createCustomer {
        @DisplayName("Create customer successfully")
        @Test
        void success() {
            var user = User.createCustomer("John Doe", "john.doe@example.com");

            assertAll(() -> {
                assertNotNull(user.getId());
                assertEquals("John Doe", user.getName());
                assertEquals("john.doe@example.com", user.getEmailAddress());
                assertEquals(UserRole.CUSTOMER, user.getRole());
                assertTrue(user.getLanguages().isEmpty());
                assertNotNull(user.getCreatedAt());
            });
        }

        @DisplayName("Create customer unsuccessfully - invalid name")
        @ParameterizedTest
        @NullAndEmptySource
        void invalidName(String name) {
            assertThrows(IllegalArgumentException.class, () -> User.createCustomer(name, "john.doe@example.com"));
        }

        @DisplayName("Create customer unsuccessfully - invalid email address")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "not-a-valid-email-address")
        // TODO: enable this test when emailAddress validation is implemented
        void invalidEmailAddress(String emailAddress) {
            assertThrows(IllegalArgumentException.class, () -> User.createCustomer("John Doe", emailAddress));
        }
    }

    @Nested
    class createTranslator {
        @DisplayName("Create translator successfully")
        @Test
        void success() {
            var user = User.createTranslator("Jane Doe", "jane.doe@example.com", Set.of(Locale.GERMAN, Locale.FRENCH));

            assertAll(() -> {
                assertNotNull(user.getId());
                assertEquals("Jane Doe", user.getName());
                assertEquals("jane.doe@example.com", user.getEmailAddress());
                assertEquals(UserRole.TRANSLATOR, user.getRole());
                assertTrue(user.getLanguages().contains(Locale.GERMAN));
                assertTrue(user.getLanguages().contains(Locale.FRENCH));
                assertNotNull(user.getCreatedAt());
            });
        }

        // TODO: implement tests for unhappy scenarios
        @DisplayName("Create translator unsuccessfully - empty name")
        @Test
        void unsuccess_1() {
            assertThrows(IllegalArgumentException.class, () -> User.createTranslator("", "rpejs@gmail.com", Set.of(Locale.GERMAN, Locale.FRENCH)));
        }

        @DisplayName("Create translator unsuccessfully - invalid email")
        @Test
        void unsuccess_2() {
            assertThrows(IllegalArgumentException.class, () -> User.createTranslator("romisp", "rpejsgmail.com", Set.of(Locale.GERMAN, Locale.FRENCH)));
        }

        @DisplayName("Create translator unsuccessfully - empty languages")
        @Test
        void unsuccess_3() {
            assertThrows(IllegalArgumentException.class, () -> User.createTranslator("romisp", "", Set.of()));
        }

    }

    @Nested
    class createProject {

        @DisplayName("Create project successfully")
        @Test
        void success() {
            var user = User.createCustomer("John Doe", "john.doe@example.com");

            var project = user.createProject(Locale.CHINESE, "Hello World!".getBytes(StandardCharsets.UTF_8));

            assertAll(() -> {
                assertEquals(user, project.getCustomer());
                assertEquals(Locale.CHINESE, project.getTargetLanguage());
                assertNotNull(project.getSourceFile());
                assertNotNull(project.getCreatedAt());
            });
        }

        // TODO: implement tests for unhappy scenarios
        @DisplayName("Creating project unsuccessfully - user is not a customer")
        @Test
        void unsuccess_1() {
            var user = User.createTranslator("Jane Doe", "jdoe@email.com", Set.of(Locale.GERMAN, Locale.FRENCH));

            assertThrows(IllegalArgumentException.class, () -> user.createProject(Locale.CHINESE, "Hello World!".getBytes(StandardCharsets.UTF_8)));

        }

        @DisplayName("Creating project unsuccessfully - invalid source file")
        @Test
        void unsuccess_2() {
            var user = User.createCustomer("John Doe", "jdoe@email.com");
            assertThrows(IllegalArgumentException.class, () -> user.createProject(Locale.CHINESE, new byte[0]));

            // Source file is null
            assertThrows(IllegalArgumentException.class, () -> user.createProject(Locale.CHINESE, null));

            // Source file is too big
            assertThrows(IllegalArgumentException.class, () -> user.createProject(Locale.CHINESE, new byte[Project.MAX_FILE_SIZE + 1]));
        }
    }

}