package iso.e02.planify.requests;

import java.util.List;


public class CreateMeetingRequest {
    
    private String subject;
    private boolean allDayLong;
    private String date;
    private String fromTime;
    private String toTime;
    private boolean isOnline;
    private String location;
    private String observations;
    private List<String> participants;

    public CreateMeetingRequest(String subject, boolean allDayLong, String date, String fromTime, String toTime, boolean isOnline, String location, List<String> participants, String observations) {
        this.subject = subject;
        this.date = date;
        this.allDayLong = allDayLong;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.isOnline = isOnline;
        this.location = location;
        this.participants = participants;
        this.observations = observations;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isAllDayLong() {
        return allDayLong;
    }

    public void setAllDayLong(boolean allDayLong) {
        this.allDayLong = allDayLong;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String fromDate) {
        this.date = fromDate;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
   
}
