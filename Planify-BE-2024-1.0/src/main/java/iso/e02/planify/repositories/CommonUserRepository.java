package iso.e02.planify.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iso.e02.planify.entities.AppUser;
import iso.e02.planify.entities.CommonUser;

/**
 * Repositorio de Spring Data JPA para gestionar entidades {@link CommonUser}.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre los usuarios comunes.
 */
@Repository
public interface CommonUserRepository extends JpaRepository<CommonUser, Long> {

    /**
     * Consulta personalizada que recupera una lista de usuarios comunes para mostrar,
     * incluyendo información relevante como el tipo de usuario, id, nombre, apellidos, correo electrónico,
     * y el estado de activación y bloqueo del usuario.
     * La consulta une las tablas de {@link AppUser} y {@link CommonUser} basándose en el identificador del usuario.
     * 
     * @return una lista de mapas donde cada mapa contiene la información de cada usuario (claves del mapa): dtype, id, name, surnames, email, activated y blocked.
     */
    @Query("SELECT 'CommonUser' as dtype, u.id as id, u.name as name, u.surnames as surnames, u.email as email, " +
           "c.activated as activated, c.blocked as blocked " +
           "FROM AppUser u JOIN CommonUser c ON u.id = c.id")
    List<Map<String, Object>> getUserToShow();

    
    @Query("SELECT c FROM AppUser u JOIN CommonUser c ON u.id = c.id WHERE u.email = :userEmail") //cambiar, esto se usa para retornar las reuniones del usuario
    CommonUser findByEmail(String userEmail);

    CommonUser findById(long id);

}
