package iso.e02.planify.requests;
import iso.e02.planify.entities.MeetingAttendance.Role;
import iso.e02.planify.entities.MeetingAttendance.InvitationStatus;


public class InspectUserForMeetingsDTO {
    private Long id;
    private String name;
    private String surnames;
    private String email;
    private Role role; // Incluye el rol
    private InvitationStatus invitationStatus;
    private boolean hasAssisted;
    private byte[] photo; // Incluye la foto



    // Constructor
    public InspectUserForMeetingsDTO(Long id, String name, String surnames, String email, Role role, InvitationStatus invitationStatus, boolean hasAssisted) {
        this.id = id;
        this.name = name;
        this.surnames = surnames;
        this.email = email;
        this.role = role;
        this.invitationStatus = invitationStatus;
        this.hasAssisted = hasAssisted;
    }

    // Constructor for the query para obtener el organizador
    public InspectUserForMeetingsDTO(Long id, String name, String surnames, byte[] photo) {
        this.id = id;
        this.name = name;
        this.surnames = surnames;
        this.photo = photo; // Assuming photo is stored in the email field for this query
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public boolean isHasAssisted() {
        return hasAssisted;
    }

    public void setHasAssisted(boolean hasAssisted) {
        this.hasAssisted = hasAssisted;
    }

    public byte[] getPhoto() {
        return photo;
    }

}
