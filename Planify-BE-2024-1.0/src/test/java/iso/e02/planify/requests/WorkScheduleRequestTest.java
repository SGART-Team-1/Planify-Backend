package iso.e02.planify.requests;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para la clase WorkScheduleRequest.
 */
public class WorkScheduleRequestTest {

    /**
     * Test para el constructor y el método getter de la clase WorkScheduleRequest.
     */
    @Test
    public void testWorkScheduleRequest() {
        // Preparar los datos de prueba
        Map<String, String> block1 = Map.of(
                "blockName", "Morning Shift",
                "startHour", "08:00",
                "endHour", "12:00"
        );
        Map<String, String> block2 = Map.of(
                "blockName", "Afternoon Shift",
                "startHour", "13:00",
                "endHour", "17:00"
        );

        // Crear una lista de bloques
        List<Map<String, String>> blocks = List.of(block1, block2);

        // Crear un objeto WorkScheduleRequest
        WorkScheduleRequest workScheduleRequest = new WorkScheduleRequest();
        workScheduleRequest.setBlocks(blocks);

        // Verificar que la lista de bloques está correctamente inicializada
        assertNotNull(workScheduleRequest.getBlocks(), "La lista de bloques no debe ser nula.");
        assertEquals(2, workScheduleRequest.getBlocks().size(), "La lista de bloques debe contener 2 elementos.");
        
        // Verificar los valores del primer bloque
        Map<String, String> firstBlock = workScheduleRequest.getBlocks().get(0);
        assertEquals("Morning Shift", firstBlock.get("blockName"), "El nombre del primer bloque no coincide.");
        assertEquals("08:00", firstBlock.get("startHour"), "La hora de inicio del primer bloque no coincide.");
        assertEquals("12:00", firstBlock.get("endHour"), "La hora de fin del primer bloque no coincide.");
        
        // Verificar los valores del segundo bloque
        Map<String, String> secondBlock = workScheduleRequest.getBlocks().get(1);
        assertEquals("Afternoon Shift", secondBlock.get("blockName"), "El nombre del segundo bloque no coincide.");
        assertEquals("13:00", secondBlock.get("startHour"), "La hora de inicio del segundo bloque no coincide.");
        assertEquals("17:00", secondBlock.get("endHour"), "La hora de fin del segundo bloque no coincide.");
    }
}
