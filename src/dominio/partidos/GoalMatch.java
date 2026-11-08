package dominio.partidos;

import dominio.excepciones.POOBSoccerException;
import dominio.personajes.Character;

/**
 * Partido por goles: sin limite de tiempo, gana el primero en llegar a la meta (3, 5 o 7).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class GoalMatch extends MatchType {

    public static final int GOALS_SHORT = 3;
    public static final int GOALS_MEDIUM = 5;
    public static final int GOALS_LONG = 7;

    private final int targetGoals;

    /**
     * @param targetGoals Meta de goles (3, 5 o 7).
     * @throws POOBSoccerException Si la meta no es valida.
     */
    public GoalMatch(int targetGoals) throws POOBSoccerException {
        super("Por Goles (" + targetGoals + ")");
        boolean valid = targetGoals == GOALS_SHORT || targetGoals == GOALS_MEDIUM || targetGoals == GOALS_LONG;
        if (!valid) {
            throw new POOBSoccerException(POOBSoccerException.META_INVALIDA);
        }
        this.targetGoals = targetGoals;
    }

    @Override
    protected void onBegin() {
        // No tiene estado propio: las metas se leen de los goles de los jugadores.
    }

    @Override
    public void tick(double deltaTimeSeconds) {
        // Sin limite de tiempo.
    }

    @Override
    public void goalScored(Character scorer, Character conceded) {
        if (!isFinished() && scorer.getGoals() >= targetGoals) {
            finish(scorer);
        }
    }

    /** @return Meta de goles. */
    public int getTargetGoals() {
        return targetGoals;
    }
}
