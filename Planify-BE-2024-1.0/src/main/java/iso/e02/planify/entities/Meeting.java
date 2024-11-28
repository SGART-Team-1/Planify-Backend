package iso.e02.planify.entities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Meeting {

      /**
     * Identificador único del usuario, generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     
    @Column(nullable = false)
    private String subject;

    @Column(name = "all_day_long", nullable = false)
    private boolean allDayLong;

    @Column(name = "from_date_time", nullable = true, columnDefinition = "DATETIME2(0)")
    private LocalDateTime fromDateTime;

    @Column(name = "to_date_time", nullable = true,  columnDefinition = "DATETIME2(0)")
    private LocalDateTime toDateTime;

    @Column(nullable = false)
    private boolean isOnline;

    @Enumerated(EnumType.STRING)
    private Location location;

    @Column(nullable = true)
    private String observations;

    @Enumerated(EnumType.STRING)
    private Status status;  // Estado de la reunión: Abierta, Cerrada, Cancelada

    @OneToMany(mappedBy = "meeting", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<MeetingAttendance> participants;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(LocalDateTime fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    public void setToDateTime(LocalDateTime toDateTime) {
        this.toDateTime = toDateTime;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<MeetingAttendance> getParticipants() {
        return participants;
    }

    public void setParticipants(List<MeetingAttendance> participants) {
        this.participants = participants;
    }

    public enum Status {
        ABIERTA, CERRADA, CANCELADA
    }

    public enum Location {
        ESI, POLITECNICO, ALU, OFICINA, DESPACHO, BIBLIOTECA, CAFETERIA
    }

     public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("subject", this.subject);
        map.put("allDayLong", this.allDayLong);
        map.put("fromDateTime", this.fromDateTime);
        map.put("toDateTime", this.toDateTime);
        map.put("isOnline", this.isOnline);
        map.put("location", this.location);
        map.put("observations", this.observations);
        map.put("status", this.status);
        return map;
    }
}