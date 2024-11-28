package iso.e02.planify.controllers;

import iso.e02.planify.requests.LoginRequest;
import iso.e02.planify.services.UserServiceLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserControllerLoginTest {

    @InjectMocks
    private UserControllerLogin userController; // Controller to test

    @Mock
    private UserServiceLogin userService; // Mocked service

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testLoginSuccess() {
        // Given
        String testEmail = "test@example.com";
        String testPassword = "password123";
        LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
        
        assertEquals(testEmail, loginRequest.getEmail(), "El correo electrónico no coincide.");
        assertEquals(testPassword, loginRequest.getPassword(), "La contraseña no coincide.");
    }

    @Test
    void testLoginFailure() {
         // Given
         String testEmail = "test@example.com";
         String testPassword = "password123";
         LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
         
         assertEquals(testEmail, loginRequest.getEmail(), "El correo electrónico no coincide.");
         assertEquals(testPassword, loginRequest.getPassword(), "La contraseña no coincide.");

        // Mocking the service method to throw an exception
        when(userService.login(loginRequest.getEmail(), loginRequest.getPassword(), null))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales inválidas"));

        // When / Then
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userController.login(loginRequest, null);
        });

        assertEquals("403 FORBIDDEN \"Credenciales inválidas\"", exception.getMessage()); // Verify exception message
    }
}
