package iso.e02.planify.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.util.List;

/**
 * Representa un usuario común en la aplicación.
 * Extiende la clase {@link AppUser} para heredar las propiedades de un usuario general.
 */
@Entity
public class CommonUser extends AppUser {

    /**
     * Fecha de alta del usuario en la organización.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(nullable = false)
    private LocalDate registrationDate;

    /**
     * Departamento de la empresa al que pertenece el usuario.
     * Este campo es opcional (puede ser nulo).
     */
    @Column(nullable = true)
    private String department;

    /**
     * Perfil del usuario en la organización (por ejemplo, rol o especialidad).
     * Este campo es opcional (puede ser nulo).
     */
    @Column(nullable = true)
    private String profile;

    /**
     * Indica si la cuenta del usuario está activada.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(nullable = false)
    private boolean activated;

    /**
     * Indica si la cuenta del usuario está bloqueada.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(nullable = false)
    private boolean blocked;

    /**
     * Constructor de la clase CommonUser.
     * Inicializa las credenciales del usuario.
     */
    public CommonUser() {
        this.credentials = new Credentials();
    }

    
    @Column(name = "login_attempts")
    protected int loginAttempts = 0;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<MeetingAttendance> meetings;

    /**
     * Lista de ausencias asociadas al usuario común.
     * Se gestiona con una relación de uno a muchos.
     * Las operaciones en cascada incluyen persistencia y actualización.
     * Las ausencias huérfanas serán eliminadas.
     */
    @OneToMany(mappedBy = "commonUser", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    protected List<Absence> absences;

    /**
     * Obtiene la fecha de alta del usuario en la organización.
     * 
     * @return la fecha de alta del usuario en la organización.
     */
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Establece la fecha de alta del usuario en la organización.
     * 
     * @param registrationDate la fecha de alta del usuario en la organización a establecer.
     */
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Obtiene el departamento al que pertenece el usuario.
     * 
     * @return el departamento del usuario.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Establece el departamento al que pertenece el usuario.
     * 
     * @param department el departamento a establecer.
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Obtiene el perfil del usuario en el sistema.
     * 
     * @return el perfil del usuario.
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Establece el perfil del usuario en el sistema.
     * 
     * @param profile el perfil a establecer.
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Indica si la cuenta del usuario está activada.
     * 
     * @return true si la cuenta está activada, false en caso contrario.
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Establece el estado de activación de la cuenta del usuario.
     * 
     * @param activated true para activar la cuenta, false para desactivarla.
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /**
     * Indica si la cuenta del usuario está bloqueada.
     * 
     * @return true si la cuenta está bloqueada, false en caso contrario.
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Establece el estado de bloqueo de la cuenta del usuario.
     * 
     * @param blocked true para bloquear la cuenta, false para desbloquearla.
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

     /**
     * Obtiene el número de intentos de inicio de sesión del usuario.
     * 
     * @return el número de intentos de inicio de sesión.
     */
    public int getLoginAttempts() {
        return loginAttempts;
    }

    /**
     * Establece el número de intentos de inicio de sesión del usuario.
     * 
     * @param loginAttempts el número de intentos de inicio de sesión a establecer.
     */
    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

}
