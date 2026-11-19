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

    private static final double MATCH_SECONDS = 60.0;
    private static final String GOLDEN_GOAL = "GOL DE ORO";

    private boolean goldenGoal;

    /** Crea el partido por tiempo; solo lo crea {@link MatchTypeCatalog}. */
    TimeMatch() {
        super("Por Tiempo");
    }

    @Override
    public void begin(MatchContext context) {
        super.begin(context);
        goldenGoal = false;
    }

    @Override
    public void tick(double deltaTimeSeconds) throws POOBSoccerException {
        super.tick(deltaTimeSeconds);
        if (!goldenGoal && remainingSeconds() <= TOLERANCE) {
            timeUp();
        }
    }

    /** Se acabo el tiempo: gana el lider o, con empate, empieza el Gol de Oro. */
    private void timeUp() throws POOBSoccerException {
        int difference = goalsOf(PLAYER_ONE) - goalsOf(PLAYER_TWO);
        if (difference == 0) {
            goldenGoal = true;
        } else {
            finish(difference > 0 ? PLAYER_ONE : PLAYER_TWO);
        }
    }

    /** Solo el gol de oro decide el partido; los demas goles solo suman al marcador. */
    @Override
    protected void onGoal(int scorer) {
        if (goldenGoal) {
            finish(scorer);
        }
    }

    /** @return "GOL DE ORO" o el tiempo restante "mm:ss", con las fracciones redondeadas hacia arriba. */
    @Override
    public String getStatusText() {
        return goldenGoal ? GOLDEN_GOAL : clock((int) Math.ceil(remainingSeconds() - TOLERANCE));
    }

    private double remainingSeconds() {
        return Math.max(0.0, MATCH_SECONDS - getElapsedSeconds());
    }
}
