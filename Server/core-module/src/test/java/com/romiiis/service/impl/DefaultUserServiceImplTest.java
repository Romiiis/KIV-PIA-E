package com.romiiis.service.impl;

import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.filter.UsersFilter;
import com.romiiis.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

class DefaultUserServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private DefaultUserServiceImpl userService;

    private User mockCustomer;
    private User mockTranslator;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCustomer = User.createCustomer("John", "john@test.com").withHashedPassword("hashed123");
        mockTranslator = User.createTranslator("Eva", "eva@test.com", Set.of(Locale.ENGLISH, Locale.FRENCH))
                .withHashedPassword("hashed321");
        userId = mockCustomer.getId();
    }

    @DisplayName("getUserByEmail should return user when found")
    @Test
    void getUserByEmail_shouldReturnUser_whenFound() {
        // given
        when(userRepository.getUserByEmail(mockCustomer.getEmailAddress()))
                .thenReturn(Optional.of(mockCustomer));

        // when
        User result = userService.getUserByEmail(mockCustomer.getEmailAddress());

        // then
        assert result != null;
        assert result.equals(mockCustomer);
        verify(userRepository).getUserByEmail(mockCustomer.getEmailAddress());
    }

    @DisplayName("getUserByEmail should throw UserNotFoundException when not found")
    @Test
    void getUserByEmail_shouldThrow_whenNotFound() {
        when(userRepository.getUserByEmail(mockCustomer.getEmailAddress()))
                .thenReturn(Optional.empty());

        try {
            userService.getUserByEmail(mockCustomer.getEmailAddress());
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }

    @DisplayName("createNewCustomer should create and return user successfully")
    @Test
    void createNewCustomer_shouldCreateSuccessfully() {
        when(userRepository.getUserByEmail(mockCustomer.getEmailAddress()))
                .thenReturn(Optional.of(mockCustomer));

        User result = userService.createNewCustomer(
                mockCustomer.getName(),
                mockCustomer.getEmailAddress(),
                "hashed123"
        );

        assert result != null;
        assert result.getRole() == UserRole.CUSTOMER;
        verify(userRepository).save(any(User.class));
        verify(userRepository, atLeastOnce()).getUserByEmail(mockCustomer.getEmailAddress());
    }

    @DisplayName("createNewCustomer should throw UserNotFoundException when not retrievable after save")
    @Test
    void createNewCustomer_shouldThrow_whenNotRetrievedAfterSave() {
        when(userRepository.getUserByEmail(mockCustomer.getEmailAddress()))
                .thenReturn(Optional.empty());

        try {
            userService.createNewCustomer(mockCustomer.getName(), mockCustomer.getEmailAddress(), "hashed123");
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }

    @DisplayName("createNewTranslator should create translator successfully")
    @Test
    void createNewTranslator_shouldCreateSuccessfully() {
        when(userRepository.getUserByEmail(mockTranslator.getEmailAddress()))
                .thenReturn(Optional.of(mockTranslator));

        User result = userService.createNewTranslator(
                mockTranslator.getName(),
                mockTranslator.getEmailAddress(),
                mockTranslator.getLanguages(),
                "hashed321"
        );

        assert result != null;
        assert result.getRole() == UserRole.TRANSLATOR;
        verify(userRepository).save(any(User.class));
    }

    @DisplayName("createNewTranslator should throw UserNotFoundException when not retrievable after save")
    @Test
    void createNewTranslator_shouldThrow_whenNotRetrievedAfterSave() {
        when(userRepository.getUserByEmail(mockTranslator.getEmailAddress()))
                .thenReturn(Optional.empty());

        try {
            userService.createNewTranslator(
                    mockTranslator.getName(),
                    mockTranslator.getEmailAddress(),
                    mockTranslator.getLanguages(),
                    "hashed321"
            );
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }


    @DisplayName("getUserById should return user when found")
    @Test
    void getUserById_shouldReturnUser_whenFound() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(mockCustomer));

        User result = userService.getUserById(userId);

        assert result != null;
        assert result.equals(mockCustomer);
        verify(userRepository).getUserById(userId);
    }

    @DisplayName("getUserById should throw UserNotFoundException when not found")
    @Test
    void getUserById_shouldThrow_whenNotFound() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        try {
            userService.getUserById(userId);
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }

    @DisplayName("getAllUsers should return list of users (empty)")
    @Test
    void getAllUsers_shouldReturnEmptyList() {
        var filter = new UsersFilter();
        when(userRepository.getAllUsers(filter)).thenReturn(List.of());

        var result = userService.getAllUsers(filter);

        assert result != null;
        assert result.isEmpty();
        verify(userRepository).getAllUsers(filter);
    }

    @DisplayName("getAllUsers should return non-empty list")
    @Test
    void getAllUsers_shouldReturnNonEmptyList() {
        var filter = new UsersFilter();
        when(userRepository.getAllUsers(filter)).thenReturn(List.of(mockCustomer));

        var result = userService.getAllUsers(filter);

        assert result.size() == 1;
        assert result.getFirst().equals(mockCustomer);
        verify(userRepository).getAllUsers(filter);
    }

    @DisplayName("getUsersLanguages should return translator's languages")
    @Test
    void getUsersLanguages_shouldReturnLanguages() {
        UUID translatorId = mockTranslator.getId();
        when(userRepository.getRoleById(translatorId)).thenReturn(UserRole.TRANSLATOR);
        when(userRepository.getUsersLanguages(translatorId)).thenReturn(List.copyOf(mockTranslator.getLanguages()));

        var result = userService.getUsersLanguages(translatorId);

        assert result != null;
        assert result.size() == mockTranslator.getLanguages().size();
        verify(userRepository).getUsersLanguages(translatorId);
    }

    @DisplayName("getUsersLanguages should throw IllegalArgumentException for non-translator")
    @Test
    void getUsersLanguages_shouldThrowForNonTranslator() {
        when(userRepository.getRoleById(userId)).thenReturn(UserRole.CUSTOMER);

        try {
            userService.getUsersLanguages(userId);
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException;
        }
    }

    @DisplayName("updateUserLanguages should update languages successfully")
    @Test
    void updateUserLanguages_shouldUpdateSuccessfully() {
        UUID translatorId = mockTranslator.getId();
        var newLangs = Set.of(Locale.ITALIAN, Locale.ENGLISH);
        User updatedUser = mockTranslator;
        updatedUser.setLanguages(newLangs);

        when(userRepository.getUserById(translatorId)).thenReturn(Optional.of(mockTranslator))
                .thenReturn(Optional.of(updatedUser));

        var result = userService.updateUserLanguages(translatorId, newLangs);

        assert result.equals(newLangs);
        verify(userRepository, times(2)).getUserById(translatorId);
        verify(userRepository).save(any(User.class));
    }

    @DisplayName("updateUserLanguages should throw UserNotFoundException when user not found")
    @Test
    void updateUserLanguages_shouldThrow_whenUserNotFound() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.getUserById(randomId)).thenReturn(Optional.empty());

        try {
            userService.updateUserLanguages(randomId, Set.of(Locale.JAPANESE));
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }
}
