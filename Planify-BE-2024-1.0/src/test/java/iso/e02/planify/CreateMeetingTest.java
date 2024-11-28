package iso.e02.planify;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;

import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.Schedule;
import iso.e02.planify.entities.Meeting.Location;
import iso.e02.planify.entities.Meeting.Status;
import iso.e02.planify.requests.CreateMeetingRequest;
import iso.e02.planify.services.AbsencesService;
import iso.e02.planify.services.ManageUsersService;
import iso.e02.planify.services.MeetingService;
import iso.e02.planify.services.ValidateMeetingService;
import iso.e02.planify.services.WorkScheduleService;

@ContextConfiguration(classes = {ValidateMeetingService.class, ManageUsersService.class, MeetingService.class, AbsencesService.class, WorkScheduleService.class})
@SpringBootTest
public class CreateMeetingTest {
    
    @Autowired
	private ValidateMeetingService validateMeetingService;

    @MockBean
    private ManageUsersService manageUsersService;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private AbsencesService absencesService;

    @MockBean
    private WorkScheduleService workScheduleService;

    @Test
    public void testOptionalFieldsCanBeNull() {
        CreateMeetingRequest meetingInfo = new CreateMeetingRequest(
            "Reunión de planificación", 
            false, 
            "2050-11-14", 
            "16:00", 
            "19:00", 
            false, 
            "ESI", 
            Arrays.asList("1", "2"), 
            null);
        Assertions.assertTrue(this.validateMeetingService.validateRequiredFields(meetingInfo), "Los campos opcionales pueden ser nulos o vacíos: observaciones.");
    }

