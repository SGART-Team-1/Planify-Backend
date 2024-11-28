package iso.e02.planify.requests;


public class ChangeMeetingInvitationStatusRequest {

    private String invitationStatus;
    
    private String declineReason;

    public ChangeMeetingInvitationStatusRequest(String invitationStatus, String declineReason) {
        this.invitationStatus = invitationStatus;
        this.declineReason = declineReason;
    }
    
    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }
}
