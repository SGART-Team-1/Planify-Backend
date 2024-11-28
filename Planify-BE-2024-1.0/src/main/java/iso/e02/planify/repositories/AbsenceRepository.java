package iso.e02.planify.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import iso.e02.planify.entities.Absence;

/**
 * Repositorio de Spring Data JPA para gestionar entidades {@link Absence}.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas sobre las ausencias de los usuarios comunes.
 */
@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    /**
     * Busca y devuelve una lista de ausencias asociadas a un usuario común específico.
     * 
     * @param usuarioNormalId el identificador del usuario común.
     * @return una lista de ausencias que pertenecen al usuario con el ID especificado.
     */
    List<Absence> findByCommonUserId(Long userId);
}
