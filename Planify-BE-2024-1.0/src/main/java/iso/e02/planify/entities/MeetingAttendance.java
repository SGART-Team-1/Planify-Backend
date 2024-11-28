package iso.e02.planify.entities;

import jakarta.persistence.*;

@Entity
public class MeetingAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private CommonUser user;

    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    /*
     * null
     */
    // Estado de la invitación: Aceptada, Rechazada, etc.
    @Enumerated(EnumType.STRING)
    private InvitationStatus invitationStatus;

    @Column(name = "decline_reason", nullable = true)
    private String declineReason;  // Motivo del rechazo, si aplica

    @Column(name = "has_assisted", nullable = false)
    private boolean hasAssisted;      // Indica si asistió a la reunión

    @Enumerated(EnumType.STRING)
    private Role role;            // Rol en la reunión


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CommonUser getUser() {
        return user;
    }

    public void setUser(CommonUser user) {
        this.user = user;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public enum InvitationStatus {
        ACEPTADA, RECHAZADA, PENDIENTE
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public boolean hasAssisted() {
        return hasAssisted;
    }

    public void setAssisted(boolean hasAssisted) {
        this.hasAssisted = hasAssisted;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public enum Role {
        ORGANIZADOR, ASISTENTE
    }
    
}