package iso.e02.planify.requests;

/**
 * Clase que representa una solicitud para cambiar la contraseña de un usuario.
 * Contiene los campos necesarios para procesar la solicitud, como la contraseña nueva y el correo electrónico asociado al usuario.
 */
public class ChangePasswordRequest {

    /** La nueva contraseña del usuario */
    private String password;

    /** El correo electrónico del usuario asociado a la cuenta cuya contraseña se va a cambiar */
    private String email;

    public ChangePasswordRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y setters

    /**
     * Obtiene el correo electrónico asociado a la solicitud de cambio de contraseña.
     * 
     * @return el correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obtiene la nueva contraseña del usuario.
     * 
     * @return la nueva contraseña.
     */
    public String getPassword() {
        return password;
    }

}
