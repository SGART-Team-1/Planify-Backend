package iso.e02.planify.requests;

import java.util.List;
import java.util.Map;

/**
 * Clase auxiliar para recibir solicitudes de horarios de trabajo.
 * 
 * Esta clase actúa como un objeto de transferencia de datos (DTO) para
 * encapsular
 * la información de los bloques de horario laboral en las solicitudes HTTP.
 * Contiene una lista de mapas que representan los bloques de horarios, donde
 * cada
 * mapa almacena los detalles del bloque, como el nombre, la hora de inicio y la
 * hora de fin.
 */

// Nueva clase auxiliar para el horario de trabajo
public class WorkScheduleRequest {

    /**
     * Lista de bloques de horario laboral.
     * 
     * Cada bloque está representado como un mapa de claves y valores, donde las
     * claves son
     * nombres de atributos (por ejemplo, "blockName", "startHour", "endHour") y los
     * valores son cadenas
     * que representan los detalles de cada bloque de horario.
     */

    private List<Map<String, String>> blocks;

    //constructor
   

    /**
     * Obtiene la lista de bloques de horario laboral.
     * 
     * @return una lista de mapas que representan los bloques de horarios.
     */

    public List<Map<String, String>> getBlocks() {
        return this.blocks;
    }

    public void setBlocks(List<Map<String, String>> blocks) {
        this.blocks = blocks;
    }

}