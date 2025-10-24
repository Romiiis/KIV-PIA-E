package com.romiiis;

import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of {@link User} domain class.
 */
class UserTest {

    @Nested
    @DisplayName("createCustomer()")
    class CreateCustomerTests {

        @DisplayName("Create customer successfully")
        @Test
        void success() {
            var user = User.createCustomer("John Doe", "john.doe@example.com");

            assertAll(
                    () -> assertNotNull(user.getId()),
                    () -> assertEquals("John Doe", user.getName()),
                    () -> assertEquals("john.doe@example.com", user.getEmailAddress()),
                    () -> assertEquals(UserRole.CUSTOMER, user.getRole()),
                    () -> assertTrue(user.getLanguages().isEmpty()),
                    () -> assertNotNull(user.getCreatedAt())
            );
        }

        @DisplayName("Create customer unsuccessfully - invalid or empty name")
        @ParameterizedTest
        @NullAndEmptySource
        void invalidName(String name) {
            assertThrows(IllegalArgumentException.class,
                    () -> User.createCustomer(name, "john.doe@example.com"));
        }

        @DisplayName("Create customer unsuccessfully - invalid or empty email")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"not-an-email", "missing@domain", "no-at-symbol.com"})
        void invalidEmail(String emailAddress) {
            assertThrows(IllegalArgumentException.class,
                    () -> User.createCustomer("John Doe", emailAddress));
        }
    }

    @Nested
    @DisplayName("createTranslator()")
    class CreateTranslatorTests {

        @DisplayName("Create translator successfully")
        @Test
        void success() {
            var user = User.createTranslator(
                    "Jane Doe",
                    "jane.doe@example.com",
                    Set.of(Locale.GERMAN, Locale.FRENCH)
            );

            assertAll(
                    () -> assertNotNull(user.getId()),
                    () -> assertEquals("Jane Doe", user.getName()),
                    () -> assertEquals("jane.doe@example.com", user.getEmailAddress()),
                    () -> assertEquals(UserRole.TRANSLATOR, user.getRole()),
                    () -> assertTrue(user.getLanguages().contains(Locale.GERMAN)),
                    () -> assertTrue(user.getLanguages().contains(Locale.FRENCH)),
                    () -> assertNotNull(user.getCreatedAt())
            );
        }

        @DisplayName("Create translator unsuccessfully - empty name")
        @ParameterizedTest
        @NullAndEmptySource
        void invalidName(String name) {
            assertThrows(IllegalArgumentException.class,
                    () -> User.createTranslator(name, "translator@example.com", Set.of(Locale.ENGLISH)));
        }

        @DisplayName("Create translator unsuccessfully - invalid or empty email")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"invalid-email", "missing-at-symbol.com"})
        void invalidEmail(String emailAddress) {
            assertThrows(IllegalArgumentException.class,
                    () -> User.createTranslator("Translator", emailAddress, Set.of(Locale.ENGLISH)));
        }

        @DisplayName("Create translator unsuccessfully - empty or null languages")
        @Test
        void invalidLanguages() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> User.createTranslator("Translator", "valid@email.com", Set.of())),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> User.createTranslator("Translator", "valid@email.com", null))
            );
        }
    }

    @Nested
    @DisplayName("withHashedPassword()")
    class WithHashedPasswordTests {

        @DisplayName("should return same user instance with hashed password set")
        @Test
        void success() {
            var user = User.createCustomer("John Doe", "john.doe@example.com");

            var result = user.withHashedPassword("hashed-pass");

            assertSame(user, result);
            assertEquals("hashed-pass", result.getHashedPassword());
        }
    }
}
