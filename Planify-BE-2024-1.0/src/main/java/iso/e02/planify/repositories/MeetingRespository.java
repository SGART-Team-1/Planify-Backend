package iso.e02.planify.repositories;

import java.util.List;
import iso.e02.planify.requests.CandidateToMeetinDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iso.e02.planify.entities.Meeting;

@Repository
public interface MeetingRespository extends JpaRepository<Meeting, Long> {

    @Query("SELECT new iso.e02.planify.requests.CandidateToMeetinDTO (a.id, a.name, a.surnames, a.email, ab.fromDateTime as fromDateTimeAbsences, ab.toDateTime as toDateTimeAbsences) "
            +
            "FROM AppUser a " +
            "LEFT JOIN CommonUser c ON a.id = c.id " +
            "LEFT JOIN Absence ab ON c.id = ab.commonUser " +
            "WHERE c.activated = 'true' AND c.blocked = 'false'")
    List<CandidateToMeetinDTO> getCandidatesToMeeting();

}
