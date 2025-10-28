package com.romiiis.service.impl;

import com.romiiis.domain.User;
import com.romiiis.exception.EmailInUseException;
import com.romiiis.exception.InvalidAuthCredentialsException;
import com.romiiis.repository.IUserRepository;
import com.romiiis.security.CallerContextProvider;
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
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private CallerContextProvider callerContextProvider;

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

        when(callerContextProvider.runAsSystem(any())).thenAnswer(invocation -> {
            Supplier<?> s = invocation.getArgument(0);
            return s.get();
        });
    }


    @DisplayName("login should return JWT token when credentials are valid")
    @Test
    void login_shouldReturnJwtToken_whenCredentialsValid() {
        when(userRepository.getUserPasswordHash(email)).thenReturn(Optional.of(hashed));
        when(passwordHasher.verify(password, hashed)).thenReturn(true);
        when(userService.getUserByEmail(email)).thenReturn(mockCustomer);
        when(jwtService.generateToken(mockCustomer.getId(), mockCustomer.getRole().name())).thenReturn(jwtToken);

        User result = authService.login(email, password);

        assert result.equals(mockCustomer);
        verify(passwordHasher).verify(password, hashed);
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


    @DisplayName("registerUser should create new user and return token")
    @Test
    void registerUser_shouldCreateUserAndReturnToken() throws Exception {
        when(userRepository.emailInUse(email)).thenReturn(false);
        when(passwordHasher.hash(password)).thenReturn(hashed);
        when(userService.createNewCustomer("Roman", email, hashed)).thenReturn(mockCustomer);
        when(jwtService.generateToken(mockCustomer.getId(), mockCustomer.getRole().name())).thenReturn(jwtToken);

        User result = authService.registerUser("Roman", email, password);

        assert result.equals(mockCustomer);

        verify(userService).createNewCustomer("Roman", email, hashed);
    }

    @DisplayName("registerUser should throw EmailInUseException when email already used")
    @Test
    void registerUser_shouldThrow_whenEmailAlreadyUsed() {
        when(userRepository.emailInUse(email)).thenReturn(true);

        try {
            authService.registerUser("Roman", email, password);
            assert false;
        } catch (Exception e) {
            assert e instanceof EmailInUseException;
        }
    }

    @DisplayName("registerUser should throw InvalidAuthCredentialsException for invalid email")
    @Test
    void registerUser_shouldThrow_whenEmailInvalid() {
        String badEmail = "invalidEmail";
        try {
            authService.registerUser("Roman", badEmail, password);
            assert false;
        } catch (Exception e) {
            assert e instanceof InvalidAuthCredentialsException;
        }
    }

}
