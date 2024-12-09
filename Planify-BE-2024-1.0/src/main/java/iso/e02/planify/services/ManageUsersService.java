package iso.e02.planify.services;

// imports de iso.e02.planify
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.MeetingAttendance;
import iso.e02.planify.repositories.CommonUserRepository;
import iso.e02.planify.repositories.MeetingAttendanceRepository;
import iso.e02.planify.repositories.MeetingRespository;
import iso.e02.planify.requests.CreateAbsenceRequest;

// imports de java
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// imports de jakarta
import jakarta.transaction.Transactional;

/**
 * Servicio para gestionar operaciones relacionadas con usuarios,
 * incluyendo obtener, bloquear, activar, editar y eliminar usuarios.
 */
@Service
public class ManageUsersService {
    @Autowired
    private CommonUserRepository commonUserRepository; // Inyección del repositorio de usuarios comunes

    @Autowired
    private MeetingAttendanceRepository meetingAttendanceRepository; // Inyección del repositorio de asistencia a reuniones

    @Autowired
    private MeetingRespository meetingRepository; // Inyección del repositorio de reuniones

    /**
     * Obtiene una lista de todos los usuarios.
     *
     * @return Lista de todos los usuarios.
     */
    public List<CommonUser> getAllUsers() {
        return commonUserRepository.findAll();
    }

    /**
     * Obtiene una lista de usuarios con los datos mínimos (de una query) necesarios
     * para mostrar.
     *
     * @return Lista de usuarios en un formato de mapa de datos con campos
     *         específicos.
     */
    public List<Map<String, Object>> getUserToShow() {
        return commonUserRepository.getUserToShow();
    }

    /**
     * Obtiene un usuario específico por su ID.
     *
     * @param userId ID del usuario.
     * @return Optional que contiene el usuario si se encuentra.
     */
    public Optional<CommonUser> getUserById(Long userId) {
        return commonUserRepository.findById(userId);
    }

    /**
     * Comprueba si existe un usuario específico por su ID.
     *
     * @param userId ID del usuario.
     * @return true si existe, false en caso contrario
     */
    public boolean userExists(Long userId){
        if(!commonUserRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe.");
        }
        return true;
    }
    
