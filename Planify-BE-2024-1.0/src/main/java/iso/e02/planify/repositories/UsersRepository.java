package iso.e02.planify.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iso.e02.planify.entities.AppUser;

/**
 * Repositorio de Spring Data JPA para gestionar entidades {@link AppUser}.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre los usuarios de la aplicación.
 */
@Repository
public interface UsersRepository extends JpaRepository<AppUser, Long> {

    /**
     * Busca un usuario en la base de datos por su correo electrónico.
     * 
     * @param email el correo electrónico del usuario.
     * @return un {@link Optional} que contiene el usuario si se encuentra, o vacío si no.
     */
    Optional<AppUser> findByEmail(String email);


}
