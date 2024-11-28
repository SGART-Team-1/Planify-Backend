package iso.e02.planify.services;

import iso.e02.planify.entities.Absence;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Absence.Type;
import iso.e02.planify.repositories.AbsenceRepository;
import iso.e02.planify.repositories.CommonUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbsencesServiceTest {

    @InjectMocks
    private AbsencesService absencesService;

    @Mock
    private AbsenceRepository absenceRepository;

    @Mock
    private CommonUserRepository commonUserRepository;

    @Mock
    private WorkScheduleService workScheduleService;

    private CommonUser user;
    private Absence absence;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new CommonUser();
        user.setId(1L);
        user.setName("Jane Doe");
        // Set other properties as necessary

        absence = new Absence();
        absence.setId(1L);
        absence.setUser(user);
        absence.setAbsenceType(Type.VACACIONES);
        // Set other properties as necessary
    }
    
    

    @Test
    void testListAbsences() {
        // Arrange
        when(absenceRepository.findByCommonUserId(1L)).thenReturn(Collections.singletonList(absence));

        // Act
        List<Absence> absences = absencesService.list(1L);

        // Assert
        assertEquals(1, absences.size());
        assertEquals(Type.VACACIONES, absences.get(0).getAbsenceType());
        verify(absenceRepository).findByCommonUserId(1L);
    }

    @Test
    void testDeleteAbsence() {
        // Arrange
        doNothing().when(absenceRepository).deleteById(1L);

        // Act
        absencesService.delete(1L);

        // Assert
        verify(absenceRepository).deleteById(1L);
    }

  
}
