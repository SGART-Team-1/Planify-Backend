package iso.e02.planify.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iso.e02.planify.entities.MeetingAttendance;
import iso.e02.planify.requests.InspectUserForMeetingsDTO;

@Repository
public interface MeetingAttendanceRepository extends JpaRepository<MeetingAttendance, Long>{
    
    @Query("""
    SELECT 
        m.id AS id, 
        m.subject AS subject, 
        m.fromDateTime AS fromDateTime, 
        m.toDateTime AS toDateTime, 
        m.allDayLong AS allDayLong, 
        ma.role AS role, 
        m.status AS status, 
        ma.invitationStatus AS invitationStatus
    FROM MeetingAttendance ma
    JOIN ma.meeting m
    WHERE ma.user.id = :userId
    """)
    List<Map<String,Object>> findAllByUserId(Long userId);

    List<MeetingAttendance> findByUserId(Long userId);

    MeetingAttendance findByMeetingIdAndUserId(Long meetingId, Long userId);




    @Query("SELECT COUNT(ma) FROM MeetingAttendance ma JOIN ma.meeting m WHERE ma.user.id = :userId AND m.status = 'ABIERTA'")
    Long countOpenedMeetings(Long userId);   

    @Query("""
        select new iso.e02.planify.requests.InspectUserForMeetingsDTO (a.id, a.name, a.surnames, a.email, m.role, m.invitationStatus, m.hasAssisted)
        From AppUser a, CommonUser c, MeetingAttendance m, Meeting me
        where a.id=c.id and c.id=m.user and m.meeting=me.id and me.id=:meetingId

    """)
    List<InspectUserForMeetingsDTO> findAttendeesForMeetingById(Long meetingId); //usado para inspecionar

    @Query("SELECT new iso.e02.planify.requests.InspectUserForMeetingsDTO(a.id, a.name, a.surnames, a.photo) FROM AppUser a WHERE a.id = :userId")
    InspectUserForMeetingsDTO findOrganizadorDetailsById(Long userId);

    @Query("""
    SELECT 
        COUNT(ma)
    FROM MeetingAttendance ma
    JOIN ma.meeting m
    WHERE ma.user.id = :userId
    AND m.fromDateTime <= :toDateTime
    AND m.toDateTime >= :fromDateTime
    AND m.status = 'ABIERTA'
    """)
    Long checkOverlap(LocalDateTime fromDateTime, LocalDateTime toDateTime, Long userId);

    @Query("""
        SELECT ma
        FROM MeetingAttendance ma
        JOIN ma.meeting m
        WHERE (ma.user.id = :userId
        AND m.fromDateTime >= :fromDateTime
        AND m.toDateTime <= :toDateTime) OR (ma.user.id = :userId AND m.allDayLong= :allDayLong)
        """)
    List<MeetingAttendance> findAttendancesByUserIdAndPeriod(LocalDateTime fromDateTime, LocalDateTime toDateTime, Long userId, Boolean allDayLong);

}
