package iso.e02.planify.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iso.e02.planify.services.JWTService;
import iso.e02.planify.services.MeetingService;
import iso.e02.planify.services.ValidateMeetingService;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.MeetingAttendance;
import iso.e02.planify.requests.CandidateToMeetinDTO;
import iso.e02.planify.requests.CandidatesRequest;
import iso.e02.planify.requests.ChangeMeetingInvitationStatusRequest;
import iso.e02.planify.requests.ChangeMeetingStatusRequest;
import iso.e02.planify.requests.CreateMeetingRequest;
import iso.e02.planify.requests.InspectUserForMeetingsDTO;

@RestController
@RequestMapping("meetings")
@CrossOrigin("*")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private ValidateMeetingService validateMeetingService;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/listAll")
    public ResponseEntity<List<Map<String,Object>>> listAll(@RequestHeader("Authorization") String authorizationHeader) {
        CommonUser user = this.jwtService.getCommonUserFromJWT(authorizationHeader);
        return ResponseEntity.ok(this.meetingService.listAll(user.getId()));
    }


    @GetMapping("/{meetingId}")//usado para inspeccionar
    public  ResponseEntity<Map<String, Object>> getMeeting(@PathVariable Long meetingId) {
        return ResponseEntity.ok(this.meetingService.getMeeting(meetingId).toMap());
    }
    

    @GetMapping("/{meetingId}/attendees")//usando para inspecionar
    public ResponseEntity<List<InspectUserForMeetingsDTO>> getMeetingAttendees(@PathVariable Long meetingId) {
        List<InspectUserForMeetingsDTO> meetingAttendances = this.meetingService.getMeetingAttendances(meetingId);
        return ResponseEntity.ok(meetingAttendances);
    }

    @GetMapping("/{userId}/organizador")//usando para inspecionar
    public ResponseEntity<InspectUserForMeetingsDTO> getOrganizador(@PathVariable Long userId) {
        InspectUserForMeetingsDTO meetingAttendances = this.meetingService.findOrganizadorDetailsById(userId);
        return ResponseEntity.ok(meetingAttendances);
    }

    @GetMapping("/{meetingId}/attendStatus")//obtiene si el usuario asistio a la reunion o la acepto
    public ResponseEntity<Map<String, String>> attendStatus(@RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long meetingId) {

            CommonUser user = jwtService.getCommonUserFromJWT(authorizationHeader);
            Map<String, String> response = new HashMap<>();
            response.put("hasAssisted", String.valueOf(this.meetingService.hasAssisted(user, meetingId)));
            response.put("hasAccepted", String.valueOf(this.meetingService.hasAccepted(user, meetingId)));
            response.put("ActulUserId", String.valueOf(user.getId()));

            return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public void create(@RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateMeetingRequest meetingInfo) {
        
        // Validar la información de la reunión
        this.validateMeetingService.validateMeetingInfo(meetingInfo);

        // Obtención de la reunión y el organizador
        Meeting meeting = this.validateMeetingService.toMeeting(meetingInfo);
        CommonUser organizer = jwtService.getCommonUserFromJWT(authorizationHeader);

        // Validación del organizador y la lista de participantes (solo que sean
        // usuarios activos)
        this.validateMeetingService.validateParticipants(meeting, organizer, meetingInfo.getParticipants());

        // Creación de la reunión
        this.meetingService.create(meeting, organizer, meetingInfo.getParticipants());  
    }
    
    @PatchMapping("/{meetingId}/edit")
    public void editMeeting(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long meetingId,
            @RequestBody CreateMeetingRequest meetingInfo) {
        // Comprobar que la reunión existe
        Meeting existingMeeting = this.meetingService.getMeeting(meetingId);

        // Validar la información de la reunión modificada
        this.validateMeetingService.validateMeetingInfo(meetingInfo);

        // Generación de la reunión modificada y obtención del organizador
        Meeting updatedMeeting = this.validateMeetingService.toMeeting(meetingInfo);
        CommonUser organizer = jwtService.getCommonUserFromJWT(authorizationHeader);

        // Validación de la lista de participantes, incluido el organizador
        updatedMeeting.setId(existingMeeting.getId());
        this.validateMeetingService.validateIsOrganizer(updatedMeeting.getId(), organizer.getId());
        this.validateMeetingService.validateParticipants(updatedMeeting, organizer, meetingInfo.getParticipants());

        // Modificación de la reunión
        this.meetingService.editMeeting(updatedMeeting, meetingId, organizer, meetingInfo.getParticipants());
    }

    @PatchMapping("/{meetingId}/assist")
    public void assist(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long meetingId) {
        CommonUser user = jwtService.getCommonUserFromJWT(authorizationHeader);
        this.validateMeetingService.validateAssistChangeInfo(user, meetingId);
        this.meetingService.assist(user, meetingId);
    }

    @PatchMapping("/{meetingId}/changeStatus")
    public void changeStatus(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long meetingId, @RequestBody ChangeMeetingStatusRequest statusInfo) {  
        CommonUser user = jwtService.getCommonUserFromJWT(authorizationHeader);
        this.validateMeetingService.validateChangeStatusInfo(user, meetingId, statusInfo.getStatus());
        this.meetingService.changeStatus(meetingId, statusInfo.getStatus(), user);
    }

    @PatchMapping("/{meetingId}/changeInvitationStatus")
    public void changeInvitationStatus(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable Long meetingId,
            @RequestBody ChangeMeetingInvitationStatusRequest changeInfo) {
        
        CommonUser user = jwtService.getCommonUserFromJWT(authorizationHeader);
        this.validateMeetingService.validateInvitationStatusChangeInfo(user, changeInfo, meetingId);
        MeetingAttendance changedMeetingAttendance = this.validateMeetingService.toMeetingAttendance(meetingId,
                user, changeInfo);
        this.meetingService.changeInvitationStatus(changedMeetingAttendance, user);
    }

    @PostMapping("/getCandidatesToMeeting")
    public ResponseEntity<List<CandidateToMeetinDTO>> getCandidatesToMeeting(@RequestBody CandidatesRequest request) {
        return ResponseEntity.ok(meetingService.getCandidatesToMeeting(request));
    }
}