package iso.e02.planify.controllers;

import iso.e02.planify.entities.Administrator;
import iso.e02.planify.requests.CreateAdminRequest;
import iso.e02.planify.services.ManageAdministratorService;
import iso.e02.planify.services.ValidateUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageAdministratorControllerTest {

    @InjectMocks
    private ManageAdministratorController administratorController;

    @Mock
    private ManageAdministratorService administratorService;

    @Mock
    private ValidateUserService validateUserService;

    private Administrator admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        admin = new Administrator();
        admin.setId(1L);
        admin.setName("John");
        admin.setSurnames("Doe");
        // Set other properties as necessary
    }

    @Test
    void testGetAllAdministrators() {
        // Arrange
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("name", "John"); // Clave "name" como en el mapa esperado
        List<Map<String, Object>> admins = Arrays.asList(adminData);
        when(administratorService.getAdminsToShow()).thenReturn(admins); // Mock correcto

        // Act
        ResponseEntity<List<Map<String, Object>>> response = administratorController.getAdminsToShow();
        List<Map<String, Object>> responseBody = response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody, "El cuerpo de la respuesta no debe ser nulo");
        assertEquals(1, responseBody.size());
        assertEquals("John", responseBody.get(0).get("name")); // Acceso correcto a la clave "name"
        verify(administratorService).getAdminsToShow();
    }


    @Test
    void testGetAdminsToShow() {
        // Arrange
        List<Map<String, Object>> userMaps = Arrays.asList(
            Map.of("id", 1L, "name", "John Doe")
        );
        when(administratorService.getAdminsToShow()).thenReturn(userMaps);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = administratorController.getAdminsToShow();
        List<Map<String, Object>> responseBody = response.getBody();
       
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody, "El cuerpo de la respuesta no debe ser nulo");
        assertEquals(1, responseBody.size());
        assertEquals("John Doe", responseBody.get(0).get("name"));
        verify(administratorService).getAdminsToShow();
    }

    @Test
    void testGetAdministratorById() {
        // Arrange
        when(administratorService.getAdministratorById(1L)).thenReturn(Optional.of(admin));

        // Act
        ResponseEntity<Administrator> response = administratorController.getAdministratorById(1L);
        Administrator responseBody = response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody, "El cuerpo de la respuesta no debe ser nulo");
        assertEquals("John", responseBody.getName());
        verify(administratorService).getAdministratorById(1L);
    }

    @Test
    void testGetAdministratorById_NotFound() {
        // Arrange
        when(administratorService.getAdministratorById(2L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Administrator> response = administratorController.getAdministratorById(2L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(administratorService).getAdministratorById(2L);
    }

    @Test
    void testCreateAdministrator() {
        // Arrange
        CreateAdminRequest createAdminRequest = new CreateAdminRequest(
            "John", "Doe", "Centre A", "john@example.com", "password", "password", true
        );
        when(validateUserService.validateAdminInfo(createAdminRequest)).thenReturn(true);
        when(validateUserService.toAdmin(createAdminRequest)).thenReturn(admin);

        // Act
        assertDoesNotThrow(() -> administratorController.createAdministrator(createAdminRequest));

        // Assert
        verify(validateUserService).validateAdminInfo(createAdminRequest);
        verify(validateUserService).toAdmin(createAdminRequest);
        verify(administratorService).createAdministrator(admin);
    }
}