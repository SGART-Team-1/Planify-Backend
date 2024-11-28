package iso.e02.planify.requests;

/**
 * Clase que representa una solicitud para crear un nuevo administrador.
 * Extiende la clase base UserRequest e incluye campos adicionales específicos.
 */
public class CreateAdminRequest extends UserRequest{

    /** Indica si el administrador es interno o no */
    private Boolean interno;

    public CreateAdminRequest(String name, String surnames, String centre, String email, 
                              String password, String confirmPassword, Boolean interno) {
        super(name, surnames, centre, email, password, confirmPassword);
        this.interno = interno;
    }

    // Getters y Setters
    /**
     * Obtiene el estado de si el administrador es interno o no.
     * 
     * @return verdadero si el administrador es interno, falso de lo contrario.
     */
    public boolean getInterno() {
        return interno;
    }

}
