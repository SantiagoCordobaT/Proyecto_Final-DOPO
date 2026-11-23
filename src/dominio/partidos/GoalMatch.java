package dominio.partidos;

import dominio.excepciones.POOBSoccerException;

/**
 * Partido por goles: no tiene limite de tiempo y gana el primero en alcanzar
 * la meta de goles definida (3, 5 o 7).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class GoalMatch extends MatchType {

    private final int metaGoles;

    /**
     * Crea un partido por goles con la meta indicada.
     *
     * @param metaGoles Cantidad de goles necesaria para ganar.
     */
    GoalMatch(int metaGoles) {
        super("Por Goles (" + metaGoles + ")");
        this.metaGoles = metaGoles;
    }

    /**
     * Verifica si el anotador alcanzo la meta de goles y, de ser asi, cierra el
     * partido dandolo como ganador.
     *
     * @param anotador Jugador que anoto el gol (1 o 2).
     */
    @Override
    protected void alAnotarGol(int anotador) throws POOBSoccerException {
        if (golesDe(anotador) >= metaGoles) {
            finalizar(anotador);
        }
    }

    /**
     * Construye el texto del marcador con la meta y el tiempo jugado.
     *
     * @return Meta de goles y reloj transcurrido en formato "mm:ss".
     */
    @Override
    public String obtenerTextoEstado() {
        int segundos = (int) (obtenerSegundosTranscurridos() + TOLERANCIA);
        return "Meta: " + metaGoles + " goles | " + formatoReloj(segundos);
    }
}
