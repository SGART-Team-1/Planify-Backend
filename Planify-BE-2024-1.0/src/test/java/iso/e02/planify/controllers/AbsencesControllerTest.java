package iso.e02.planify.controllers;

import iso.e02.planify.entities.Absence;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.requests.CreateAbsenceRequest;
import iso.e02.planify.services.AbsencesService;
import iso.e02.planify.services.JWTService;
import iso.e02.planify.services.ManageUsersService;
import iso.e02.planify.services.ValidateAbsenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbsencesControllerTest {

    @InjectMocks
    private AbsencesController absencesController;

    @Mock
    private AbsencesService absencesService;

    @Mock
    private ValidateAbsenceService validateAbsenceService;

    @Mock
    private JWTService jwtService;

    @Mock
    private ManageUsersService manageUsersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckMeetingOverlap() {
        CreateAbsenceRequest request = new CreateAbsenceRequest("Sick Leave", true, "2024-11-25", "09:00","2024-11-26", "17:00", 12345L, false);

        when(absencesService.checkMeetingOverlap(request)).thenReturn(true);

        ResponseEntity<Boolean> response = absencesController.checkMeetingOverlap(request);
        Boolean responseBody = response.getBody();

        verify(validateAbsenceService).validateAbsenceInfo(request);
        assertNotNull(responseBody);
        assertTrue(responseBody);
    }

    @Test
    void testCreate() {
        CreateAbsenceRequest request = new CreateAbsenceRequest("Sick Leave", true, "2024-11-25", "09:00","2024-11-26", "17:00", 12345L, false);

        Absence absence = new Absence();

        when(validateAbsenceService.toAbsence(request)).thenReturn(absence);

        absencesController.create(request);

        verify(validateAbsenceService).validateAbsenceInfo(request);
        verify(absencesService).create(absence);
        
    }

    @Test
    void testList() {
        Long userId = 1L;
        List<Absence> absences = Arrays.asList(new Absence(), new Absence());

        when(absencesService.list(userId)).thenReturn(absences);

        ResponseEntity<List<Absence>> response = absencesController.list(userId);
        List<Absence> responseBody = response.getBody();

        verify(manageUsersService).userExists(userId);
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void testListAll() {
        String token = "Bearer some.jwt.token";
        CommonUser user = new CommonUser();
        user.setId(1L);
        List<Absence> absences = Arrays.asList(new Absence(), new Absence());

        when(jwtService.getCommonUserFromJWT(token)).thenReturn(user);
        when(absencesService.list(user.getId())).thenReturn(absences);

        ResponseEntity<List<Absence>> response = absencesController.listAll(token);
        List<Absence> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void testDelete() {
        long absenceId = 1L;

        // Configurar mocks para métodos que devuelven valores
        when(validateAbsenceService.validateAbsenceExist(absenceId)).thenReturn(true); // o el valor apropiado
        
        // Llamar al método
        absencesController.delete(absenceId);

        // Verificar que se llamaron correctamente
        verify(validateAbsenceService).validateAbsenceExist(absenceId);
        verify(absencesService).delete(absenceId);
    }

}