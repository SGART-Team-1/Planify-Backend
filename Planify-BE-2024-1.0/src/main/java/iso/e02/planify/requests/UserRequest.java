package iso.e02.planify.requests;

/**
 * Clase base que representa una solicitud de registro de usuario.
 * Contiene los campos comunes para registro de usuarios y administradores.
 */
public abstract class UserRequest {
    protected String name;
    protected String surnames;
    protected String centre;
    protected byte[] photo;
    protected String email;
    protected String password;
    protected String confirmPassword;

    // Constructor
    protected UserRequest(String name, String surnames, String centre, String email, String password, String confirmPassword) {
        this.name = name;
        this.surnames = surnames;
        this.centre = centre;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurnames() { return surnames; }
    public void setSurnames(String surnames) { this.surnames = surnames; }

    public String getCentre() { return centre; }
    public void setCentre(String centre) { this.centre = centre; }

    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}