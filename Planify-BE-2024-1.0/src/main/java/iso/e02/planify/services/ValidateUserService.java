package iso.e02.planify.services;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import iso.e02.planify.entities.Administrator;
import iso.e02.planify.entities.AppUser;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.requests.CreateAdminRequest;
import iso.e02.planify.requests.RegisterRequest;
import iso.e02.planify.entities.Credentials;
import iso.e02.planify.repositories.UsersRepository;

/**
 * Servicio para validar y gestionar datos de usuarios y administradores,
 * incluyendo validaciones de email, fecha de registro, contraseñas, y
 * creación de entidades de usuario con atributos específicos.
 */

@Service
public class ValidateUserService {

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private UsersRepository usersRepository;

    /**
     * Logger para la clase EmailSMTP.
     */
    private static final Logger LOGGER = Logger.getLogger(ValidateUserService.class.getName());

    private String emailFormat = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"; // Expresión regular para validar
                                                                                      // emails
    private DateTimeFormatter registrationDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Formato de fecha de
                                                                                                  // alta
    private BCryptPasswordEncoder pwdHashing = new BCryptPasswordEncoder(); // Codificador de contraseñas

    /**
     * Valida la información del usuario. Comprueba que los campos obligatorios
     * estén rellenados,
     * que el email tenga un formato válido, que las contraseñas coincidan y que la
     * contraseña sea segura.
     * 
     * @param userInfo petición https como objeto
     * 
     * @return true si pasa todas las validaciones, false en caso contrario
     */
    public boolean validateUserInfo(RegisterRequest userInfo) {
        validateRequiredFields(userInfo);
        isValidEmail(userInfo.getEmail());
        isValidRegistrationDate(userInfo.getRegistrationDate());
        doPasswordsMatch(userInfo.getPassword(), userInfo.getConfirmPassword());
        isPasswordSecure(userInfo.getPassword());
        return true;
    }

    /**
     * Valida la información del usuario en una edición. Comprueba que los campos
     * obligatorios
     * estén rellenado y que la fecha sea válida. Además, si también se cambia la
     * contraseña comprueba
     * que las contraseñas coincidan y que sea segura.
     * 
     * @param userInfo petición https como objeto
     * 
     * @return true si pasa todas las validaciones, false en caso contrario
     */
    public boolean validateUserInfoEdit(RegisterRequest userInfo) {
        // Validaciones obligatorias
        validateRequiredFieldsForEdit(userInfo);
        isValidRegistrationDate(userInfo.getRegistrationDate());

        // Validaciones opcionales. Se realizan si hay cambio en la pwd
        String pwd = userInfo.getPassword();
        if (pwd != null && !pwd.trim().isEmpty()) {
            doPasswordsMatch(pwd, userInfo.getConfirmPassword());
            isPasswordSecure(pwd);
        }
        return true;
    }

    /**
     * Valida la información del administrador. Comprueba que el email tenga un
     * formato válido, que las contraseñas coincidan y que la contraseña sea segura.
     *
     * @param adminInfo petición https como objeto
     * @return true si pasa todas las validaciones, false en caso contrario
     */

    public boolean validateAdminInfo(CreateAdminRequest adminInfo) {
        isValidEmail(adminInfo.getEmail());
        doPasswordsMatch(adminInfo.getPassword(), adminInfo.getConfirmPassword());
        isPasswordSecure(adminInfo.getPassword());
        return true;
    }

    /**
     * Valida que todos los campos requeridos no sean nulos ni estén vacíos.
     * 
     * @param userInfo petición https como objeto
     * 
     * @return true si toddo está rellenado, false en caso contrario
     */
    public boolean validateRequiredFields(RegisterRequest userInfo) {
        validateNotNull(userInfo.getName(), "nombre");
        validateNotNull(userInfo.getSurnames(), "apellidos");
        validateNotNull(userInfo.getCentre(), "centro");
        validateNotNull(userInfo.getRegistrationDate(), "fecha de alta");
        validateNotNull(userInfo.getEmail(), "email");
        validateNotNull(userInfo.getPassword(), "contraseña");
        validateNotNull(userInfo.getConfirmPassword(), "confirmación de la contraseña");
        return true;
    }

    /**
     * Valida que todos los campos requeridos en una edición no sean nulos ni estén
     * vacíos.
     * 
     * @param userInfo petición https como objeto
     * 
     * @return true si toddo está rellenado, false en caso contrario
     */
    public boolean validateRequiredFieldsForEdit(RegisterRequest userInfo) {
        validateNotNull(userInfo.getName(), "nombre");
        validateNotNull(userInfo.getSurnames(), "apellidos");
        validateNotNull(userInfo.getCentre(), "centro");
        validateNotNull(userInfo.getRegistrationDate(), "fecha de alta");
        validateNotNull(userInfo.getEmail(), "email");
        return true;
    }

