package dominio.partidos;

import dominio.excepciones.POOBSoccerException;

/**
 * Partido por goles: sin limite de tiempo, gana el primero en llegar a la meta
 * (3, 5 o 7).
 * 
 * 
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class GoalMatch extends MatchType {

    private final int metaGoles;

    /**
     * Solo lo crea {@link MatchTypeCatalog}, que decide las metas ofrecidas.
     *
     * @param metaGoles Meta de goles.
     */
    GoalMatch(int metaGoles) {
        super("Por Goles (" + metaGoles + ")");
        this.metaGoles = metaGoles;
    }

    @Override
    protected void alAnotarGol(int anotador) throws POOBSoccerException {
        if (golesDe(anotador) >= metaGoles) {
            finalizar(anotador);
        }
    }

    /**
     * @return Meta de goles y tiempo jugado en segundos completos (redondeado hacia
     *         abajo), por ejemplo "Meta: 3 goles | 00:42".
     */
    @Override
    public String obtenerTextoEstado() {
        return "Meta: " + metaGoles + " goles | "
                + formatoReloj((int) Math.floor(obtenerSegundosTranscurridos() + TOLERANCIA));
    }
}
