package iso.e02.planify.requests;

/**
 * Clase que representa una solicitud de registro de usuario.
 * Extiende la clase base UserRequest e incluye campos adicionales específicos.
 */
public class RegisterRequest extends UserRequest{

    
    private String registrationDate;
    private String department;
    private String profile;

    /**
     * Constructor predeterminado que guarda los campos obligatorios de una petición HTTPS como objeto.
     *
     * @param name             el nombre del usuario.
     * @param surnames         los apellidos del usuario.
     * @param centre           el centro del usuario.
     * @param registrationDate la fecha de registro del usuario.
     * @param email            el correo electrónico del usuario.
     * @param password         la contraseña del usuario.
     * @param confirmPassword  la contraseña para confirmar del usuario.
     */
    public RegisterRequest(String name, String surnames, String centre, String registrationDate,
    String email, String password, String confirmPassword) {
        super(name, surnames, centre, email, password, confirmPassword);
        this.registrationDate = registrationDate;
    }

    /**
     * Obtiene la fecha de alta del usuario.
     *
     * @return la fecha de alta del usuario.
     */
    public String getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Establece la fecha de alta del usuario.
     *
     * @param registrationDate la fecha de alta del usuario.
     */
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Obtiene el departamento del usuario.
     *
     * @return el departamento del usuario.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Establece el departamento del usuario.
     *
     * @param department el departamento del usuario.
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Obtiene el perfil del usuario.
     *
     * @return el perfil del usuario.
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Establece el perfil del usuario.
     *
     * @param profile el perfil del usuario.
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

}
