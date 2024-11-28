package iso.e02.planify.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

class ScheduleTest {

    private Schedule schedule;

    @BeforeEach
    void setUp() {
        schedule = new Schedule();
    }

 

    @Test
    void testBlockName() {
        String blockName = "Mañana";
        schedule.setBlockName(blockName);
        assertEquals(blockName, schedule.getBlockName());
    }

    @Test
    void testStartHour() {
        LocalTime startHour = LocalTime.of(8,0);
        schedule.setStartHour(startHour);
        assertEquals(startHour, schedule.getStartHour());
    }

    @Test
    void testEndHour() {
        LocalTime endHour = LocalTime.of(17,0);
        schedule.setEndHour(endHour);
        assertEquals(endHour, schedule.getEndHour());
    }
}
