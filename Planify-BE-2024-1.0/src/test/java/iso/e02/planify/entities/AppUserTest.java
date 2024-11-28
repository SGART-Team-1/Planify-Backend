package iso.e02.planify.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class AppUserTest {

    private CommonUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new CommonUser();
    }

    @Test
    void testId() {
        appUser.setId(1L);
        assertEquals(1L, appUser.getId());
    }

    @Test
    void testName() {
        String name = "John";
        appUser.setName(name);
        assertEquals(name, appUser.getName());
    }

    @Test
    void testSurnames() {
        String surnames = "Doe";
        appUser.setSurnames(surnames);
        assertEquals(surnames, appUser.getSurnames());
    }

    @Test
    void testEmail() {
        String email = "johndoe@example.com";
        appUser.setEmail(email);
        assertEquals(email, appUser.getEmail());
    }

    @Test
    void testCentre() {
        String centre = "Main Campus";
        appUser.setCentre(centre);
        assertEquals(centre, appUser.getCentre());
    }

    @Test
    void testPhoto() {
        byte[] photo = {1, 2, 3, 4};
        appUser.setPhoto(photo);
        assertTrue(Arrays.equals(photo, appUser.getPhoto()));
    }

    @Test
    void testCredentials() {
        Credentials credentials = new Credentials();
        credentials.setPassword("password123");
        appUser.setCredentials(credentials);
        assertEquals(credentials, appUser.getCredentials());
        assertEquals("password123", appUser.getCredentials().getPassword());
    }

    @Test
    void testSetPassword() {
        appUser.setCredentials(new Credentials()); // Asegura que las credenciales no son null
        appUser.setPassword("newpassword");
        assertEquals("newpassword", appUser.getCredentials().getPassword());
    }

    @Test
    void testUserType() {
        assertEquals("CommonUser", appUser.getType());
    }
}
