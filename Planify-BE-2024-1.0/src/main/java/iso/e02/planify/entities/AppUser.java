package iso.e02.planify.entities;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Clase abstracta que representa un usuario en la aplicación.
 * Esta clase utiliza herencia para ser extendida por otros tipos de usuarios.
 * La estrategia de herencia es de tipo "JOINED" (las tablas estarán unidas mediante una clave externa.)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class AppUser {

    /**
     * Identificador único del usuario, generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * Nombre del usuario.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(nullable = false)
    protected String name;

    /**
     * Apellidos del usuario.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(nullable = false)
    protected String surnames;

    /**
     * Dirección de correo electrónico del usuario.
     * Este campo es obligatorio y debe ser único en la base de datos.
     */
    @Column(nullable = false, unique = true)
    protected String email;

    /**
     * Centro al que está asociado el usuario.
     * Este campo es obligatorio.
     */
    @Column(nullable = false)
    protected String centre;

    /**
     * Foto de perfil del usuario almacenada como un array de bytes.
     * Este campo es opcional y puede ser nulo.
     */
    @Lob
    @Column(name = "photo", columnDefinition = "VARBINARY(MAX)", nullable = true)
    protected byte[] photo;

    /**
     * Credenciales del usuario, asociadas mediante una relación de uno a uno.
     * Se aplican operaciones en cascada, de modo que la persistencia y eliminación
     * de las credenciales están ligadas a la del usuario.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credentialsID", referencedColumnName = "id")
    protected Credentials credentials;


    /**
     * Obtiene el tipo de usuario.
     * El tipo de usuario es el nombre simple de la clase que extiende {@code AppUser}.
     * 
     * @return el tipo de usuario.
     */
    public String getType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Obtiene el identificador único del usuario.
     * 
     * @return el id del usuario.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del usuario.
     * 
     * @param id el id a establecer para el usuario.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del usuario.
     * 
     * @return el nombre del usuario.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del usuario.
     * 
     * @param name el nombre a establecer para el usuario.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene los apellidos del usuario.
     * 
     * @return los apellidos del usuario.
     */
    public String getSurnames() {
        return surnames;
    }

    /**
     * Establece los apellidos del usuario.
     * 
     * @param surnames los apellidos a establecer para el usuario.
     */
    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * 
     * @return el correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     * 
     * @param email el correo electrónico a establecer para el usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el centro al que está asociado el usuario.
     * 
     * @return el centro del usuario.
     */
    public String getCentre() {
        return centre;
    }

    /**
     * Establece el centro al que está asociado el usuario.
     * 
     * @param centre el centro a establecer para el usuario.
     */
    public void setCentre(String centre) {
        this.centre = centre;
    }

    /**
     * Obtiene la foto de perfil del usuario como un array de bytes.
     * 
     * @return la foto de perfil del usuario.
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * Establece la foto de perfil del usuario.
     * 
     * @param photo la foto en formato de array de bytes a establecer.
     */
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    /**
     * Obtiene las credenciales del usuario.
     * 
     * @return las credenciales del usuario.
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Establece las credenciales del usuario.
     * 
     * @param credentials las credenciales a establecer para el usuario.
     */
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Obtiene la contraseña del usuario.
     * 
     * @return la contraseña del usuario.
     */
    public String getPassword() {
        return this.credentials.getPassword();
    }

    /**
     * Establece la contraseña del usuario.
     * 
     * @param password la contraseña a establecer en las credenciales.
     */
    public void setPassword(String password) {
        this.credentials.setPassword(password);
    }

   
}
