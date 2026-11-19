package dominio.partidos;

import dominio.excepciones.POOBSoccerException;

/**
 * Partido por tiempo: 60 segundos. Al acabarse gana quien va adelante; si hay
 * empate el reloj se congela en Gol de Oro y el siguiente gol termina el
 * partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class TimeMatch extends MatchType {

    private static final double SEGUNDOS_PARTIDO = 60.0;
    private static final String GOL_DE_ORO = "GOL DE ORO";

    private boolean golDeOro;

    /** Crea el partido por tiempo; solo lo crea {@link MatchTypeCatalog}. */
    TimeMatch() {
        super("Por Tiempo");
    }

    @Override
    public void iniciar(MatchContext contexto) {
        super.iniciar(contexto);
        golDeOro = false;
    }

    @Override
    public void avanzar(double segundos) throws POOBSoccerException {
        super.avanzar(segundos);
        if (!golDeOro && segundosRestantes() <= TOLERANCIA) {
            tiempoAgotado();
        }
    }

    /** Se acabo el tiempo: gana el lider o, con empate, empieza el Gol de Oro. */
    private void tiempoAgotado() throws POOBSoccerException {
        int diferencia = golesDe(JUGADOR_UNO) - golesDe(JUGADOR_DOS);
        if (diferencia == 0) {
            golDeOro = true;
        } else {
            finalizar(diferencia > 0 ? JUGADOR_UNO : JUGADOR_DOS);
        }
    }

    /**
     * Solo el gol de oro decide el partido; los demas goles solo suman al marcador.
     */
    @Override
    protected void alAnotarGol(int anotador) {
        if (golDeOro) {
            finalizar(anotador);
        }
    }

    /**
     * @return "GOL DE ORO" o el tiempo restante "mm:ss", con las fracciones
     *         redondeadas hacia arriba.
     */
    @Override
    public String obtenerTextoEstado() {
        return golDeOro ? GOL_DE_ORO : formatoReloj((int) Math.ceil(segundosRestantes() - TOLERANCIA));
    }

    private double segundosRestantes() {
        return Math.max(0.0, SEGUNDOS_PARTIDO - obtenerSegundosTranscurridos());
    }
}
