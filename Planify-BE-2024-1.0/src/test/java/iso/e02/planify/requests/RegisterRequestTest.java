package iso.e02.planify.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisterRequestTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String name = "John";
        String surnames = "Doe";
        String centre = "Main Office";
        String registrationDate = "2024-10-29";
        String email = "john.doe@example.com";
        String password = "securePassword123";
        String confirmPassword = "securePassword123";
        
        // Act
        RegisterRequest registerRequest = new RegisterRequest(name, surnames, centre, registrationDate, email, password, confirmPassword);

        // Assert
        assertEquals(name, registerRequest.getName());
        assertEquals(surnames, registerRequest.getSurnames());
        assertEquals(centre, registerRequest.getCentre());
        assertEquals(registrationDate, registerRequest.getRegistrationDate());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
        assertEquals(confirmPassword, registerRequest.getConfirmPassword());
    }

    @Test
    void testSetters() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest("Name", "Surnames", "Centre", "2024-10-29", "email@example.com", "password", "password");

        // Test department
        String department = "IT Department";
        registerRequest.setDepartment(department);
        assertEquals(department, registerRequest.getDepartment());

        // Test profile
        String profile = "Admin";
        registerRequest.setProfile(profile);
        assertEquals(profile, registerRequest.getProfile());
    }
}