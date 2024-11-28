package iso.e02.planify.controllers;

import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.requests.RegisterRequest;
import iso.e02.planify.services.UserServiceRegister;
import iso.e02.planify.services.ValidateUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class UserControllerRegisterTest {

    @InjectMocks
    private UserControllerRegister userController;

    @Mock
    private UserServiceRegister userService;

    @Mock
    private ValidateUserService validateUserService;

    private RegisterRequest registerRequest;
    private CommonUser commonUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerRequest = new RegisterRequest(null, null, null, null, null, null, null);
        registerRequest.setEmail("user@example.com");
        registerRequest.setPassword("SecurePassword123!");
        registerRequest.setName("John");
        registerRequest.setSurnames("Doe");

        commonUser = new CommonUser();
        commonUser.setEmail("user@example.com");
        commonUser.setActivated(false);
    }

    
    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(validateUserService.validateUserInfo(registerRequest)).thenReturn(true); 
        when(validateUserService.toCommonUser(any(RegisterRequest.class))).thenReturn(commonUser);
        
        // Act
        userController.registerUser(registerRequest);

        // Assert
        verify(validateUserService).validateUserInfo(registerRequest);
        verify(validateUserService).toCommonUser(registerRequest);
        verify(userService).register(commonUser);
    }
    

    @Test
    void testRegisterUser_InvalidUserInfo() {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user info"))
                .when(validateUserService).validateUserInfo(registerRequest);
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userController.registerUser(registerRequest);
        });

        assertEquals("Invalid user info", exception.getReason());
        verify(validateUserService).validateUserInfo(registerRequest);
        verify(validateUserService, never()).toCommonUser(registerRequest);
        verify(userService, never()).register(any());
    }

    
    @Test
    void testRegisterUser_ConversionFailure() {
        // Arrange
        when(validateUserService.validateUserInfo(registerRequest)).thenReturn(false); 
        when(validateUserService.toCommonUser(registerRequest)).thenThrow(new RuntimeException("Conversion error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.registerUser(registerRequest);
        });

        assertEquals("Conversion error", exception.getMessage());
        verify(validateUserService).validateUserInfo(registerRequest);
        verify(validateUserService).toCommonUser(registerRequest);
        verify(userService, never()).register(any());
    }
    
}
