package iso.e02.planify.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdministratorTest {

    private Administrator administrator;

    @BeforeEach
    void setUp() {
        administrator = new Administrator();
    }

    @Test
    void testInternal() {
        administrator.setInternal(true);
        assertTrue(administrator.isInternal());

        administrator.setInternal(false);
        assertFalse(administrator.isInternal());
    }

    @Test
    void testNameInheritance() {
        String name = "John";
        administrator.setName(name);
        assertEquals(name, administrator.getName());
    }

    @Test
    void testEmailInheritance() {
        String email = "admin@example.com";
        administrator.setEmail(email);
        assertEquals(email, administrator.getEmail());
    }

    @Test
    void testCentreInheritance() {
        String centre = "Admin Centre";
        administrator.setCentre(centre);
        assertEquals(centre, administrator.getCentre());
    }

    @Test
    void testSurnamesInheritance() {
        String surnames = "Doe";
        administrator.setSurnames(surnames);
        assertEquals(surnames, administrator.getSurnames());
    }
}
