package iso.e02.planify.requests;

public class InvitationNotificationRequest {
    private Long meetingId;
    private Long userId; // Agregado

    // Getters y Setters
    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

