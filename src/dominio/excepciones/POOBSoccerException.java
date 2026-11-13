package dominio.excepciones;

/**
 * Excepcion unica del dominio de POOBSoccer. Toda violacion de las reglas de
 * negocio se reporta con esta clase y uno de los mensajes constantes.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class POOBSoccerException extends Exception {

    private static final long serialVersionUID = 1L;

    public static final String PERSONAJE_INVALIDO = "El personaje seleccionado no existe o no es válido.";
    public static final String MODO_INVALIDO = "La modalidad o tipo de partido seleccionado no es válido.";
    public static final String ESTADISTICAS_FUERA_DE_RANGO = "Las estadísticas deben estar entre 1 y 5.";
    public static final String SUPERVIVENCIA_SOLO_PVSM = "El modo Supervivencia solo está disponible en Jugador vs Máquina (PvsM).";
    public static final String JUGADORES_INCOMPLETOS = "Deben seleccionarse los personajes de ambos jugadores antes de iniciar.";
    public static final String PARTIDO_NO_INICIADO = "El partido no ha sido iniciado.";
    public static final String JUGADOR_INVALIDO = "El número de jugador debe ser 1 o 2.";
    public static final String TIEMPO_INVALIDO = "El tiempo transcurrido no puede ser negativo.";
    public static final String ACCION_INVALIDA = "La acción indicada no existe.";
    public static final String PARTIDO_EN_CURSO = "El partido aún no ha terminado.";

    /**
     * Crea la excepcion con el mensaje indicado.
     *
     * @param message Mensaje (normalmente una de las constantes de la clase).
     */
    public POOBSoccerException(String message) {
        super(message);
    }
}
