package iso.e02.planify.requests;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationDTO {
    private UUID id;
    private String message;
    private LocalDateTime readingDate;

    public NotificationDTO(UUID id, String message, LocalDateTime readingDate) {
        this.id = id;
        this.message = message;
        this.readingDate = readingDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getReadingDate() {
        return readingDate;
    }

    public void setReadingDate(LocalDateTime readingDate) {
        this.readingDate = readingDate;
    }

}

