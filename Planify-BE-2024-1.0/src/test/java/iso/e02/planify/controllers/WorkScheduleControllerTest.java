package iso.e02.planify.controllers;

import iso.e02.planify.entities.Schedule;
import iso.e02.planify.requests.WorkScheduleRequest;
import iso.e02.planify.services.WorkScheduleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class WorkScheduleControllerTest {

    @Mock
    private WorkScheduleService workScheduleService;

    @InjectMocks
    private WorkScheduleController workScheduleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddWorkSchedule() {
        // Arrange
        WorkScheduleRequest request = new WorkScheduleRequest();
        List<Map<String, String>> blocks = List.of(
            Map.of("blockName", "Morning Shift", "startHour", "08:00", "endHour", "12:00"),
            Map.of("blockName", "Afternoon Shift", "startHour", "13:00", "endHour", "17:00")
        );
        request.setBlocks(blocks);

        // Act
        workScheduleController.addWorkSchedule(request);

        // Assert
        verify(workScheduleService, times(1)).validateWorkSchedule(request.getBlocks());
        verify(workScheduleService, times(1)).saveWorkSchedule(request.getBlocks());
    }

    @Test
    void testGetWorkSchedule() {
        // Arrange
        List<Schedule> expectedSchedules = Arrays.asList(
            new Schedule(1L, "Block 1", LocalTime.of(9,0), LocalTime.of(14,0)),
            new Schedule(2L, "Block 2", LocalTime.of(9,0), LocalTime.of(14,0))
        );
        when(workScheduleService.getWorkSchedule()).thenReturn(expectedSchedules);

        // Act
        List<Schedule> actualSchedules = workScheduleController.getWorkSchedule();

        // Assert
        verify(workScheduleService, times(1)).getWorkSchedule();
        assertEquals(expectedSchedules, actualSchedules);
    }
}
