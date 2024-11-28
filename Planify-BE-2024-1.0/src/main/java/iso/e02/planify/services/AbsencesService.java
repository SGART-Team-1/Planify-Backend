package iso.e02.planify.services;

// imports de iso.e02.planify.services
import iso.e02.planify.entities.Absence;
import iso.e02.planify.repositories.AbsenceRepository;
import iso.e02.planify.repositories.MeetingAttendanceRepository;
import iso.e02.planify.requests.CreateAbsenceRequest;

import java.time.LocalDateTime;
// imports de java
import java.util.List;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las ausencias de los usuarios.
 * 
 * Proporciona métodos para crear, listar y eliminar ausencias de los
 * usuarios.
 */
@Service
public class AbsencesService {

	@Autowired
	private AbsenceRepository absenceRepository; // Inyección del repositorio de ausencias

	@Autowired
	private MeetingAttendanceRepository meetingAttendanceRepository;

	public boolean checkMeetingOverlap(CreateAbsenceRequest absenceInfo) {
		LocalDateTime fromDateTime;
		LocalDateTime toDateTime;
		if (absenceInfo.isAllDayLong()) {
			fromDateTime = LocalDateTime.parse(absenceInfo.getFromDate() + "T" + "00:00");
			toDateTime = LocalDateTime.parse(absenceInfo.getToDate() + "T" + "23:59");
		}
		else {
			fromDateTime = LocalDateTime.parse(absenceInfo.getFromDate() + "T" + absenceInfo.getFromTime());
			toDateTime = LocalDateTime.parse(absenceInfo.getToDate() + "T" + absenceInfo.getToTime());
		}
		return meetingAttendanceRepository.checkOverlap(fromDateTime, toDateTime, absenceInfo.getUserId()) > 0;
	}

	/**
	 * Guarda una nueva ausencia en la base de datos.
	 */
	public void create(Absence absence) {
		absenceRepository.save(absence);
	}

	/**
	 * Lista todas las ausencias asociadas a un usuario específico.
	 * 
	 * @param userId ID del usuario cuyas ausencias se desean listar.
	 * @return una lista de ausencias asociadas al usuario.
	 */
	public List<Absence> list(Long userId) {
		return absenceRepository.findByCommonUserId(userId);
	}

	/**
	 * Elimina una ausencia específica de la base de datos.
	 * 
	 * @param absenceId ID de la ausencia que se desea eliminar.
	 */
	public void delete(Long absenceId) {
		absenceRepository.deleteById(absenceId);
	}
}