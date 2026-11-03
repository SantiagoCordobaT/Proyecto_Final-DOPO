package dominio.excepciones;

public class POOBSoccerException extends Exception {
    
    public static final String PERSONAJE_INVALIDO = "El índice del personaje seleccionado no existe.";
    public static final String MODO_INVALIDO = "La modalidad o tipo de partido seleccionado no es válido.";
    public static final String ESTADISTICAS_FUERA_DE_RANGO = "Las estadísticas deben estar entre 1 y 5.";
    public static final String SUPERVIVENCIA_SOLO_PVSM = "El modo Supervivencia solo está disponible en Jugador vs Máquina (PvsM).";
    public static final String BARRA_PODER_INCOMPLETA = "El poder solo se puede usar cuando la barra de poder está llena (100%).";
    public static final String JUGADORES_INCOMPLETOS = "Deben seleccionarse los personajes de ambos jugadores antes de iniciar.";

    public POOBSoccerException(String message) {
        super(message);
    }
}
