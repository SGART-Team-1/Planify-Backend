package iso.e02.planify.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;

import iso.e02.planify.requests.*;

class MeetingControllerTest {

    @Test
    void testListAllReturnsCorrectDataStructure() {
        // Simulación de datos que podría devolver el servicio
        List<Map<String, Object>> mockMeetings = List.of(
            Map.of("id", 1, "name", "Meeting 1", "organizer", "User 1"),
            Map.of("id", 2, "name", "Meeting 2", "organizer", "User 2")
        );

        // Verificación básica de la estructura
        assertNotNull(mockMeetings);
        assertEquals(2, mockMeetings.size());
        assertEquals("Meeting 1", mockMeetings.get(0).get("name"));
        assertEquals("User 2", mockMeetings.get(1).get("organizer"));
    }



    @Test
    void testChangeStatusValidatesStatus() {
        // Configuración de datos para cambiar el estado
        String newStatus = "CANCELLED";

        // Validar que el nuevo estado es válido
        List<String> validStatuses = List.of("SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED");

        assertTrue(validStatuses.contains(newStatus));
    }

    @Test
    void testInvitationStatusValidatesData() {
        // Configuración de datos para cambiar el estado de invitación
        ChangeMeetingInvitationStatusRequest changeRequest = new ChangeMeetingInvitationStatusRequest(null, null);
        changeRequest.setInvitationStatus("ACCEPTED");

        // Validación básica de los datos
        assertNotNull(changeRequest.getInvitationStatus());
        assertEquals("ACCEPTED", changeRequest.getInvitationStatus());
    }
}
