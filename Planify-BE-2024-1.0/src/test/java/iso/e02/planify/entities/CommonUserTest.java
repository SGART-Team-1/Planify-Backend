package iso.e02.planify.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CommonUserTest {

    private CommonUser commonUser;

    @BeforeEach
    void setUp() {
        commonUser = new CommonUser();
    }

    @Test
    void testRegistrationDate() {
        LocalDate date = LocalDate.now();
        commonUser.setRegistrationDate(date);
        assertEquals(date, commonUser.getRegistrationDate());
    }

    @Test
    void testDepartment() {
        String department = "Engineering";
        commonUser.setDepartment(department);
        assertEquals(department, commonUser.getDepartment());
    }

    @Test
    void testProfile() {
        String profile = "Manager";
        commonUser.setProfile(profile);
        assertEquals(profile, commonUser.getProfile());
    }

    @Test
    void testActivationStatus() {
        commonUser.setActivated(true);
        assertTrue(commonUser.isActivated());
        commonUser.setActivated(false);
        assertFalse(commonUser.isActivated());
    }

    @Test
    void testBlockedStatus() {
        commonUser.setBlocked(true);
        assertTrue(commonUser.isBlocked());
        commonUser.setBlocked(false);
        assertFalse(commonUser.isBlocked());
    }

    @Test
    void testAbsencesList() {
        Absence absence1 = new Absence();
        Absence absence2 = new Absence();
        List<Absence> absences = new ArrayList<>();
        absences.add(absence1);
        absences.add(absence2);
        commonUser.absences = absences;

        assertEquals(2, commonUser.absences.size());
        assertTrue(commonUser.absences.contains(absence1));
        assertTrue(commonUser.absences.contains(absence2));
    }
}
