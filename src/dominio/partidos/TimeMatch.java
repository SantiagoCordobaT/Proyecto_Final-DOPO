package dominio.partidos;

import dominio.excepciones.POOBSoccerException;

/**
 * Partido por tiempo: 60 segundos. Al acabarse gana quien va adelante; si hay
 * empate el reloj se congela en Gol de Oro y el siguiente gol termina el partido.
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
    public void begin(MatchContext contexto) {
        super.begin(contexto);
        golDeOro = false;
    }

    @Override
    public void tick(double segundos) throws POOBSoccerException {
        super.tick(segundos);
        if (!golDeOro && remainingSeconds() <= TOLERANCIA) {
            timeUp();
        }
    }

    /** Se acabo el tiempo: gana el lider o, con empate, empieza el Gol de Oro. */
    private void timeUp() throws POOBSoccerException {
        int diferencia = goalsOf(JUGADOR_UNO) - goalsOf(JUGADOR_DOS);
        if (diferencia == 0) {
            golDeOro = true;
        } else {
            finish(diferencia > 0 ? JUGADOR_UNO : JUGADOR_DOS);
        }
    }

    /** Solo el gol de oro decide el partido; los demas goles solo suman al marcador. */
    @Override
    protected void onGoal(int anotador) {
        if (golDeOro) {
            finish(anotador);
        }
    }

    /** @return "GOL DE ORO" o el tiempo restante "mm:ss", con las fracciones redondeadas hacia arriba. */
    @Override
    public String getStatusText() {
        return golDeOro ? GOL_DE_ORO : clock((int) Math.ceil(remainingSeconds() - TOLERANCIA));
    }

    private double remainingSeconds() {
        return Math.max(0.0, SEGUNDOS_PARTIDO - getElapsedSeconds());
    }
}
