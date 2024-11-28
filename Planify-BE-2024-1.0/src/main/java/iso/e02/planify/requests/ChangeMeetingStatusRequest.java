package iso.e02.planify.requests;

public class ChangeMeetingStatusRequest {

    private String status;

    public ChangeMeetingStatusRequest() {}
    
    public ChangeMeetingStatusRequest(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}