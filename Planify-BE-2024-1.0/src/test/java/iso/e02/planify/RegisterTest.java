package iso.e02.planify;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;

import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.requests.RegisterRequest;
import iso.e02.planify.services.ManageUsersService;
import iso.e02.planify.services.ValidateUserService;

@ContextConfiguration(classes = {ValidateUserService.class, ManageUsersService.class, UsersRepository.class})
@SpringBootTest
public class RegisterTest {

    @Autowired
	private ValidateUserService validateUserService;

    @MockBean
    private ManageUsersService manageUsersService;

    @MockBean
    private UsersRepository usersRepository;

    // Ni nulos ni cadenas vacías en campos obligatorios: todos menos department, profile y photo
    @Test
    public void testOptionalFieldsCanBeNull() {
        RegisterRequest userInfo = new RegisterRequest("John", "Doe", "ESI", "2024-10-10", "john.doe1111@example.com", "securepassword123", "securepassword123");
        Assertions.assertTrue(this.validateUserService.validateRequiredFields(userInfo), "Los campos opcionales pueden ser nulos o vacíos: departamento, perfil, foto.");
    }

    @ParameterizedTest
    @MethodSource("provideRequiredFieldsCannotBeNullOrEmpty")
    public void testRequiredFieldsCannotBeNullOrEmpty(String name, String surnames, String centre, 
        String registrationDate, String email, String password, String confirmPassword) {
        RegisterRequest userInfo = new RegisterRequest(name, surnames, centre,registrationDate, email, password, confirmPassword);
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateUserService.validateRequiredFields(userInfo), 
            "Se esperaba que se lanzara una excepción por no rellenar algún campo obligatorio: nombre, apellidos, centro, fecha de alta, email, contraseña y contraseña para confirmar.");
    }

    public static Stream<Arguments> provideRequiredFieldsCannotBeNullOrEmpty() {
        String name = "John";
        String surnames = "Doe";
        String centre = "ESI";
        String registrationDate = "2024-10-10";
        String email = "john.doe1111@example.com";
        String password = "securepassword123";

        return Stream.of(
            Arguments.of(null, surnames, centre, registrationDate, email, password, password),     
            Arguments.of("", surnames, centre, registrationDate, email, password, password),   

            Arguments.of(name, null, centre, registrationDate, email, password, password),     
            Arguments.of(name, "", centre, registrationDate, email, password, password),  

            Arguments.of(name, surnames, null, registrationDate, email, password, password),  
            Arguments.of(name, surnames, "", registrationDate, email, password, password),  

            Arguments.of(name, surnames, centre, null, email, password, password),  
            Arguments.of(name, surnames, centre, "", email, password, password),  

            Arguments.of(name, surnames, centre, registrationDate, null, password, password),  
            Arguments.of(name, surnames, centre, registrationDate, "", password, password),  
            
            Arguments.of(name, surnames, centre, registrationDate, email, null, password),  
            Arguments.of(name, surnames, centre, registrationDate, email, "", password),  

            Arguments.of(name, surnames, centre, registrationDate, email, password, null),  
            Arguments.of(name, surnames, centre, registrationDate, email, password, "")
            );
    }
    
    // Email
    @Test
    public void testValidEmail() { 
        String email = "email@planify.com";
        Assertions.assertTrue(this.validateUserService.isValidEmail(email), "Formato de email inválido. Asegúrese de que sigue el patrón xxx@planify.com.");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEmail")
    public void testInvalidEmail(String email) {
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateUserService.isValidEmail(email), "El email se consideró válido, pero se esperaba que no lo fuera.");
    }

    public static Stream<Arguments> provideInvalidEmail() {
        return Stream.of(
            Arguments.of("correoSinArroba"),            // Sin @
            Arguments.of("correoSinPunto@planify"),     // Sin .
            Arguments.of("@planify.com"),               // Sin cadena inicial: correo
            Arguments.of("correoSinDominio@.com"),      // Sin cadena intermedia: dominio
            Arguments.of("correoSinTLD@planify.")       // Sin cadena final: TLD
            );
    }

    // Fecha de alta
    @Test
    public void testValidRegistrationDate() {
        String registrationDate = "2024-10-11";
        Assertions.assertTrue(this.validateUserService.isValidRegistrationDate(registrationDate), "Formato de fecha inválido. Asegúrese de que sigue el patrón yyyy-MM-dd.");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRegistrationDate")
    public void testInvalidRegistrationDate(String registrationDate) {
       Assertions.assertThrows(ResponseStatusException.class, () -> this.validateUserService.isValidRegistrationDate(registrationDate), "La fecha de alta se consideró válida, pero se esperaba que no lo fuera.");
    }

    public static Stream<Arguments> provideInvalidRegistrationDate() {
        return Stream.of(
            Arguments.of("12/10/2024"),    
            // Arguments.of("2024-10-12"),   // Formato correcto
            Arguments.of("12-10-2024"),
            Arguments.of("2023-10-12"), // Al menos año 2024
            // Arguments.of("2024-10-29") // HOY
            Arguments.of("2024-12-30") // Fecha posterior a hoy
            );
    }

    // Pwd1 = Pwd2
    @Test
    public void testPasswordsMatch() {
        String password = "securepassword123";
        String confirmPassword = "securepassword123";
        Assertions.assertTrue(this.validateUserService.doPasswordsMatch(password, confirmPassword),"Se esperaba que las contraseñas coincidieran.");
    }

    @Test
    public void testPasswordsDontMatch() {
        String password = "securepassword123";
        String confirmPassword = "SECUREPASSWORD123";
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateUserService.doPasswordsMatch(password, confirmPassword), "Se esperaba que las contraseñas no coincidieran.");
    }

    // Política de contraseñas
    @Test
    public void testSecurePassword() {
        String password = "C0ntras3ña_S3gura";
        Assertions.assertTrue(this.validateUserService.isPasswordSecure(password), "Se esperaba que la contraseña fuese segura.");
    }

    @ParameterizedTest
    @MethodSource("provideInsecurePassword")
    public void testInsecurePassword(String password) {
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateUserService.isPasswordSecure(password), "Se esperaba que la contraseña fuese insegura.");
    }

    public static Stream<Arguments> provideInsecurePassword() {
        return Stream.of(
            Arguments.of("Co1@"),     // Menos de 8 caracteres
            Arguments.of("Cordo1@"),     // Menos de 8 caracteres - 7 caracteres
            // Arguments.of("Cordob1@"),     // Esta no falla porque tiene 8 caracteres
            Arguments.of("PEPE123@"),     // Sin minúsculas
            Arguments.of("pepe123@"),     // Sin mayúsculas
            Arguments.of("pepePEPE@"),     // Sin números
            Arguments.of("pepePEPE123")     // Sin símbolos
            );
    }

    // Petición correcta
    @Test
    public void testGoodRequest() {
        RegisterRequest userInfo = new RegisterRequest("John", "Doe", "ESI", "2024-10-10", "john.doe@planify.com", "SecurePassword123@", "SecurePassword123@");
        Assertions.assertTrue(this.validateUserService.validateUserInfo(userInfo), "Se esperaba una petición correcta.");
    }

    // Construcción de hashes de contraseñas
    @Test
    public void testSameHash() {
        String password = "SecurePassword123@";
        BCryptPasswordEncoder pwdHashing = new BCryptPasswordEncoder();
        Assertions.assertTrue(pwdHashing.matches(password, this.validateUserService.hashPassword(password)), "Se esperaba mismo hash.");
    }

    @Test
    public void testDifferentHashes() {
        String password1 = "SecurePassword123@";
        String password2 = "C0ntras3ña_S3gura";
        BCryptPasswordEncoder pwdHashing = new BCryptPasswordEncoder();
        Assertions.assertFalse(pwdHashing.matches(password2, this.validateUserService.hashPassword(password1)), "Se esperaban hashes distintos.");
    }

    // Comprobación de hashes de contraseñas
    @Test
    public void testHashesMatch() {
        String password = "C0ntras3ña_S3gura";
        String hash = "$2a$10$It0vRGKZsSMPs4E9EjYL9uZBD.GqfT5f0vUlkPnLLl3H/bQv.vaEC";
        Assertions.assertTrue(this.validateUserService.doHashesMatch(password, hash), "Se esperaba mismo hash.");
    }

    @Test
    public void testHashesDontMatch() {
        String password = "SecurePassword123@";
        String hash = "$2a$10$It0vRGKZsSMPs4E9EjYL9uZBD.GqfT5f0vUlkPnLLl3H/bQv.vaEC";
        Assertions.assertFalse(this.validateUserService.doHashesMatch(password, hash), "Se esperaban hashes distintos.");
    }
}