    /**
     * Valida que un campo no sea nulo ni esté vacío.
     * 
     * @param field     El valor del campo.
     * @param fieldName El nombre del campo (para mensajes de error).
     */
    private void validateNotNull(String field, String fieldName) {
        if (field == null || field.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El campo \"" + fieldName + "\" es obligatorio.");
        }
    }

    /**
     * Comprueba si la fecha de alta tiene un formato válido.
     * El formato válido de una fecha de alta en Planify es yyyy-MM-dd.
     * La fecha de alta debe ser al menos en el año 2024 y anterior al día de hoy.
     * 
     * @param registrationDate la fecha de alta a comprobar.
     * @return true si la fecha de alta tiene un formato válido, false en caso
     *         contrario.
     */
    public boolean isValidRegistrationDate(String registrationDate) {
        try {
            LocalDate userDate = parseRegistrationDate(registrationDate);
            if (userDate.isBefore(LocalDate.of(2024, 1, 1))) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "La fecha de alta debe ser al menos en el año 2024.");
            }

            if (userDate.isAfter(LocalDate.now())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "La fecha de alta no puede ser posterior al día de hoy.");
            }
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La fecha de alta no tiene un formato válido. Se esperaba 'yyyy-MM-dd'.");
        }
        return true;
    }

    /**
     * Comprueba si el email proporcionado tiene un formato válido.
     * El formato válido de un email en Planify es xxx@planify.com.
     * 
     * @param email el email a comprobar.
     * @return true si el email tiene un formato válido, false en caso contrario.
     */
    public boolean isValidEmail(String email) {

        Pattern pattern = Pattern.compile(this.emailFormat); // Compilar la expresión regular
        Matcher matcher = pattern.matcher(email); // Crear un matcher a partir del email proporcionado

        // Comprobar si el email cumple con la expresión regular
        if (!matcher.matches()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El formato del email no es el correcto: \"texto@texto.texto.\"");
        }
        return true;
    }

    /**
     * Comprueba si las contraseñas coinciden.
     * 
     * @param password        contraseña
     * @param confirmPassword contraseña para verificar que coinciden
     * 
     * @return true si coinciden, false en caso contrario.
     */
    public boolean doPasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Las contraseñas no coinciden.");
        }
        return true;
    }

    /**
     * Comprueba si la contraseña proporcionada cumple con las políticas de
     * seguridad.
     * Al menos 8 caracteres, 1 minúscula, 1 mayúsculas, 1 número y 1 carácter
     * especial.
     * 
     * @param password la contraseña a comprobar.
     * @return true si la contraseña es segura, false en caso contrario.
     */
    public boolean isPasswordSecure(String password) {

        Pattern upperCasePattern = Pattern.compile("[A-ZÁÉÍÓÚÑÄËÏÖÜÀÈÌÒÙÇ]"); // Al menos una mayúscula
        Pattern lowerCasePattern = Pattern.compile("[a-záéíóúñäëïöüàèìòùç]"); // Al menos una minúscula
        Pattern digitPattern = Pattern.compile("\\d"); // Al menos un dígito
        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\":¿'¡+{}|<>_\\-/\\\\=\\[\\]`;ºª~€¬¨]"); // Al menos
                                                                                                             // un
                                                                                                             // carácter
                                                                                                             // especial

        // Verificar cada uno de los patrones
        if (password.length() < 8 || !upperCasePattern.matcher(password).find()
                || !lowerCasePattern.matcher(password).find() ||
                !digitPattern.matcher(password).find() || !specialCharPattern.matcher(password).find()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La contraseña incumple con la política de seguridad. La contraseña debe tener una mayúscula, una minúscula, un número, un carácter especial y al menos 8 caracteres.");
        }

        return true; // Si pasa todas las comprobaciones, es segura
    }

    /**
     * Devuelve el hash obtenido a partir de una contraseña.
     * 
     * @param password la contraseña
     * @return el hash de la contraseña
     */
    public String hashPassword(String password) {
        return this.pwdHashing.encode(password);
    }

    /**
     * Compara hashes de dos contraseñas.
     * 
     * @param rawPassword     la contraseña a codificar y comparar
     * @param encodedPassword el hash de la contraseña almacenada
     * 
     * @return true si los hashes coinciden, false en caso contrario
     */
    public boolean doHashesMatch(String rawPassword, String encodedPassword) {
        return this.pwdHashing.matches(rawPassword, encodedPassword);
    }

    /**
     * Crea un token UUID
     * 
     * @return token único y universal
     */
    public String createToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Obtiene un usuario común a partir de la petición HTTPS ya validada en forma
     * de objeto.
     * 
     * @param validatedUserInfo la petición HTTPS en forma de objeto ya validada en
     *                          cuanto a formato.
     * 
     * @return el usuario
     */
    public CommonUser toCommonUser(RegisterRequest validatedUserInfo) {

        CommonUser user = new CommonUser();

        user.setName(validatedUserInfo.getName());
        user.setSurnames(validatedUserInfo.getSurnames());
        user.setCentre(validatedUserInfo.getCentre());
        user.setRegistrationDate(parseRegistrationDate(validatedUserInfo.getRegistrationDate()));
        user.setDepartment(validatedUserInfo.getDepartment() == null || validatedUserInfo.getDepartment().equals("") ? null : validatedUserInfo.getDepartment());
        user.setProfile(validatedUserInfo.getProfile() == null || validatedUserInfo.getProfile().equals("") ? null : validatedUserInfo.getProfile());
        // redimensionar y comprimir la imagen antes de guardarla a 150px x 150px ya que
        // es como la mostramos en el front
        if (validatedUserInfo.getPhoto() != null) {

            try {
                user.setPhoto(resizeAndCompressImageFromBytes(validatedUserInfo.getPhoto(), 150, 150));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al redimensionar y comprimir la imagen", e);
            }
        }

        user.setEmail(validatedUserInfo.getEmail());
        if (validatedUserInfo.getPassword() != null && !validatedUserInfo.getPassword().trim().isEmpty()) {
            user.setPassword(hashPassword(validatedUserInfo.getPassword()));
        }
        return user;
    }

    /**
     * Convierte una solicitud de creación de administrador en un objeto
     * Administrator
     * 
     * @param validatedUserInfo información del administrador validada en forma de
     *                          objeto
     * @return Administrator configurado con la información proporcionada
     */

    public Administrator toAdmin(CreateAdminRequest validatedUserInfo) {
        Administrator user = new Administrator();

        user.setName(validatedUserInfo.getName());
        user.setSurnames(validatedUserInfo.getSurnames());
        user.setCentre(validatedUserInfo.getCentre());
        user.setEmail(validatedUserInfo.getEmail());
        // redimensionar y comprimir la imagen antes de guardarla a 150px x 150px ya que
        // es como la mostramos en el front
        if (validatedUserInfo.getPhoto() != null) {

            try {
                user.setPhoto(resizeAndCompressImageFromBytes(validatedUserInfo.getPhoto(), 150, 150));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al redimensionar y comprimir la imagen", e);
            }
        }

        Credentials credentials = new Credentials();
        credentials.setPassword(hashPassword(validatedUserInfo.getPassword()));
        user.setCredentials(credentials);
        user.setInternal(validatedUserInfo.getInterno());

        return user;
    }

    /**
     * Convierte una cadena en una fecha.
     * 
     * @param registrationDate fecha en formato de cadena
     * 
     * @return fecha formateada
     */
    private LocalDate parseRegistrationDate(String registrationDate) {
        return LocalDate.parse(registrationDate, this.registrationDateFormat);
    }

    public static byte[] resizeAndCompressImageFromBytes(byte[] imageBytes, int targetWidth, int targetHeight)
            throws IOException {

        if (imageBytes == null) {
            throw new IllegalArgumentException("imageBytes cannot be null");
        }
        // Leer la imagen desde los bytes
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

        // Redimensionar la imagen
        Image resizedImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        // Dibujar la imagen redimensionada en el BufferedImage
        Graphics2D g2d = bufferedResizedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();

        // Comprimir la imagen y convertirla a un byte[]
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedResizedImage, "jpg", byteArrayOutputStream); // Cambia "jpg" por el formato que necesites

        // Devolver los bytes de la imagen comprimida
        return byteArrayOutputStream.toByteArray();
    }

    public String validateJWT(String jwt) {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.replace("Bearer ", "");
        }
        try {
            return JWT.require(Algorithm.HMAC256(System.getProperty("JWT_SECRET")))
                    .build()
                    .verify(jwt)
                    .getSubject();
        } catch (com.auth0.jwt.exceptions.JWTVerificationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT no válido o expirado.");
        }
    }

    public boolean validateCommonUserExist(Long userId) {
        if (!this.manageUsersService.userExists(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe.");
        }
        return true;
    }

     public boolean validateAccess(String authorizationHeader, long userId) {
        String email = validateJWT(authorizationHeader);
        Optional<AppUser> userOpt = usersRepository.findByEmail(email);
        AppUser user = userOpt.get();

        if (user.getId() != userId && user instanceof CommonUser) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a esta información.");
        }
        return true;
    }
    
}