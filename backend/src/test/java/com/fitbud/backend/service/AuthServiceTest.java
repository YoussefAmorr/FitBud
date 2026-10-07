package com.fitbud.backend.service;

import com.fitbud.backend.model.UserAccount;
import com.fitbud.backend.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fitbud.backend.exception.DuplicateEmailException;
import com.fitbud.backend.exception.InvalidCredentialsException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authService = new AuthService(
                userAccountRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void shouldRegisterNewUser() {

        String email = "newuser@fitbud.com";
        String password = "SecurePassword123!";
        String passwordHash = "hashed-password";

        when(userAccountRepository.existsByEmail(email))
                .thenReturn(false);

        when(passwordEncoder.encode(password))
                .thenReturn(passwordHash);

        when(userAccountRepository.save(any(UserAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount result = authService.register(
                email,
                password
        );

        assertEquals(email, result.getEmail());
        assertEquals(passwordHash, result.getPasswordHash());

        verify(userAccountRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userAccountRepository).save(any(UserAccount.class));
    }
    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {

        String email = "existing@fitbud.com";
        String password = "SecurePassword123!";

        when(userAccountRepository.existsByEmail(email))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> authService.register(email, password)
        );

        verify(userAccountRepository).existsByEmail(email);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userAccountRepository, never()).save(any(UserAccount.class));
    }
    @Test
    void shouldLoginWithValidCredentials() {

        String email = "test@fitbud.com";
        String password = "SecurePassword123!";
        String passwordHash = "hashed-password";
        String token = "test-jwt-token";

        UserAccount userAccount = new UserAccount(
                email,
                passwordHash
        );

        when(userAccountRepository.findByEmail(email))
                .thenReturn(java.util.Optional.of(userAccount));

        when(passwordEncoder.matches(password, passwordHash))
                .thenReturn(true);

        when(jwtService.generateToken(email))
                .thenReturn(token);

        String result = authService.login(
                email,
                password
        );

        assertEquals(token, result);

        verify(userAccountRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, passwordHash);
        verify(jwtService).generateToken(email);
    }
    @Test
    void shouldRejectLoginWithInvalidPassword() {

        String email = "test@fitbud.com";
        String password = "WrongPassword123!";
        String passwordHash = "hashed-password";

        UserAccount userAccount = new UserAccount(
                email,
                passwordHash
        );

        when(userAccountRepository.findByEmail(email))
                .thenReturn(java.util.Optional.of(userAccount));

        when(passwordEncoder.matches(password, passwordHash))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(email, password)
        );

        verify(userAccountRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, passwordHash);
        verify(jwtService, never()).generateToken(anyString());
    }
    @Test
    void shouldRejectLoginWhenEmailDoesNotExist() {

        String email = "missing@fitbud.com";
        String password = "SecurePassword123!";

        when(userAccountRepository.findByEmail(email))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(email, password)
        );

        verify(userAccountRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }
}