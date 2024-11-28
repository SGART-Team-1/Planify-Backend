package iso.e02.planify.controllers;

import iso.e02.planify.entities.AppUser;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.requests.RegisterRequest;
import iso.e02.planify.services.ManageUsersService;
import iso.e02.planify.services.MeetingService;
import iso.e02.planify.services.ValidateUserService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageUsersControllerTest {

    @Mock
    private ManageUsersService userService;

    @Mock
    private ValidateUserService validateUserService;

    @Mock
    private UsersRepository appUserRepository;

    @Mock
    private MeetingService meetingService;

    @InjectMocks
    private ManageUsersController manageUsersController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserToShow() {
        List<Map<String, Object>> mockUsers = List.of(
                Map.of("id", 1, "name", "User 1"),
                Map.of("id", 2, "name", "User 2")
        );
        when(userService.getUserToShow()).thenReturn(mockUsers);

        ResponseEntity<List<Map<String, Object>>> response = manageUsersController.getUserToShow();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUsers, response.getBody());
    }

    @Test
void testGetUserById() {
    Long userId = 1L;
    String authHeader = "Bearer mock-token";
    CommonUser mockUser = new CommonUser();
    mockUser.setId(userId);

    // Cambiar doNothing por una simulación normal
    doAnswer(invocation -> null).when(validateUserService).validateAccess(authHeader, userId);
    when(userService.getUserById(userId)).thenReturn(Optional.of(mockUser));

    ResponseEntity<CommonUser> response = manageUsersController.getUserById(authHeader, userId);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockUser, response.getBody());
}


    @Test
    void testGetUserById_NotFound() {
        Long userId = 1L;
        String authHeader = "Bearer mock-token";

        // Cambiar doNothing por una simulación normal
        doAnswer(invocation -> null).when(validateUserService).validateAccess(authHeader, userId);
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        ResponseEntity<CommonUser> response = manageUsersController.getUserById(authHeader, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void testBlockUser() {
        Long userId = 1L;

        // No se necesita doNothing para métodos void
        doAnswer(invocation -> null).when(userService).blockUser(userId);

        assertDoesNotThrow(() -> manageUsersController.blockUser(userId));
        verify(userService, times(1)).blockUser(userId);
    }


    @Test
    void testUnblockUser() {
        Long userId = 1L;

        doAnswer(invocation -> null).when(userService).unblockUser(userId);

        assertDoesNotThrow(() -> manageUsersController.unblockUser(userId));
        verify(userService, times(1)).unblockUser(userId);
    }


    @Test
    void testActivateUser() {
        Long userId = 1L;

        doAnswer(invocation -> null).when(userService).activateUser(userId);

        assertDoesNotThrow(() -> manageUsersController.activateUser(userId));
        verify(userService, times(1)).activateUser(userId);
    }

    @Test
    void testEditUser() {
        Long userId = 1L;
        RegisterRequest mockRequest = new RegisterRequest("John", "Doe", "ESI", "2024-10-10", "john.doe1111@example.com", "securepassword123", "securepassword123");
            
        CommonUser updatedUser = new CommonUser();

        // Cambiar doNothing por una simulación normal
        doAnswer(invocation -> null).when(validateUserService).validateUserInfoEdit(mockRequest);
        when(validateUserService.toCommonUser(mockRequest)).thenReturn(updatedUser);
        doAnswer(invocation -> null).when(userService).editUser(userId, updatedUser);

        assertDoesNotThrow(() -> manageUsersController.editUser(userId, mockRequest));
        verify(userService, times(1)).editUser(userId, updatedUser);
    }


    @Test
    void testDeleteUser() {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        assertDoesNotThrow(() -> manageUsersController.deleteUser(userId));
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testHasOpenedMeetings() {
        Long userId = 1L;

        when(meetingService.hasOpenedMeetings(userId)).thenReturn(true);
        doAnswer(invocation -> null).when(validateUserService).validateCommonUserExist(userId);

        ResponseEntity<Boolean> response = manageUsersController.hasOpenedMeetings(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void testValidateJWT() {
        String authHeader = "Bearer mock-token";
        String mockEmail = "user@example.com";
        AppUser mockUser = new CommonUser();
        mockUser.setEmail(mockEmail);

        when(validateUserService.validateJWT(authHeader)).thenReturn(mockEmail);
        when(appUserRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));

        ResponseEntity<AppUser> response = manageUsersController.validateJWT(authHeader);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    void testValidateJWT_Forbidden() {
        String authHeader = "Bearer mock-token";
        String mockEmail = "user@example.com";

        when(validateUserService.validateJWT(authHeader)).thenReturn(mockEmail);
        when(appUserRepository.findByEmail(mockEmail)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> manageUsersController.validateJWT(authHeader));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Usted se está intentando colar en el sistema", exception.getReason());
    }
}
