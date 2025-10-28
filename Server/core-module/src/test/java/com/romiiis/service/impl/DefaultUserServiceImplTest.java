package com.romiiis.service.impl;

import com.romiiis.domain.User;
import com.romiiis.domain.UserRole;
import com.romiiis.exception.NoAccessToOperateException;
import com.romiiis.exception.UserNotFoundException;
import com.romiiis.filter.UsersFilter;
import com.romiiis.repository.IUserRepository;
import com.romiiis.security.CallerContextProvider;
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

    @Mock
    private CallerContextProvider callerContextProvider;

    @InjectMocks
    private DefaultUserServiceImpl userService;

    private User admin;
    private User customer;
    private User translator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = User.createAdmin("Admin", "admin@test.com");
        customer = User.createCustomer("John", "john@test.com").withHashedPassword("hashed123");
        translator = User.createTranslator("Eva", "eva@test.com", Set.of(Locale.ENGLISH, Locale.FRENCH))
                .withHashedPassword("hashed321");
    }

    // === Pomocné metody ===

    private void asSystem() {
        when(callerContextProvider.isSystem()).thenReturn(true);
    }

    private void asUser(User user) {
        when(callerContextProvider.isSystem()).thenReturn(false);
        when(callerContextProvider.getCaller()).thenReturn(user);
    }

    // === TESTY getUserByEmail ===

    @DisplayName("ADMIN can get any user by email")
    @Test
    void adminCanGetAnyUserByEmail() {
        asUser(admin);
        when(userRepository.getUserByEmail(customer.getEmailAddress()))
                .thenReturn(Optional.of(customer));

        var result = userService.getUserByEmail(customer.getEmailAddress());
        assert result.equals(customer);
    }

    @DisplayName("CUSTOMER can get only own user data")
    @Test
    void customerCanOnlyGetOwnData() {
        asUser(customer);
        when(userRepository.getUserByEmail(customer.getEmailAddress()))
                .thenReturn(Optional.of(customer));

        var result = userService.getUserByEmail(customer.getEmailAddress());
        assert result.equals(customer);
    }

    @DisplayName("CUSTOMER cannot get other user's data")
    @Test
    void customerCannotGetOtherUserData() {
        asUser(customer);
        when(userRepository.getUserByEmail(translator.getEmailAddress()))
                .thenReturn(Optional.of(translator));

        try {
            userService.getUserByEmail(translator.getEmailAddress());
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    @DisplayName("SYSTEM mode bypasses access checks")
    @Test
    void systemModeBypassesAccessChecks() {
        asSystem();
        when(userRepository.getUserByEmail(customer.getEmailAddress()))
                .thenReturn(Optional.of(customer));

        var result = userService.getUserByEmail(customer.getEmailAddress());
        assert result.equals(customer);
    }

    // === TESTY getAllUsers ===

    @DisplayName("ADMIN can get all users")
    @Test
    void adminCanGetAllUsers() {
        asUser(admin);
        var filter = new UsersFilter();
        when(userRepository.getAllUsers(filter)).thenReturn(List.of(admin, customer, translator));

        var result = userService.getAllUsers(filter);
        assert result.size() == 3;
    }

    @DisplayName("CUSTOMER cannot get all users")
    @Test
    void customerCannotGetAllUsers() {
        asUser(customer);
        var filter = new UsersFilter();

        try {
            userService.getAllUsers(filter);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    @DisplayName("SYSTEM can get all users")
    @Test
    void systemCanGetAllUsers() {
        asSystem();
        var filter = new UsersFilter();
        when(userRepository.getAllUsers(filter)).thenReturn(List.of(admin, customer));

        var result = userService.getAllUsers(filter);
        assert result.size() == 2;
    }

    // === TESTY getUsersLanguages ===

    @DisplayName("Translator can get own languages")
    @Test
    void translatorCanGetOwnLanguages() {
        asUser(translator);
        UUID translatorId = translator.getId();
        when(userRepository.getRoleById(translatorId)).thenReturn(UserRole.TRANSLATOR);
        when(userRepository.getUsersLanguages(translatorId)).thenReturn(List.copyOf(translator.getLanguages()));

        var result = userService.getUsersLanguages(translatorId);
        assert result.size() == 2;
    }

    @DisplayName("Customer cannot get other user's languages")
    @Test
    void customerCannotGetOtherLanguages() {
        asUser(customer);
        UUID translatorId = translator.getId();
        when(userRepository.getRoleById(translatorId)).thenReturn(UserRole.TRANSLATOR);

        try {
            userService.getUsersLanguages(translatorId);
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    @DisplayName("Admin can get any user's languages")
    @Test
    void adminCanGetAnyLanguages() {
        asUser(admin);
        UUID translatorId = translator.getId();
        when(userRepository.getRoleById(translatorId)).thenReturn(UserRole.TRANSLATOR);
        when(userRepository.getUsersLanguages(translatorId)).thenReturn(List.copyOf(translator.getLanguages()));

        var result = userService.getUsersLanguages(translatorId);
        assert result.size() == 2;
    }

    @DisplayName("Throws IllegalArgumentException if user not translator")
    @Test
    void throwsIfNotTranslator() {
        asUser(admin);
        UUID customerId = customer.getId();
        when(userRepository.getRoleById(customerId)).thenReturn(UserRole.CUSTOMER);

        try {
            userService.getUsersLanguages(customerId);
            assert false;
        } catch (Exception e) {
            assert e instanceof UserNotFoundException;
        }
    }

    // === TESTY updateUserLanguages ===

    @DisplayName("Translator can update own languages")
    @Test
    void translatorCanUpdateOwnLanguages() {
        asUser(translator);
        UUID id = translator.getId();
        var newLangs = Set.of(Locale.ITALIAN);
        User updatedUser = translator;
        updatedUser.setLanguages(newLangs);

        when(userRepository.getUserById(id)).thenReturn(Optional.of(translator))
                .thenReturn(Optional.of(updatedUser));

        var result = userService.updateUserLanguages(id, newLangs);

        assert result.equals(newLangs);
        verify(userRepository).save(any(User.class));
    }

    @DisplayName("Customer cannot update translator's languages")
    @Test
    void customerCannotUpdateTranslatorLanguages() {
        asUser(customer);
        UUID id = translator.getId();

        try {
            userService.updateUserLanguages(id, Set.of(Locale.ITALIAN));
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    @DisplayName("Admin cannot update other user's languages")
    @Test
    void adminCannotUpdateOthersLanguages() {
        asUser(admin);
        UUID id = translator.getId();

        try {
            userService.updateUserLanguages(id, Set.of(Locale.ITALIAN));
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }
    }

    @DisplayName("System mode can update any user's languages")
    @Test
    void systemModeCanUpdateLanguages() {
        asSystem();
        UUID id = translator.getId();
        var newLangs = Set.of(Locale.GERMAN);
        User updatedUser = translator;
        updatedUser.setLanguages(newLangs);

        when(userRepository.getUserById(id)).thenReturn(Optional.of(translator))
                .thenReturn(Optional.of(updatedUser));

        var result = userService.updateUserLanguages(id, newLangs);
        assert result.equals(newLangs);
    }




    // === TESTY initializeUser ===

    @DisplayName("User can initialize themselves with role and languages")
    @Test
    void userCanInitializeSelf() {
        User user = User.createUser("New User", "nu@gmail.com", "hashedPass");
        asUser(user);
        UUID id = user.getId();
        Set<Locale> langs = Set.of(Locale.ENGLISH, Locale.FRENCH);
        UserRole role = UserRole.TRANSLATOR;

        when(userRepository.getUserById(id)).thenReturn(Optional.of(user));

        var result = userService.initializeUser(id, role, langs);

        assert result.equals(user);
        assert result.getRole() == role;
        assert result.getLanguages().size() == langs.size();
        verify(userRepository).save(user);
    }

    @DisplayName("Throws NoAccessToOperateException when another user tries to initialize")
    @Test
    void otherUserCannotInitialize() {
        asUser(admin);
        UUID id = customer.getId();
        when(userRepository.getUserById(id)).thenReturn(Optional.of(customer));

        try {
            userService.initializeUser(id, UserRole.CUSTOMER, Set.of(Locale.ENGLISH));
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }

        verify(userRepository, never()).save(any());
    }

    @DisplayName("Throws NoAccessToOperateException when user cannot access this operation")
    @Test
    void throwsWhenUserNotFound() {
        asUser(customer);
        UUID id = UUID.randomUUID();
        when(userRepository.getUserById(id)).thenReturn(Optional.empty());

        try {
            userService.initializeUser(id, UserRole.CUSTOMER, Set.of(Locale.ENGLISH));
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }

        verify(userRepository, never()).save(any());
    }

    @DisplayName("System mode cannot initialize user (not owner)")
    @Test
    void systemCannotInitializeUser() {
        asSystem();
        UUID id = customer.getId();
        when(userRepository.getUserById(id)).thenReturn(Optional.of(customer));

        try {
            userService.initializeUser(id, UserRole.CUSTOMER, Set.of(Locale.ENGLISH));
            assert false;
        } catch (Exception e) {
            assert e instanceof NoAccessToOperateException;
        }

        verify(userRepository, never()).save(any());
    }

    @DisplayName("User can initialize self without languages CUSTOMER")
    @Test
    void userCanInitializeWithoutLanguages() {
        User user = User.createUser("New User", "nu@gmail.com", "hashedPass");
        asUser(user);
        UUID id = user.getId();
        when(userRepository.getUserById(id)).thenReturn(Optional.of(user));

        var result = userService.initializeUser(id, UserRole.CUSTOMER, null);

        assert result.equals(user);
        assert result.getLanguages().isEmpty();
        verify(userRepository).save(user);
    }

    @DisplayName("initializeUser updates role and languages and saves user")
    @Test
    void initializeUserSavesAndUpdatesData() {
        User translator = User.createUser("Translator", "t@gmail.com", "hashedPass");
        asUser(translator);
        UUID id = translator.getId();
        Set<Locale> langs = Set.of(Locale.GERMAN, Locale.ITALIAN);
        UserRole role = UserRole.TRANSLATOR;

        when(userRepository.getUserById(id)).thenReturn(Optional.of(translator));

        var result = userService.initializeUser(id, role, langs);

        assert result.equals(translator);
        assert result.getRole() == role;
        assert result.getLanguages().equals(langs);
        verify(userRepository).save(translator);
    }

    @DisplayName("initializeUser Translator without languages")
    @Test
    void initializeUserErrorTranslatorNoLangs() {
        User translator = User.createUser("Translator", "t@gmail.com", "hashedPass");
        asUser(translator);
        UUID id = translator.getId();
        UserRole role = UserRole.TRANSLATOR;

        when(userRepository.getUserById(id)).thenReturn(Optional.of(translator));

        try {
            userService.initializeUser(id, role, Collections.emptySet());
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException;
        }

        verify(userRepository, never()).save(any());




    }



}