    /**
     * Obtiene un usuario específico por su email.
     *
     * @param email Email del usuario.
     * @return el usuario si se encuentra.
     */
    public CommonUser getUserByEmail(String email) {
        CommonUser user = commonUserRepository.findByEmail(email);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "No existe el usuario con email \"" + email + "\".");
        }
        return user;
    }

    /**
     * Bloquea a un usuario específico.
     *
     * @param userId ID del usuario a bloquear.
     * @return true si el usuario fue bloqueado exitosamente.
     */
    @Transactional
    public boolean blockUser(Long userId) {
        Optional<CommonUser> userOptional = getUserById(userId);
        if (userOptional.isPresent()) { // Validar que el usuario exista
            CommonUser user = userOptional.get();
            updateUserBlockedStatus(user, true); // Actualizar el estado de bloqueo
            cancelOrRejectMeetings(user); // Cancelar o rechazar las reuniones en las que participa dependiendo de su rol en éstas
            commonUserRepository.save(user); // Guardar los cambios en el repositorio
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se puede bloquear un usuario desconocido.");
        }
    }

    /**
     * Desbloquea a un usuario específico.
     *
     * @param userId ID del usuario a desbloquear.
     * @return true si el usuario fue desbloqueado exitosamente.
     */
    @Transactional
    public boolean unblockUser(Long userId) {
        Optional<CommonUser> userOptional = getUserById(userId);
        if (userOptional.isPresent()) { // Validar que el usuario exista
            CommonUser user = userOptional.get();
            updateUserBlockedStatus(user, false); // Actualizar el estado de bloqueo
            user.setLoginAttempts(0);
            commonUserRepository.save(user); // Guardar los cambios en el repositorio
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "No se puede desbloquear un usuario desconocido.");
        }
    }

    /**
     * Activa a un usuario específico.
     *
     * @param userId ID del usuario a activar.
     * @return true si el usuario fue activado exitosamente.
     */
    @Transactional
    public boolean activateUser(Long userId) {
        Optional<CommonUser> userOptional = getUserById(userId);
        if (userOptional.isPresent()) { // Validar que el usuario exista
            CommonUser user = userOptional.get();
            user.setActivated(true); // Activar el usuario
            commonUserRepository.save(user); // Guardar los cambios en el repositorio
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se puede activar un usuario desconocido.");
        }
    }

    /**
     * Edita la información de un usuario específico.
     *
     * @param userId      ID del usuario a editar.
     * @param updatedUser Objeto CommonUser con la nueva información.
     */
    @Transactional
    public void editUser(Long userId, CommonUser updatedUser) {
        Optional<CommonUser> userOptional = getUserById(userId);
        if (userOptional.isPresent()) { // Validar que el usuario exista
            CommonUser existingUser = userOptional.get();
            // Actualizar los atributos de AppUser (clase padre)
            existingUser.setName(updatedUser.getName());
            existingUser.setSurnames(updatedUser.getSurnames());
            existingUser.setCentre(updatedUser.getCentre());
            existingUser.setRegistrationDate(updatedUser.getRegistrationDate());
            existingUser.setDepartment(updatedUser.getDepartment());
            existingUser.setProfile(updatedUser.getProfile());
            // Si hay cambio en la pwd, se introduce en la base de datos
            String pwd = updatedUser.getPassword();
            if(pwd != null && !pwd.trim().isEmpty()) {
                existingUser.setPassword(pwd);
            }
            // Si la foto es null no se introduce en la base de datos ni como 0x
            if (updatedUser.getPhoto() == null || updatedUser.getPhoto().length == 0) {
                existingUser.setPhoto(null);
            } else {
                existingUser.setPhoto(updatedUser.getPhoto());
            }
            commonUserRepository.save(existingUser); // Guardar los cambios en el repositorio
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario a editar no existe");
        }
    }

    /**
     * Elimina a un usuario específico.
     *
     * @param userId ID del usuario a eliminar.
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (userExists(userId)) { // Validar que el usuario exista
            commonUserRepository.deleteById(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado");
        }
    }

    /**
     * Actualiza el estado de bloqueo de un usuario.
     *
     * @param user  Usuario a actualizar.
     * @param block true para bloquear al usuario, false para desbloquear.
     * @return true si el estado de bloqueo fue actualizado correctamente.
     */
    public boolean updateUserBlockedStatus(CommonUser user, boolean block) {
        if (user.isBlocked() && block) { // Validar si el usuario ya está bloqueado y se intenta bloquear de nuevo
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario ya está bloqueado.");
        } else if (!user.isBlocked() && !block) { // Validar si el usuario ya está desbloqueado y se intenta desbloquear de nuevo
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario ya está desbloqueado.");
        } else {
            user.setBlocked(block); // Actualizar el estado de bloqueo
        }
        return true;
    }

    /**
     * Cancela o rechaza las reuniones en las que participa un usuario.
     * Usado al bloquear a un usuario.
     *
     * @param user Usuario a cancelar o rechazar las reuniones.
     */
    /*  Este método no debería ir aquí. El problema es que si lo ponemos en MeetingService
    estamos creando un ciclo para Springboot (no dejará ejecutar el back).

    Básicamente, porque importaremos MeetingService en ManageUsersService y ManageUsersService en MeetingService.

    Pero hay que darle una vuelta. Ahora mismo no van los tests
    */
    public void cancelOrRejectMeetings(CommonUser user) {
        List<Map<String,Object>> meetings = meetingAttendanceRepository.findAllByUserId(user.getId()); // Obtener las reuniones en las que participa el usuario
        for (Map<String,Object> meeting : meetings) {
            Meeting meetingAux = meetingRepository.findById((Long)meeting.get("id")).get();
            MeetingAttendance meetingAttendance = meetingAttendanceRepository.findByMeetingIdAndUserId((Long)meeting.get("id"), user.getId());
            if (meetingAttendance.getRole() == MeetingAttendance.Role.ORGANIZADOR) { // Si el usuario es organizador, se cancela la reunión
                meetingAux.setStatus(Meeting.Status.CANCELADA);
                meetingRepository.save(meetingAux);
            } else { // Si el usuario es asistente, se rechaza la invitación
                meetingAttendance.setInvitationStatus(MeetingAttendance.InvitationStatus.RECHAZADA);
                meetingAttendance.setDeclineReason("Usuario bloqueado");
                meetingAttendanceRepository.save(meetingAttendance);
            }
        }
    }
    
    //ORIGINAL 
    public void cancelOrRejectMeetingsAbsence(CreateAbsenceRequest absenceInfo){
        LocalDateTime fromDateTime;
        LocalDateTime toDateTime;
        if (absenceInfo.isAllDayLong()){
            fromDateTime = LocalDateTime.parse(absenceInfo.getFromDate() + "T00:00");
            toDateTime = LocalDateTime.parse(absenceInfo.getToDate() + "T23:59");
        }
        else{
            fromDateTime = LocalDateTime.parse(absenceInfo.getFromDate() + "T" + absenceInfo.getFromTime());
            toDateTime = LocalDateTime.parse(absenceInfo.getToDate() + "T" + absenceInfo.getToTime());
        }
        List<MeetingAttendance> meetings = meetingAttendanceRepository.findAttendancesByUserIdAndPeriod(fromDateTime, toDateTime, absenceInfo.getUserId(), absenceInfo.isAllDayLong()); // Obtener las reuniones en las que participa el usuario
        for (MeetingAttendance meetingAttendance : meetings) {
            Meeting meeting = meetingAttendance.getMeeting();
            if (meetingAttendance.getRole() == MeetingAttendance.Role.ORGANIZADOR) { // Si el usuario es organizador, se cancela la reunión
                meeting.setStatus(Meeting.Status.CANCELADA);
                meetingRepository.save(meeting);
            } else { // Si el usuario es asistente, se rechaza la invitación
                meetingAttendance.setInvitationStatus(MeetingAttendance.InvitationStatus.RECHAZADA);
                meetingAttendance.setDeclineReason("Ausencia programada");
                meetingAttendanceRepository.save(meetingAttendance);
            }
        }
    }
    
    //NUEVO CON INFO
    public List<String> cancelOrRejectMeetingsAbsenceInfo(CreateAbsenceRequest absenceInfo) {
        List<String> results = new ArrayList<>();
        LocalDateTime fromDateTime;
        LocalDateTime toDateTime;

        if (absenceInfo.isAllDayLong()) {
            fromDateTime = LocalDateTime.parse(absenceInfo.getFromDate() + "T00:00");
            toDateTime = LocalDateTime.parse(absenceInfo.getToDate() + "T23:59");
        } else {
            fromDateTime = LocalDateTime.parse(absenceInfo.getFromDate() + "T" + absenceInfo.getFromTime());
            toDateTime = LocalDateTime.parse(absenceInfo.getToDate() + "T" + absenceInfo.getToTime());
        }

        List<MeetingAttendance> meetings = meetingAttendanceRepository.findAttendancesByUserIdAndPeriod(
            fromDateTime, toDateTime, absenceInfo.getUserId(), absenceInfo.isAllDayLong()
        );

        for (MeetingAttendance meetingAttendance : meetings) {
            Meeting meeting = meetingAttendance.getMeeting();
            if (meetingAttendance.getRole() == MeetingAttendance.Role.ORGANIZADOR) {
                meeting.setStatus(Meeting.Status.CANCELADA);
                meetingRepository.save(meeting);
                results.add("Se ha cancelado la reunión: " + meeting.getSubject());
            } else {
                meetingAttendance.setInvitationStatus(MeetingAttendance.InvitationStatus.RECHAZADA);
                meetingAttendance.setDeclineReason("Ausencia programada");
                meetingAttendanceRepository.save(meetingAttendance);
                results.add("Se ha rechazado la reunión: " + meeting.getSubject());
            }
        }
        return results;
    }

}