package iso.e02.planify.requests;

/**
 * Clase auxiliar que representa una solicitud de inicio de sesión.
 * Contiene los campos necesarios para autenticar a un usuario, como
 * el correo electrónico y la contraseña.
 */
public class LoginRequest {

    /** El correo electrónico del usuario */
    private String email;

    /** La contraseña del usuario */
    private String password;

    // Constructor
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y setters

    /**
     * Obtiene el correo electrónico del usuario.
     * 
     * @return el correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Obtiene la contraseña del usuario.
     * 
     * @return la contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

}
