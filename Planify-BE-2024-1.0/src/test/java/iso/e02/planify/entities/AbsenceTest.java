package iso.e02.planify.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import iso.e02.planify.entities.Absence.Type;

public class AbsenceTest {

    private Absence absence;

    @BeforeEach
    public void setUp() {
        absence = new Absence();
    }

    @Test
    public void testSetAndGetId() {
        Long id = 1L;
        absence.setId(id);
        assertEquals(id, absence.getId());
    }

    @Test
    public void testSetAndGetUser() {
        CommonUser user = new CommonUser();
        user.setId(1L);
        absence.setUser(user);
        assertEquals(user.getId(), absence.getUser());
    }

    @Test
    public void testSetAndGetFromDateTime() {
        LocalDateTime fromDateTime = LocalDateTime.now();
        absence.setFromDateTime(fromDateTime);
        assertEquals(fromDateTime, absence.getFromDateTime());
    }

    @Test
    public void testSetAndGetToDateTime() {
        LocalDateTime toDateTime = LocalDateTime.now().plusDays(1);
        absence.setToDateTime(toDateTime);
        assertEquals(toDateTime, absence.getToDateTime());
    }

    @Test
    public void testSetAndGetAbsenceType() {
        Type absenceType = Type.VACACIONES;
        absence.setAbsenceType(absenceType);
        assertEquals(absenceType, absence.getAbsenceType());
    }
}