    @ParameterizedTest
    @MethodSource("provideRequiredFieldsCannotBeNullOrEmpty")
    public void testRequiredFieldsCannotBeNullOrEmpty(String subject, String fromDate, String fromTime, 
        String toTime, String location, List<String> participants) {
        CreateMeetingRequest meetingInfo = new CreateMeetingRequest(subject, false, fromDate, fromTime, toTime, false, location, participants, null);
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateMeetingService.validateRequiredFields(meetingInfo), 
            "Se esperaba que se lanzara una excepción por no rellenar algún campo obligatorio: asunto, día completo, fecha, inicio, fin, localización, online y lista de participantes.");
    }

    public static Stream<Arguments> provideRequiredFieldsCannotBeNullOrEmpty() {
        String subject = "Reunión de planificación";
        String fromDate = "2024-11-14";
        String fromTime = "16:00";
        String toTime = "19:00";
        String location = "ESI";
        List<String> participants = Arrays.asList("1", "2");

        return Stream.of(
            Arguments.of(null, fromDate, fromTime, toTime, location, participants),     
            Arguments.of("",  fromDate, fromTime, toTime, location, participants),     
            
            Arguments.of(subject,  null, fromTime, toTime, location, participants),  
            Arguments.of(subject,  "", fromTime, toTime, location, participants),  

            Arguments.of(subject,  fromDate, null, toTime, location, participants),  
            Arguments.of(subject,  fromDate, "", toTime, location, participants),  

            Arguments.of(subject,  fromDate, fromTime, null, location, participants),  
            Arguments.of(subject,  fromDate, fromTime, "", location, participants),  
            
            Arguments.of(subject,  fromDate, fromTime, toTime, null, participants),  
            Arguments.of(subject,  fromDate, fromTime, toTime, "", participants),  

            Arguments.of(subject,  fromDate, fromTime, toTime, location, null),  
            Arguments.of(subject,  fromDate, fromTime, toTime, location, Arrays.asList())

            );
    }

    // Fecha
    @Test
    public void testValidDate() {
        String date = "2050-11-16";
        Assertions.assertTrue(this.validateMeetingService.isValidDate(date), "Fecha inválida. Asegúrese de que sigue el patrón yyyy-MM-dd.");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDate")
    public void testInvalidDate(String date) {
       Assertions.assertThrows(ResponseStatusException.class, () -> this.validateMeetingService.isValidDate(date), "La fecha de la reunión se consideró válida, pero se esperaba que no lo fuera.");
    }

    public static Stream<Arguments> provideInvalidDate() {
        return Stream.of(
            Arguments.of("12/10/2024"), // Formato incorrecto  
            Arguments.of("12-10-2024"), // Formato incorrecto
            Arguments.of("2023-10-12"), // Al menos año 2024
            Arguments.of(LocalDate.now().toString()), // HOY
            Arguments.of("2024-11-15") // Fecha anterior a hoy
            
            );
    }

    // Hora
    @Test
    public void testValidTime() {
        String fromTime = "18:00";
        String toTime = "18:30";
        Assertions.assertTrue(this.validateMeetingService.isValidTime(fromTime, toTime), "Hora inválida. Asegúrese de que sigue el patrón HH:mm.");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTime")
    public void testInvalidTime(String fromTime, String toTime) {
       Assertions.assertThrows(ResponseStatusException.class, () -> this.validateMeetingService.isValidTime(fromTime, toTime), "La fecha de la reunión se consideró válida, pero se esperaba que no lo fuera.");
    }

    public static Stream<Arguments> provideInvalidTime() {
        return Stream.of(
            Arguments.of("5PM", "8PM"), // Formato incorrecto  
            Arguments.of("10:00", "9:00"), // Hora inicio posterior a hora fin
            Arguments.of("10:00", "9:59"), // Hora inicio posterior a hora fin - límite
            Arguments.of("9:59", "10:00"), // Duración reunión mínimo 30 min
            Arguments.of("10:00", "10:29") // Duración reunión mínimo 30 min - límite
            );
    }

    // Enum Location
    @ParameterizedTest
    @MethodSource("provideValidLocation")
    public void testIsValidLocation(String location) {
       Assertions.assertTrue(this.validateMeetingService.isValidLocation(location), "La ubicación se consideró inválida, pero se esperaba que no lo fuera.");
    }

    public static Stream<Arguments> provideValidLocation() {
        return Stream.of(
            Arguments.of("ESI"),
            Arguments.of("Esi"), 
            Arguments.of("Politecnico"),
            Arguments.of("ALU"),
            Arguments.of("Oficina"),
            Arguments.of("Despacho"),
            Arguments.of("Biblioteca"),
            Arguments.of("Cafeteria")
            );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLocation")
    public void testIsInvalidLocation(String location) {
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateMeetingService.isValidLocation(location), "La ubicación se consideró válida, pero se esperaba que no lo fuera.");
    }

    public static Stream<Arguments> provideInvalidLocation() {
        return Stream.of(
            Arguments.of("ESI1"),
            Arguments.of("Esi2"), 
            Arguments.of("Politecnico3"),
            Arguments.of("ALU4"),
            Arguments.of("Oficina5"),
            Arguments.of("Despacho6"),
            Arguments.of("Biblioteca7"),
            Arguments.of("Cafeteria8")
            );
    }

    // Horario laboral
    @ParameterizedTest
    @MethodSource("provideValidWithSchedule")
    public void testIsValidWithSchedule(LocalTime fromTime, LocalTime toTime) {
        List<Schedule> workSchedule = defaultWorkSchedule();
        Assertions.assertTrue(this.validateMeetingService.validateWithSchedule(fromTime, toTime, workSchedule), "Se esperaba una validación correcta dentro de los bloques del horario laboral.");
    }

    public static Stream<Arguments> provideValidWithSchedule() {
        return Stream.of(
            // Madrugada
            Arguments.of(LocalTime.of(3, 00), LocalTime.of(4, 00)), // Dentro de horario
            Arguments.of(LocalTime.of(2, 00), LocalTime.of(7, 00)), // Dentro de horario - límites
            Arguments.of(LocalTime.of(2, 00), LocalTime.of(4, 00)), // Dentro de horario - límite inferior
            Arguments.of(LocalTime.of(3, 00), LocalTime.of(7, 00)), // Dentro de horario - límite superior
            // Mañana
            Arguments.of(LocalTime.of(10, 00), LocalTime.of(11, 00)),
            Arguments.of(LocalTime.of(9, 00), LocalTime.of(14, 00)),
            Arguments.of(LocalTime.of(9, 00), LocalTime.of(11, 00)),
            Arguments.of(LocalTime.of(10, 00), LocalTime.of(14, 00)),
            // Tarde
            Arguments.of(LocalTime.of(17, 00), LocalTime.of(19, 00)),
            Arguments.of(LocalTime.of(16, 00), LocalTime.of(20, 00)),
            Arguments.of(LocalTime.of(16, 00), LocalTime.of(19, 00)),
            Arguments.of(LocalTime.of(17, 00), LocalTime.of(20, 00))
            );
    }

    @Test
    public void testIsEmptySchedule() {
        List<Schedule> workSchedule = new ArrayList<>();
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateMeetingService.validateWithSchedule(LocalTime.of(10, 00), LocalTime.of(11, 00), workSchedule), "Se esperaba que no hubieran horarios laborales configurados.");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidWithSchedule")
    public void testIsInvalidWithSchedule(LocalTime fromTime, LocalTime toTime) {
        List<Schedule> workSchedule = defaultWorkSchedule();
        Assertions.assertThrows(ResponseStatusException.class, () -> this.validateMeetingService.validateWithSchedule(fromTime, toTime, workSchedule), "Se esperaba una validación incorrecta dentro de los bloques del horario laboral.");
    }

    public static Stream<Arguments> provideInvalidWithSchedule() {
        return Stream.of(
            // Antes de madrugada
            Arguments.of(LocalTime.of(1, 00), LocalTime.of(1, 30)), // Fuera de horario
            Arguments.of(LocalTime.of(0, 00), LocalTime.of(2, 00)), // Fuera de horario - límites
            Arguments.of(LocalTime.of(0, 00), LocalTime.of(1, 30)), // Fuera de horario - límite inferior
            Arguments.of(LocalTime.of(1, 00), LocalTime.of(2, 00)), // Fuera de horario - límite superior
            // Entre madrugada y mañana
            Arguments.of(LocalTime.of(8, 00), LocalTime.of(8, 30)),
            Arguments.of(LocalTime.of(7, 00), LocalTime.of(9, 00)),
            Arguments.of(LocalTime.of(7, 00), LocalTime.of(8, 30)),
            Arguments.of(LocalTime.of(8, 00), LocalTime.of(9, 00)),

            // Entre mañana y tarde
            Arguments.of(LocalTime.of(15, 00), LocalTime.of(15, 30)),
            Arguments.of(LocalTime.of(14, 00), LocalTime.of(16, 00)),
            Arguments.of(LocalTime.of(14, 00), LocalTime.of(15, 30)),
            Arguments.of(LocalTime.of(15, 00), LocalTime.of(16, 00)),

            // Después de tarde
            Arguments.of(LocalTime.of(21, 00), LocalTime.of(22, 00)),
            Arguments.of(LocalTime.of(20, 00), LocalTime.of(23, 59)),
            Arguments.of(LocalTime.of(20, 00), LocalTime.of(22, 00)),
            Arguments.of(LocalTime.of(21, 00), LocalTime.of(23, 59)),

            // Casos de solapamiento
            Arguments.of(LocalTime.of(1, 30), LocalTime.of(5, 30)), // Solapa con madrugada - límite inferior
            Arguments.of(LocalTime.of(6, 00), LocalTime.of(8, 00)), // Solapa con madrugada - límite superior
            Arguments.of(LocalTime.of(8, 00), LocalTime.of(10, 00)), // Solapa con mañana - límite inferior
            Arguments.of(LocalTime.of(13, 00), LocalTime.of(15, 00)), // Solapa con mañana - límite superior
            Arguments.of(LocalTime.of(15, 00), LocalTime.of(17, 00)), // Solapa con tarde - límite inferior
            Arguments.of(LocalTime.of(19, 00), LocalTime.of(21, 00)) // Solapa con tarde - límite superior
            );
    }

    // isAllDayLong
    @Test
    public void testAllDayLongMeeting() {
        CreateMeetingRequest meetingInfo = new CreateMeetingRequest(
            "Reunión de planificación", 
            true, 
            "2050-11-16", 
            "18:00", 
            "19:00", 
            false, 
            "ESI", 
            Arrays.asList("1", "2"), 
            null);

        List<Schedule> workSchedule = defaultWorkSchedule();
        when(workScheduleService.getWorkSchedule()).thenReturn(workSchedule);
        Assertions.assertTrue(this.validateMeetingService.validateMeetingInfo(meetingInfo), "Se esperaba una reunión de todo el día y, por tanto, sin horas que especificar.");
        Assertions.assertNull(meetingInfo.getFromTime(), "Como es una reunión de todo el día, se esperaba una hora de inicio nula.");
        Assertions.assertNull(meetingInfo.getToTime(), "Como es una reunión de todo el día, se esperaba una hora de fin nula.");
    }

    @Test
    public void testNotAllDayLongMeeting() {
        CreateMeetingRequest meetingInfo = new CreateMeetingRequest(
            "Reunión de planificación", 
            false, 
            "2050-11-16", 
            "18:00", 
            "19:00", 
            false, 
            "ESI", 
            Arrays.asList("1", "2"), 
            null);

        List<Schedule> workSchedule = defaultWorkSchedule();
        when(workScheduleService.getWorkSchedule()).thenReturn(workSchedule);
        Assertions.assertTrue(this.validateMeetingService.validateMeetingInfo(meetingInfo), "Se esperaba una reunión entre un rango de horas.");
        Assertions.assertNotNull(meetingInfo.getFromTime(), "Como no es una reunión de todo el día, se esperaba una hora de inicio.");
        Assertions.assertNotNull(meetingInfo.getToTime(), "Como no es una reunión de todo el día, se esperaba una hora de fin.");
    }

    // isOnline
    @Test
    public void testIsOnlineMeeting() {
        CreateMeetingRequest meetingInfo = new CreateMeetingRequest(
            "Reunión de planificación", 
            false, 
            "2050-11-16", 
            "18:00", 
            "19:00", 
            true, 
            "ESI", 
            Arrays.asList("1", "2"), 
            null);

        List<Schedule> workSchedule = defaultWorkSchedule();
        when(workScheduleService.getWorkSchedule()).thenReturn(workSchedule);
        Assertions.assertTrue(this.validateMeetingService.validateMeetingInfo(meetingInfo), "Se esperaba una reunión online y, por tanto, sin ubicación.");
        Assertions.assertNull(meetingInfo.getLocation(), "Como es una reunión online, se esperaba una ubicación nula.");
    }

    @Test
    public void testNotOnlineMeeting() {
        CreateMeetingRequest meetingInfo = new CreateMeetingRequest(
            "Reunión de planificación", 
            false, 
            "2050-11-16", 
            "18:00", 
            "19:00", 
            false, 
            "ESI", 
            Arrays.asList("1", "2"), 
            null);

        List<Schedule> workSchedule = defaultWorkSchedule();
        when(workScheduleService.getWorkSchedule()).thenReturn(workSchedule);
        Assertions.assertTrue(this.validateMeetingService.validateMeetingInfo(meetingInfo), "Se esperaba una reunión con ubicación.");
        Assertions.assertNotNull(meetingInfo.getLocation(), "Como no es una reunión online, se esperaba una ubicación.");
    }

    @ParameterizedTest
    @MethodSource("provideToMeeting")
    public void testToMeetingDependingOnAllDayLongAndOnline(boolean isAllDayLong, boolean isOnline) {
        CreateMeetingRequest meetingInfo = new CreateMeetingRequest(
            "Reunión de planificación", 
            isAllDayLong, 
            "2050-11-16", 
            "18:00", 
            "19:00", 
            isOnline, 
            "ESI", 
            Arrays.asList("1", "2"), 
            "");
            
        List<Schedule> workSchedule = defaultWorkSchedule();
        when(workScheduleService.getWorkSchedule()).thenReturn(workSchedule);
        Assertions.assertTrue(this.validateMeetingService.validateMeetingInfo(meetingInfo), "Se esperaba una reunión con ubicación.");
            
        Meeting meeting = this.validateMeetingService.toMeeting(meetingInfo); // Conversión

        // Comprobaciones de campos 
        Assertions.assertEquals("Reunión de planificación", meeting.getSubject(), "Se esperaba el mismo asunto.");
        Assertions.assertNull(meeting.getObservations(), "Se esperaba que las observaciones fueran nulas.");
        Assertions.assertEquals(Status.ABIERTA, meeting.getStatus(), "Se esperaba una reunión abierta.");

        // Si isAllDayLong, 
        Assertions.assertEquals(isAllDayLong, meeting.isAllDayLong(), "Se esperaba el mismo valor de día completo.");
        if(isAllDayLong) {
            Assertions.assertEquals(LocalDateTime.of(2050,11,16,0,0), meeting.getFromDateTime(), "Se esperaba una fecha de inicio mínima por ser una reunión de día completo.");
            Assertions.assertEquals(LocalDateTime.of(LocalDate.of(2050,11,16),LocalTime.MAX), meeting.getToDateTime(), "Se esperaba una fecha de fin por ser una reunión de día completo.");
        } else {
            Assertions.assertEquals(LocalDateTime.of(2050,11,16,18,0), meeting.getFromDateTime(), "Se esperaba misma fecha de inicio.");
            Assertions.assertEquals(LocalDateTime.of(2050,11,16,19,0), meeting.getToDateTime(), "Se esperaba misma fecha de fin.");
        }
        
        Assertions.assertEquals(isOnline, meeting.isOnline(), "Se esperaba el mismo valor de reunión online.");
        if(isOnline) {
            Assertions.assertNull(meeting.getLocation(), "Por ser una reunión online, se esperaba una ubicación nula.");
        } else {
            Assertions.assertEquals(Location.ESI, meeting.getLocation(),"Al no ser una reunión online, se esperaba la misma ubicación.");
        }
        
        
    
    }

    public static Stream<Arguments> provideToMeeting() {
        return Stream.of(
            Arguments.of(false, false),
            Arguments.of(false, true), 
            Arguments.of(true, false),
            Arguments.of(true, true)
            );
    }

    public List<Schedule> defaultWorkSchedule() {
        List<Schedule> workSchedule = new ArrayList<>();
        Schedule night = new Schedule(1L, "Madrugada", LocalTime.of(2,0), LocalTime.of(7,0));
        Schedule morning = new Schedule(2L, "Mañana", LocalTime.of(9,0), LocalTime.of(14,0));
        Schedule evening = new Schedule(3L, "Tarde", LocalTime.of(16,0), LocalTime.of(20,0));
        workSchedule.add(night);
        workSchedule.add(morning);
        workSchedule.add(evening);
        return workSchedule;
    }
}
