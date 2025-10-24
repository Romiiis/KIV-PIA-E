package com.romiiis.service.impl;

import com.romiiis.domain.User;
import com.romiiis.exception.EmailInUseException;
import com.romiiis.exception.InvalidAuthCredentialsException;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.interfaces.IJwtService;
import com.romiiis.service.interfaces.IPasswordHasher;
import com.romiiis.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultAuthServiceImplTest {

    @Mock
    private IUserService userService;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private IJwtService jwtService;
    @Mock
    private IPasswordHasher passwordHasher;

    @InjectMocks
    private DefaultAuthServiceImpl authService;

    private User mockCustomer;
    private User mockTranslator;
    private final String password = "pass123";
    private final String email = "test@example.com";
    private final String hashed = "hashed_pw";
    private final String jwtToken = "jwt_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCustomer = User.createCustomer("Roman", email).withHashedPassword(hashed);
        mockTranslator = User.createTranslator("Eva", "eva@example.com", Set.of(Locale.ENGLISH)).withHashedPassword(hashed);
    }


    @DisplayName("login should return JWT token when credentials are valid")
    @Test
    void login_shouldReturnJwtToken_whenCredentialsValid() {
        when(userRepository.getUserPasswordHash(email)).thenReturn(Optional.of(hashed));
        when(passwordHasher.verify(password, hashed)).thenReturn(true);
        when(userService.getUserByEmail(email)).thenReturn(mockCustomer);
        when(jwtService.generateToken(mockCustomer.getId(), mockCustomer.getRole().name())).thenReturn(jwtToken);

        String result = authService.login(email, password);

        assert result.equals(jwtToken);
        verify(passwordHasher).verify(password, hashed);
        verify(jwtService).generateToken(mockCustomer.getId(), mockCustomer.getRole().name());
    }

    @DisplayName("login should throw InvalidAuthCredentialsException when email not found")
    @Test
    void login_shouldThrow_whenEmailNotFound() {
        when(userRepository.getUserPasswordHash(email)).thenReturn(Optional.empty());

        try {
            authService.login(email, password);
            assert false;
        } catch (Exception e) {
            assert e instanceof InvalidAuthCredentialsException;
        }
    }

    @DisplayName("login should throw InvalidAuthCredentialsException when password invalid")
    @Test
    void login_shouldThrow_whenPasswordInvalid() {
        when(userRepository.getUserPasswordHash(email)).thenReturn(Optional.of(hashed));
        when(passwordHasher.verify(password, hashed)).thenReturn(false);

        try {
            authService.login(email, password);
            assert false;
        } catch (Exception e) {
            assert e instanceof InvalidAuthCredentialsException;
        }
    }


    @DisplayName("registerCustomer should create new user and return token")
    @Test
    void registerCustomer_shouldCreateUserAndReturnToken() throws Exception {
        when(userRepository.emailInUse(email)).thenReturn(false);
        when(passwordHasher.hash(password)).thenReturn(hashed);
        when(userService.createNewCustomer("Roman", email, hashed)).thenReturn(mockCustomer);
        when(jwtService.generateToken(mockCustomer.getId(), mockCustomer.getRole().name())).thenReturn(jwtToken);

        String result = authService.registerCustomer("Roman", email, password);

        assert result.equals(jwtToken);
        verify(userService).createNewCustomer("Roman", email, hashed);
        verify(jwtService).generateToken(mockCustomer.getId(), mockCustomer.getRole().name());
    }

    @DisplayName("registerCustomer should throw EmailInUseException when email already used")
    @Test
    void registerCustomer_shouldThrow_whenEmailAlreadyUsed() {
        when(userRepository.emailInUse(email)).thenReturn(true);

        try {
            authService.registerCustomer("Roman", email, password);
            assert false;
        } catch (Exception e) {
            assert e instanceof EmailInUseException;
        }
    }

    @DisplayName("registerCustomer should throw InvalidAuthCredentialsException for invalid email")
    @Test
    void registerCustomer_shouldThrow_whenEmailInvalid() {
        String badEmail = "invalidEmail";
        try {
            authService.registerCustomer("Roman", badEmail, password);
            assert false;
        } catch (Exception e) {
            assert e instanceof InvalidAuthCredentialsException;
        }
    }


    @DisplayName("registerTranslator should create translator and return token")
    @Test
    void registerTranslator_shouldCreateTranslatorAndReturnToken() throws Exception {
        Set<Locale> langs = Set.of(Locale.ENGLISH);
        when(userRepository.emailInUse(email)).thenReturn(false);
        when(passwordHasher.hash(password)).thenReturn(hashed);
        when(userService.createNewTranslator("Eva", email, langs, hashed)).thenReturn(mockTranslator);
        when(jwtService.generateToken(mockTranslator.getId(), mockTranslator.getRole().name())).thenReturn(jwtToken);

        String result = authService.registerTranslator("Eva", email, langs, password);

        assert result.equals(jwtToken);
        verify(userService).createNewTranslator("Eva", email, langs, hashed);
        verify(jwtService).generateToken(mockTranslator.getId(), mockTranslator.getRole().name());
    }

    @DisplayName("registerTranslator should throw EmailInUseException when email already in use")
    @Test
    void registerTranslator_shouldThrow_whenEmailInUse() {
        when(userRepository.emailInUse(email)).thenReturn(true);

        try {
            authService.registerTranslator("Eva", email, Set.of(Locale.ENGLISH), password);
            assert false;
        } catch (Exception e) {
            assert e instanceof EmailInUseException;
        }
    }

    @DisplayName("registerTranslator should throw InvalidAuthCredentialsException for invalid email")
    @Test
    void registerTranslator_shouldThrow_whenEmailInvalid() {
        String badEmail = "translator.gmail.com";

        try {
            authService.registerTranslator("Eva", badEmail, Set.of(Locale.ENGLISH), password);
            assert false;
        } catch (Exception e) {
            assert e instanceof InvalidAuthCredentialsException;
        }
    }
}
