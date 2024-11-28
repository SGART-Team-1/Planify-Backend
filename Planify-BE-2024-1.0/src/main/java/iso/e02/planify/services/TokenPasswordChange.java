package iso.e02.planify.services;

/**
 * Clase que representa un token de cambio de contraseña.
 * Este token se utiliza para verificar la identidad del usuario
 * durante el proceso de recuperación de contraseña.
 */
public class TokenPasswordChange {

    // Duración del token en milisegundos (5 minutos)
    public static final int DURACION = 5 * 60 * 1000; // 5 minutes in milliseconds

    private String id; // Identificador único del token
    private long horaFin; // Marca de tiempo cuando el token caduca
    private String email; // Correo electrónico del usuario asociado al token

    /**
     * Constructor para crear un nuevo token de cambio de contraseña.
     *
     * @param id    Identificador único del token.
     * @param email Correo electrónico del usuario que solicita el cambio de
     *              contraseña.
     */
    public TokenPasswordChange(String id, String email) {
        this.id = id;
        this.email = email;
        this.horaFin = System.currentTimeMillis() + DURACION;
    }

    /**
     * Obtiene el identificador del token.
     *
     * @return Identificador del token.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del token.
     *
     * @param id Identificador del token.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el correo electrónico asociado al token.
     *
     * @return Correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico asociado al token.
     *
     * @param email Correo electrónico del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la marca de tiempo de caducidad del token.
     *
     * @return Marca de tiempo de caducidad.
     */
    public long getHoraFin() {
        return horaFin;
    }

    /**
     * Establece la marca de tiempo de caducidad del token.
     *
     * @param horaFin Marca de tiempo de caducidad.
     */

    public void setHoraFin(long horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * Verifica si el token ha caducado.
     *
     * @return true si el token ha caducado, false en caso contrario.
     */

    public boolean isCaducado() {
        return System.currentTimeMillis() > this.horaFin;
    }

    /**
     * Refresca la duración del token, extendiendo su validez.
     */

    public void refrescar() {
        this.horaFin = System.currentTimeMillis() + DURACION;
    }
}