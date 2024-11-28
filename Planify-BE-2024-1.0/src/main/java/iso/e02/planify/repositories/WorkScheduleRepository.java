package iso.e02.planify.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iso.e02.planify.entities.Schedule;

/**
 * Repositorio para la entidad {Schedule}.
 * 
 * Esta interfaz proporciona métodos para la gestión de la persistencia de datos
 * de los horarios laborales en la base de datos. Extiende de {JpaRepository},
 * lo que permite operaciones CRUD básicas y consultas personalizadas para la
 * entidad Schedule.
 */

@Repository
public interface WorkScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * Busca un horario en la base de datos por su ID.
     * 
     * Este método ejecuta una consulta personalizada para obtener un horario
     * específico
     * de acuerdo a su ID, utilizando una anotación {Query}. Devuelve un objeto
     * {Optional} que contiene el horario si existe, o vacío en caso contrario.
     * 
     * La Query se suele usar para evitar inyecciones de SQL
     * 
     * @param id el identificador del horario a buscar.
     * @return un {@ Optional} que contiene el horario encontrado o vacío si no
     *         existe.
     */
    @Query("SELECT s FROM Schedule s WHERE s.id = ?1")
    Optional<Schedule> findById(int id);

}
