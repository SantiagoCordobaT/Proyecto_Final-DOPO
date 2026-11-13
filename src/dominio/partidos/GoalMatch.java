package dominio.partidos;

import dominio.personajes.Character;

/**
 * Partido por goles: sin limite de tiempo, gana el primero en llegar a la meta (3, 5 o 7).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class GoalMatch extends MatchType {

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
}
