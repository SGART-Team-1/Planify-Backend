package iso.e02.planify.requests;

import java.time.LocalDateTime;

//Un DTO (Data Transfer Object) es un objeto que se utiliza para transportar datos entre diferentes procesos.
//En este caso, se utiliza para transportar los datos de un candidato a una reunión al convocar y modificar reuniones.
public class CandidateToMeetinDTO {
    private Long id;
    private String name;
    private String surnames;
    private String email;
    private LocalDateTime fromDateTimeAbsence;
    private LocalDateTime toDateTimeAbsence;
    private boolean hasAbsences;
    private boolean hasMeetings;

    // Constructor
    public CandidateToMeetinDTO(Long id, String name, String surnames, String email, LocalDateTime fromDateTimeAbsence, LocalDateTime toDateTimeAbsence) {
        this.id = id;
        this.name = name;
        this.surnames = surnames;
        this.email = email;
        this.fromDateTimeAbsence = fromDateTimeAbsence;
        this.toDateTimeAbsence = toDateTimeAbsence;
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

    public LocalDateTime getfromDateTimeAbsence() {
        return fromDateTimeAbsence;
    }

    public void setfromDateTimeAbsence(LocalDateTime fromDateTimeAbsence) {
        this.fromDateTimeAbsence = fromDateTimeAbsence;
    }

    public LocalDateTime gettoDateTimeAbsence() {
        return toDateTimeAbsence;
    }

    public void settoDateTimeAbsence(LocalDateTime toDateTimeAbsence) {
        this.toDateTimeAbsence = toDateTimeAbsence;
    }


    public boolean getHasAbsences() {
        return hasAbsences;
    }

    public void setHasAbsences(boolean hasAbsences) {
        this.hasAbsences = hasAbsences;
    }

    public boolean getHasMeetings() {
        return hasMeetings;
    }

    public void setHasMeetings(boolean hasMeetings) {
        this.hasMeetings = hasMeetings;
    }

}