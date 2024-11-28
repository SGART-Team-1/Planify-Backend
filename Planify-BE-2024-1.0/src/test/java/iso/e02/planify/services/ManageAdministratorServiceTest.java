package iso.e02.planify.services;

import iso.e02.planify.entities.Administrator;
import iso.e02.planify.repositories.AdministratorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageAdministratorServiceTest {

    @InjectMocks
    private ManageAdministratorService manageAdministratorService;

    @Mock
    private AdministratorRepository administratorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAdministrators() {
        // Arrange
        Administrator admin1 = new Administrator();
        admin1.setId(1L);
        admin1.setName("John");
        admin1.setSurnames("Doe");
        
        Administrator admin2 = new Administrator();
        admin2.setId(2L);
        admin2.setName("Jane");
        admin2.setSurnames("Smith");

        when(administratorRepository.findAll()).thenReturn(List.of(admin1, admin2));

        // Act
        List<Administrator> result = manageAdministratorService.getAllAdministrators();

        // Assert
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getName());
        assertEquals("Jane", result.get(1).getName());
    }

    @Test
    void testGetAdminsToShow() {
        // Arrange
        Map<String, Object> adminData = Map.of("id", 1L, "name", "John Doe", "email", "john@example.com");
        when(administratorRepository.getAdminsToShow()).thenReturn(List.of(adminData));

        // Act
        List<Map<String, Object>> result = manageAdministratorService.getAdminsToShow();

        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).get("name"));
        assertEquals("john@example.com", result.get(0).get("email"));
    }

    @Test
    void testGetAdministratorById_Found() {
        // Arrange
        Administrator admin = new Administrator();
        admin.setId(1L);
        admin.setName("John");
        when(administratorRepository.findById(1L)).thenReturn(Optional.of(admin));

        // Act
        Optional<Administrator> result = manageAdministratorService.getAdministratorById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName());
    }

    @Test
    void testGetAdministratorById_NotFound() {
        // Arrange
        when(administratorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Administrator> result = manageAdministratorService.getAdministratorById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateAdministrator_Success() {
        // Arrange
        Administrator admin = new Administrator();
        admin.setName("John");
        admin.setSurnames("Doe");

        when(administratorRepository.save(any(Administrator.class))).thenReturn(admin);

        // Act
        Administrator result = manageAdministratorService.createAdministrator(admin);

        // Assert
        assertEquals("John", result.getName());
    }

    @Test
    void testCreateAdministrator_AlreadyExists() {
        // Arrange
        Administrator admin = new Administrator();
        admin.setName("John");
        admin.setSurnames("Doe");

        when(administratorRepository.save(any(Administrator.class))).thenThrow(new DataIntegrityViolationException("Violation"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            manageAdministratorService.createAdministrator(admin);
        });

        assertEquals("El usuario ya está registrado.", exception.getReason());
    }

    @Test
    void testEditAdministrator_Success() {
        // Arrange
        Administrator existingAdmin = new Administrator();
        existingAdmin.setId(1L);
        existingAdmin.setName("John");
        existingAdmin.setSurnames("Doe");

        Administrator updatedAdmin = new Administrator();
        updatedAdmin.setName("Jane");
        updatedAdmin.setSurnames("Doe");

        when(administratorRepository.findById(1L)).thenReturn(Optional.of(existingAdmin));

        // Act
        manageAdministratorService.editAdministrator(1L, updatedAdmin);

        // Assert
        assertEquals("Jane", existingAdmin.getName());
        assertEquals("Doe", existingAdmin.getSurnames());
        verify(administratorRepository, times(1)).save(existingAdmin);
    }

    @Test
    void testEditAdministrator_NotFound() {
        // Arrange
        Administrator updatedAdmin = new Administrator();
        updatedAdmin.setName("Jane");
        updatedAdmin.setSurnames("Doe");

        when(administratorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            manageAdministratorService.editAdministrator(1L, updatedAdmin);
        });

        assertEquals("Administrador no encontrado", exception.getReason());
    }

    @Test
    void testDeleteAdministrator_Success() {
        // Arrange
        when(administratorRepository.existsById(1L)).thenReturn(true);

        // Act
        manageAdministratorService.deleteAdministrator(1L);

        // Assert
        verify(administratorRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAdministrator_NotFound() {
        // Arrange
        when(administratorRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            manageAdministratorService.deleteAdministrator(1L);
        });

        assertEquals("Administrador no encontrado", exception.getReason());
    }
}
