package iso.e02.planify.services;

import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

class UserServiceRegisterTest {

    @InjectMocks
    private UserServiceRegister userService; // Service to test

    @Mock
    private UsersRepository userRepository; // Mocked repository

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testRegisterSuccess() {
        // Given
        CommonUser user = new CommonUser();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        // No exception is thrown for a successful save operation
        // We don't need to add any specific behavior since it's a successful case
        userService.register(user);
    }

    @Test
    void testRegisterUserAlreadyExists() {
        // Given
        CommonUser user = new CommonUser();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        
        // Mocking the repository to throw DataIntegrityViolationException
        doThrow(new DataIntegrityViolationException("User already exists"))
                .when(userRepository).save(user);
        
        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.register(user);
        });

        // Verify exception message and status
        assertEquals("El usuario ya está registrado.", exception.getReason());
    }
}
