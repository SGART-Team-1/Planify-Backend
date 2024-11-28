package iso.e02.planify.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Representa un administrador en el sistema.
 * Hereda de la clase {@link AppUser}, lo que significa que comparte
 * las características generales de un usuario de la aplicación.
 */
@Entity
public class Administrator extends AppUser {

    /**
     * Indica si el administrador es interno.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(nullable = false)
    private boolean internal;

    /**
     * Constructor de la clase {@code Administrator}.
     * Inicializa las credenciales del administrador.
     */
    public Administrator() {
        this.credentials = new Credentials();
    }

    /**
     * Verifica si el administrador es interno.
     * 
     * @return true si el administrador es interno, false si es externo.
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * Establece si el administrador es interno o externo.
     * 
     * @param internal true si el administrador es interno, false si es externo.
     */
    public void setInternal(boolean internal) {
        this.internal = internal;
    }
}
