package iso.e02.planify.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateAdminRequestTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String name = "John";
        String surnames = "Doe";
        String centre = "IT Department";
        String email = "john.doe@example.com";
        String password = "securePassword123";
        String confirmPassword = "securePassword123";
        Boolean interno = true;

        // Act
        CreateAdminRequest createAdminRequest = new CreateAdminRequest(name, surnames, centre, email, password, confirmPassword, interno);

        // Assert
        assertEquals(name, createAdminRequest.getName());
        assertEquals(surnames, createAdminRequest.getSurnames());
        assertEquals(centre, createAdminRequest.getCentre());
        assertEquals(email, createAdminRequest.getEmail());
        assertEquals(password, createAdminRequest.getPassword());
        assertEquals(confirmPassword, createAdminRequest.getConfirmPassword());
        assertEquals(interno, createAdminRequest.getInterno());
    }
}
