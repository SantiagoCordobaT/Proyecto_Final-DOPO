package dominio.partidos;

import dominio.personajes.Character;

/**
 * Partido por tiempo: 60 segundos; si hay empate pasa a Gol de Oro.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class TimeMatch extends MatchType {

    public static final double MATCH_SECONDS = 60.0;

    private double remainingSeconds;
    private boolean goldenGoal;

    /** Crea el partido por tiempo. */
    public TimeMatch() {
        super("Por Tiempo");
    }

    @Override
    protected void onBegin() {
        remainingSeconds = MATCH_SECONDS;
        goldenGoal = false;
    }

    @Override
    public void tick(double deltaTimeSeconds) {
        if (isFinished() || goldenGoal) {
            return;
        }
        remainingSeconds = Math.max(0.0, remainingSeconds - deltaTimeSeconds);
        if (remainingSeconds > 0.0) {
            return;
        }
        int difference = getPlayer1().getGoals() - getPlayer2().getGoals();
        goldenGoal = difference == 0;
        if (difference > 0) {
            finish(getPlayer1());
        }
        if (difference < 0) {
            finish(getPlayer2());
        }
    }

    @Override
    public void goalScored(Character scorer, Character conceded) {
        if (goldenGoal && !isFinished()) {
            finish(scorer);
        }
    }

    @Override
    public double getRemainingSeconds() {
        return remainingSeconds;
    }

    @Override
    public boolean hasTimeLimit() {
        return true;
    }

    @Override
    public boolean isGoldenGoal() {
        return goldenGoal;
    }
}
