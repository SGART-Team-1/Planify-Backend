package iso.e02.planify.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    // Concrete subclass for testing purposes
    private static class TestUserRequest extends UserRequest {
        public TestUserRequest(String name, String surnames, String centre, String email, String password, String confirmPassword) {
            super(name, surnames, centre, email, password, confirmPassword);
        }
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        TestUserRequest userRequest = new TestUserRequest("John", "Doe", "Main Centre", "john.doe@example.com", "password123", "password123");

        // Act & Assert
        assertEquals("John", userRequest.getName(), "The name should be 'John'");
        assertEquals("Doe", userRequest.getSurnames(), "The surname should be 'Doe'");
        assertEquals("Main Centre", userRequest.getCentre(), "The centre should be 'Main Centre'");
        assertEquals("john.doe@example.com", userRequest.getEmail(), "The email should be 'john.doe@example.com'");
        assertEquals("password123", userRequest.getPassword(), "The password should be 'password123'");
        assertEquals("password123", userRequest.getConfirmPassword(), "The confirm password should be 'password123'");

        // Set new values
        userRequest.setName("Jane");
        userRequest.setSurnames("Smith");
        userRequest.setCentre("Secondary Centre");
        userRequest.setEmail("jane.smith@example.com");
        userRequest.setPassword("newpassword456");
        userRequest.setConfirmPassword("newpassword456");

        // Assert new values
        assertEquals("Jane", userRequest.getName(), "The name should now be 'Jane'");
        assertEquals("Smith", userRequest.getSurnames(), "The surname should now be 'Smith'");
        assertEquals("Secondary Centre", userRequest.getCentre(), "The centre should now be 'Secondary Centre'");
        assertEquals("jane.smith@example.com", userRequest.getEmail(), "The email should now be 'jane.smith@example.com'");
        assertEquals("newpassword456", userRequest.getPassword(), "The password should now be 'newpassword456'");
        assertEquals("newpassword456", userRequest.getConfirmPassword(), "The confirm password should now be 'newpassword456'");
    }

    @Test
    void testPhotoSetterGetter() {
        // Arrange
        TestUserRequest userRequest = new TestUserRequest("John", "Doe", "Main Centre", "john.doe@example.com", "password123", "password123");
        
        // Arrange photo
        byte[] photo = new byte[]{1, 2, 3, 4, 5};

        // Act
        userRequest.setPhoto(photo);

        // Assert
        assertArrayEquals(photo, userRequest.getPhoto(), "The photo should match the set byte array");
    }
}
