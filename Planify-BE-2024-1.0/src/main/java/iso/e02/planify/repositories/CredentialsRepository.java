package iso.e02.planify.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iso.e02.planify.entities.Credentials;

/**
 * Repositorio de Spring Data JPA para gestionar entidades {@link Credentials}.
 * Proporciona métodos para realizar operaciones CRUD y consultas sobre las credenciales de los usuarios.
 */
@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, Long> {

    /**
     * Busca unas credenciales en la base de datos por el hash de la contraseña.
     * 
     * @param password el hash de la contraseña que se busca.
     * @return un {@link Optional} que contiene las credenciales si se encuentran, o vacío si no.
     */
    Optional<Credentials> findByPassword(String password);
}
