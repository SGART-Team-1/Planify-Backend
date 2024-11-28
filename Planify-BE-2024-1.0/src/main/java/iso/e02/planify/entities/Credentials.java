package iso.e02.planify.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Representa las credenciales de un usuario en el sistema.
 * Esta entidad está mapeada a una tabla de base de datos utilizando anotaciones JPA.
 */
@Entity
public class Credentials {

    /**
     * Identificador único de las credenciales, generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Contraseña encriptada? asociada a las credenciales.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String secretKey;

    /**
     * Obtiene el identificador único de las credenciales.
     * 
     * @return el id de las credenciales.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de las credenciales.
     * 
     * @param id el id a establecer para las credenciales.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene la contraseña encriptada asociada a las credenciales.
     * 
     * @return la contraseña encriptada.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña encriptada para las credenciales.
     * 
     * @param password la contraseña a establecer, que ya debe estar encriptada.
     */
    public void setPassword(String password) {
        this.password = password;
    }


    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

}
