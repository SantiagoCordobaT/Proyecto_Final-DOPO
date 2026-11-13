package dominio.partidos;

import dominio.excepciones.POOBSoccerException;

/**
 * Partido por goles: sin limite de tiempo, gana el primero en llegar a la meta (3, 5 o 7).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class GoalMatch extends MatchType {

    private final int targetGoals;

    /**
     * Solo lo crea {@link MatchTypeCatalog}, que decide las metas ofrecidas.
     *
     * @param targetGoals Meta de goles.
     */
    GoalMatch(int targetGoals) {
        super("Por Goles (" + targetGoals + ")");
        this.targetGoals = targetGoals;
    }

    @Override
    protected void onGoal(int scorer) throws POOBSoccerException {
        if (goalsOf(scorer) >= targetGoals) {
            finish(scorer);
        }
    }

    /**
     * @return Meta de goles y tiempo jugado en segundos completos (redondeado hacia
     *         abajo), por ejemplo "Meta: 3 goles | 00:42".
     */
    @Override
    public String getStatusText() {
        return "Meta: " + targetGoals + " goles | " + clock((int) Math.floor(getElapsedSeconds() + TOLERANCE));
    }
}
