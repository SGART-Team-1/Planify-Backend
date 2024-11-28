package iso.e02.planify.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iso.e02.planify.entities.Administrator;
import iso.e02.planify.entities.AppUser;

/**
 * Repositorio de Spring Data JPA para gestionar entidades {@link Administrator}.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre los administradores.
 */
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    /**
     * Busca un usuario en la base de datos por su correo electrónico.
     * Aunque está diseñado para administradores, retorna un {@link Optional} de {@link AppUser}.
     * 
     * @param email el correo electrónico del usuario.
     * @return un {@link Optional} que contiene el usuario si se encuentra, o vacío si no.
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * Consulta personalizada que recupera una lista de administradores para mostrar,
     * incluyendo información relevante como el tipo de usuario, id, nombre, apellidos y correo electrónico.
     * La consulta une las tablas de {@link AppUser} y {@link Administrator} basándose en el identificador del usuario.
     * 
     * @return una lista de mapas donde cada mapa contiene la información de cada usuario (claves del mapa): dtype, id, name, surnames y email.
     */
    @Query("SELECT 'Administrator' as dtype, u.id as id, u.name as name, u.surnames as surnames, u.email as email " +
           "FROM AppUser u JOIN Administrator a ON u.id = a.id")
    List<Map<String, Object>> getAdminsToShow();
}
