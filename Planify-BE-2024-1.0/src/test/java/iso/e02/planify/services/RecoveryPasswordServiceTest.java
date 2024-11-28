package iso.e02.planify.services;

import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Credentials;
import iso.e02.planify.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecoveryPasswordServiceTest {

    @InjectMocks
    private RecoveryPasswordService recoveryPasswordService;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private ValidateUserService validateUserService;

    @Mock
    private EmailSMTP emailSMTP;

    private String email = "user@example.com";
    private String token = UUID.randomUUID().toString();
    private String password = "SecurePassword123!";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    
   

    @Test
    void testSendEmail_UserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        String result = recoveryPasswordService.sendEmail(email);

        // Assert
        assertEquals("Email enviado, revise su bandeja de entrada y SPAM", result);
        assertFalse(recoveryPasswordService.tokens.containsKey(token)); // No token should be stored
    }

    @Test
    void testChangePassword_Success() {
        // Arrange
        CommonUser user = new CommonUser();
        user.setEmail(email);
        user.setCredentials(new Credentials());
        user.getCredentials().setPassword("OldPassword123!");

        TokenPasswordChange tokenPasswordChange = new TokenPasswordChange(token, email);
        recoveryPasswordService.tokens.put(token, tokenPasswordChange);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(validateUserService.isPasswordSecure(password)).thenReturn(true);
        when(validateUserService.hashPassword(password)).thenReturn("HashedPassword123!");

        // Act
        String result = recoveryPasswordService.changePassword(email, token, password);

        // Assert
        assertEquals("Contraseña cambiada con exito", result);
        assertEquals("HashedPassword123!", user.getCredentials().getPassword());
        assertFalse(recoveryPasswordService.tokens.containsKey(token)); // Verify token is removed
    }

    @Test
    void testChangePassword_TokenInvalid() {
        // Arrange
        recoveryPasswordService.tokens.put(token, new TokenPasswordChange(token, "other@example.com")); // Wrong email

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            recoveryPasswordService.changePassword(email, token, password);
        });

        assertEquals("Token inválido o caducado", exception.getReason());
    }

    @Test
    void testChangePassword_UserDoesNotExist() {
        // Arrange
        recoveryPasswordService.tokens.put(token, new TokenPasswordChange(token, email)); // Correct email
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            recoveryPasswordService.changePassword(email, token, password);
        });

        assertEquals("El usuario no existe", exception.getReason());
    }

    @Test
    void testChangePassword_UnsafePassword() {
        // Arrange
        CommonUser user = new CommonUser();
        user.setEmail(email);
        user.setCredentials(new Credentials());
        recoveryPasswordService.tokens.put(token, new TokenPasswordChange(token, email)); // Correct email
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(validateUserService.isPasswordSecure(password)).thenReturn(false); // Unsafe password

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            recoveryPasswordService.changePassword(email, token, password);
        });

        assertEquals("La contraseña no es segura", exception.getReason());
    }
}
