package iso.e02.planify.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateAbsenceRequestTest {

    @Test
    void testCreateAbsenceRequest() {
        // Arrange: Create an instance using the constructor
        String absenceType = "Vacation";
        Boolean allDayLong = true;
        String fromDate = "2023-12-01";
        String fromTime = "08:00";
        String toDate = "2023-12-02";
        String toTime = "17:00";
        long userId = 12345L;
        boolean overlapsMeeting = false;

        // Act: Create the request
        CreateAbsenceRequest request = new CreateAbsenceRequest(absenceType, allDayLong, fromDate, fromTime, toDate, toTime, userId, overlapsMeeting);

        // Assert: Verify that each field was set correctly
        assertEquals(absenceType, request.getAbsenceType());
        assertEquals(fromDate, request.getFromDate());
        assertEquals(fromTime, request.getFromTime());
        assertEquals(toDate, request.getToDate());
        assertEquals(toTime, request.getToTime());
        assertEquals(userId, request.getUserId());
        assertEquals(overlapsMeeting, request.hasOverlapsMeeting());
    }
}
