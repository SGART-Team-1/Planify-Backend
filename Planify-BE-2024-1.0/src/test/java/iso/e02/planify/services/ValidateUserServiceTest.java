package iso.e02.planify.services;

import iso.e02.planify.requests.CreateAdminRequest;
import iso.e02.planify.requests.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;



class ValidateUserServiceTest {

    @InjectMocks
    private ValidateUserService validateUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateUserInfo_Valid() {
        RegisterRequest validRequest = new RegisterRequest(null, null, null, null, null, null, null);
        validRequest.setName("John");
        validRequest.setSurnames("Doe");
        validRequest.setCentre("Centre A");
        validRequest.setRegistrationDate("2024-01-01");
        validRequest.setEmail("john.doe@example.com");
        validRequest.setPassword("Secure@123");
        validRequest.setConfirmPassword("Secure@123");

        assertTrue(validateUserService.validateUserInfo(validRequest));
    }

    @Test
    void testValidateUserInfo_InvalidEmail() {
        RegisterRequest invalidEmailRequest = new RegisterRequest(null, null, null, null, null, null, null);
        invalidEmailRequest.setName("John");
        invalidEmailRequest.setSurnames("Doe");
        invalidEmailRequest.setCentre("Centre A");
        invalidEmailRequest.setRegistrationDate("2024-01-01");
        invalidEmailRequest.setEmail("invalid-email");
        invalidEmailRequest.setPassword("Secure@123");
        invalidEmailRequest.setConfirmPassword("Secure@123");

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            validateUserService.validateUserInfo(invalidEmailRequest);
        });
        assertEquals("406 NOT_ACCEPTABLE \"El formato del email no es el correcto: \"texto@texto.texto.\"\"", exception.getMessage());
    }

    @Test
    void testValidateUserInfo_PasswordsDoNotMatch() {
        RegisterRequest passwordMismatchRequest = new RegisterRequest(null, null, null, null, null, null, null);
        passwordMismatchRequest.setName("John");
        passwordMismatchRequest.setSurnames("Doe");
        passwordMismatchRequest.setCentre("Centre A");
        passwordMismatchRequest.setRegistrationDate("2024-01-01");
        passwordMismatchRequest.setEmail("john.doe@example.com");
        passwordMismatchRequest.setPassword("Secure@123");
        passwordMismatchRequest.setConfirmPassword("Different@123");

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            validateUserService.validateUserInfo(passwordMismatchRequest);
        });
        assertEquals("406 NOT_ACCEPTABLE \"Las contraseñas no coinciden.\"", exception.getMessage());
    }

    @Test
    void testValidateAdminInfo_Valid() {
        CreateAdminRequest validAdminRequest = new CreateAdminRequest(null, null, null, null, null, null, true);
        validAdminRequest.setName("Admin");
        validAdminRequest.setSurnames("User");
        validAdminRequest.setCentre("Admin Centre");
        validAdminRequest.setEmail("admin@example.com");
        validAdminRequest.setPassword("Admin@123");
        validAdminRequest.setConfirmPassword("Admin@123");

        assertTrue(validateUserService.validateAdminInfo(validAdminRequest));
    }

    @Test
    void testValidateAdminInfo_InvalidPassword() {
        CreateAdminRequest invalidAdminRequest = new CreateAdminRequest(null, null, null, null, null, null, null);
        invalidAdminRequest.setName("Admin");
        invalidAdminRequest.setSurnames("User");
        invalidAdminRequest.setCentre("Admin Centre");
        invalidAdminRequest.setEmail("admin@example.com");
        invalidAdminRequest.setPassword("short");
        invalidAdminRequest.setConfirmPassword("short");

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            validateUserService.validateAdminInfo(invalidAdminRequest);
        });
        assertEquals("406 NOT_ACCEPTABLE \"La contraseña incumple con la política de seguridad. La contraseña debe tener una mayúscula, una minúscula, un número, un carácter especial y al menos 8 caracteres.\"", exception.getMessage());
    }

    @Test
    void testHashPassword() {
        String password = "Secure@123";
        String hashedPassword = validateUserService.hashPassword(password);
        assertNotNull(hashedPassword);
        assertNotEquals(password, hashedPassword); // el hash no debe ser igual a la contraseña original
    }

    @Test
    void testDoHashesMatch() {
        String password = "Secure@123";
        String hashedPassword = validateUserService.hashPassword(password);
        assertTrue(validateUserService.doHashesMatch(password, hashedPassword));
    }

    @Test
    void testCreateToken() {
        String token = validateUserService.createToken();
        assertNotNull(token);
        assertEquals(36, token.length()); // UUID tiene 36 caracteres
    }


    
        
}
