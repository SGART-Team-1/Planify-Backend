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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;

import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.requests.RegisterRequest;
import iso.e02.planify.services.ManageUsersService;
import iso.e02.planify.services.ValidateUserService;

@ContextConfiguration(classes = {ValidateUserService.class, ManageUsersService.class, UsersRepository.class})
@SpringBootTest
public class ProfileTest {

    @Autowired
	private ValidateUserService validateUserService;

    @MockBean
    private ManageUsersService manageUsersService;

    @MockBean
    private UsersRepository usersRepository;

    // Ni nulos ni cadenas vacías en campos obligatorios: todos menos password, confirmPassword, department, profile y photo
    @ParameterizedTest
    @MethodSource("provideEditOptionalFieldsCanBeNull")
    public void testEditOptionalFieldsCanBeNull(String password, String confirmPassword, String department, String profile, byte[] photo) {
        RegisterRequest userInfo = new RegisterRequest("John", "Doe", "ESI", "2024-10-10", "john.doe1111@example.com", password, confirmPassword);
        userInfo.setDepartment(department);
        userInfo.setProfile(profile);
        userInfo.setPhoto(photo);
        Assertions.assertTrue(this.validateUserService.validateRequiredFieldsForEdit(userInfo), "Los campos opcionales pueden ser nulos o vacíos: contraseña, confirmar contraseña, departamento, perfil, foto.");
      }

    public static Stream<Arguments> provideEditOptionalFieldsCanBeNull() {

        String password = "C0ntras3ña_S3gura";
        String confirmPassword = "C0ntras3ña_S3gura";
        String department = "Department";
        String profile = "Profile";
        byte[] photo = new byte[]{1,2,3};

        return Stream.of(
            Arguments.of(null, confirmPassword, department, profile, photo),     
            Arguments.of("", confirmPassword, department, profile, photo),     
            
            Arguments.of(password, null, department, profile, photo),    
            Arguments.of(password, "", department, profile, photo),     
            
            Arguments.of(password, confirmPassword, null, profile, photo),    
            Arguments.of(password, confirmPassword, "", profile, photo),   

            Arguments.of(password, confirmPassword, department, null, photo),    
            Arguments.of(password, confirmPassword, department, "", photo), 

            Arguments.of(password, confirmPassword, department, profile, null),
            Arguments.of(password, confirmPassword, department, profile, new byte[0])
            );
    }

    @ParameterizedTest
    @MethodSource("provideEditRequiredFieldsCannotBeNullOrEmpty")
    public void testEditRequiredFieldsCannotBeNullOrEmpty(String name, String surnames, String centre, 
        String registrationDate, String email, String expectedField) {
        RegisterRequest userInfo = new RegisterRequest(name, surnames, centre, registrationDate, email, "C0ntras3ña_S3gura", "C0ntras3ña_S3gura");
        
        ResponseStatusException exception = Assertions.assertThrows(
            ResponseStatusException.class, () -> this.validateUserService.validateRequiredFieldsForEdit(userInfo), 
            "Se esperaba que se lanzara una excepción por no rellenar algún campo obligatorio: nombre, apellidos, centro, fecha de alta y email");
    
        Assertions.assertEquals("El campo \"" + expectedField + "\" es obligatorio.", exception.getReason());
    }

    public static Stream<Arguments> provideEditRequiredFieldsCannotBeNullOrEmpty() {
        String name = "John";
        String surnames = "Doe";
        String centre = "ESI";
        String registrationDate = "2024-10-10";
        String email = "john.doe1111@example.com";

        return Stream.of(
            Arguments.of(null, surnames, centre, registrationDate, email, "nombre"),     
            Arguments.of("", surnames, centre, registrationDate, email, "nombre"),     
            
            Arguments.of(name, null, centre, registrationDate, email, "apellidos"),    
            Arguments.of(name, "", centre, registrationDate, email, "apellidos"),     
            
            Arguments.of(name, surnames, null, registrationDate, email, "centro"), 
            Arguments.of(name, surnames, "", registrationDate, email, "centro"), 
            
            Arguments.of(name, surnames, centre, null, email, "fecha de alta"), 
            Arguments.of(name, surnames, centre, "", email, "fecha de alta"),  
            
            Arguments.of(name, surnames, centre, registrationDate, null, "email"), 
            Arguments.of(name, surnames, centre, registrationDate, "", "email")
            );
    }

     // Fecha de alta
     @Test
     public void testEditValidRegistrationDate() {
        String registrationDate = "2024-10-11";
        Assertions.assertTrue(this.validateUserService.isValidRegistrationDate(registrationDate), "Formato de fecha inválido. Asegúrese de que sigue el patrón yyyy-MM-dd.");
     }
 
     @ParameterizedTest
     @MethodSource("provideEditInvalidRegistrationDate")
     public void testEditInvalidRegistrationDate(String registrationDate) {
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateUserService.isValidRegistrationDate(registrationDate), "La fecha de alta se consideró válida, pero se esperaba que no lo fuera.");
     }
 
     public static Stream<Arguments> provideEditInvalidRegistrationDate() {
         return Stream.of(
             Arguments.of("12/10/2024"),    
             // Arguments.of("2024-10-12"),   // Formato correcto
             Arguments.of("12-10-2024"),
             Arguments.of("2023-10-12"), // Al menos año 2024
             // Arguments.of("2024-11-11") // HOY
             Arguments.of("2024-12-30") // Fecha posterior a hoy
             );
     }

     // Pwd1 = Pwd2
    @Test
    public void testEditPasswordsMatch() {
        String password = "securepassword123";
        String confirmPassword = "securepassword123";
        Assertions.assertTrue(this.validateUserService.doPasswordsMatch(password, confirmPassword),"Se esperaba que las contraseñas coincidieran.");
    }

    @Test
    public void testEditPasswordsDontMatch() {
        String password = "securepassword123";
        String confirmPassword = "SECUREPASSWORD123";
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateUserService.doPasswordsMatch(password, confirmPassword), "Se esperaba que las contraseñas no coincidieran.");
    }

    // Política de contraseñas
    @Test
    public void testEditSecurePassword() {
        String password = "C0ntras3ña_S3gura";
        Assertions.assertTrue(this.validateUserService.isPasswordSecure(password), "Se esperaba que la contraseña fuese segura.");
    }

    @ParameterizedTest
    @MethodSource("provideEditInsecurePassword")
    public void testEditInsecurePassword(String password) {
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateUserService.isPasswordSecure(password), "Se esperaba que la contraseña fuese insegura.");
    }

    public static Stream<Arguments> provideEditInsecurePassword() {
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


    // Validaciones superadas - Flujo sin modificar contraseña
    @Test
    public void testPasswordNullOrEmptyIsOk() {
        RegisterRequest userInfo = new RegisterRequest("John", "Doe", "ESI", "2024-10-11", "john.doe1111@example.com", null, "123");
        Assertions.assertTrue(this.validateUserService.validateUserInfoEdit(userInfo));

        userInfo.setPassword("");
        Assertions.assertTrue(this.validateUserService.validateUserInfoEdit(userInfo));
    }

    // Validaciones superadas - Flujo modificando contraseña
    @Test
    public void testValidateUserInfoEditIsOk() {
        RegisterRequest userInfo = new RegisterRequest("John", "Doe", "ESI", "2024-10-11", "john.doe1111@example.com", "C0ntras3ña_S3gura", "C0ntras3ña_S3gura");
       
        Assertions.assertTrue(this.validateUserService.validateUserInfoEdit(userInfo), "Se esperaba una petición correcta.");
    }
}
