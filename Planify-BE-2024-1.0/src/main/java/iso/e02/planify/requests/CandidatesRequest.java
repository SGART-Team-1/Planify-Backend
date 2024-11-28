package iso.e02.planify.requests;

/**
 * Clase auxiliar que representa una solicitud de candidatos.
 * Contiene los campos necesarios para obtener los candidatos disponibles
 * para una reunión, como la fecha y hora de inicio y fin de la reunión,
 * si es el día completo y la fecha de la reunión.
 */
public class CandidatesRequest {

    String fromDateTime;
    String toDateTime;
    boolean isAllDay ;
    String meetingDate;

public CandidatesRequest(String fromDateTime, String toDateTime, boolean isAllDay, String meetingDate) {
    this.fromDateTime = fromDateTime;
    this.toDateTime = toDateTime;
    this.isAllDay = isAllDay;
    this.meetingDate = meetingDate;
}

public String getFromDateTime() {
    return fromDateTime;
}

public void setFromDateTime(String fromDateTime) {
    this.fromDateTime = fromDateTime;
}

public String getToDateTime() {
    return toDateTime;
}

public void setToDateTime(String toDateTime) {
    this.toDateTime = toDateTime;
}

public boolean isAllDay() {
    return isAllDay;
}

public void setAllDay(boolean isAllDay) {
    this.isAllDay = isAllDay;
}

public String getMeetingDate() {
    return meetingDate;
}

public void setMeetingDate(String meetingDate) {
    this.meetingDate = meetingDate;
}
}
