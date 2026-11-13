package dominio;

/**
 * Cancha 2D en vista lateral con sus limites y dos arcos. Es experta en saber
 * desde donde cae el balon y quien anota cuando entra en un arco.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Field {

    public static final double WIDTH = 900.0;
    public static final double GROUND_Y = 450.0;
    static final int NO_GOAL = 0;

    private static final double CEILING_Y = 0.0;
    private static final double CENTER_X = WIDTH / 2.0;
    private static final double BALL_DROP_Y = 60.0;

    // Arco izquierdo (defiende el jugador 1, anota el 2) y derecho (defiende el 2, anota el 1)
    private final Goal[] goals = { new Goal(0.0, Goal.WIDTH, 2), new Goal(WIDTH, WIDTH - Goal.WIDTH, 1) };

    /**
     * Avanza el balon un paso de fisica incluyendo el rebote en los travesaños.
     *
     * @param ball Balon.
     */
    void updateBall(Ball ball) {
        ball.updatePhysics(GROUND_Y, 0.0, WIDTH, CEILING_Y);
        for (Goal goal : goals) {
            goal.resolveCrossbar(ball);
        }
    }

    /**
     * Deja caer el balon desde el centro superior de la cancha, sin inercia.
     *
     * @param ball Balon.
     */
    void dropBall(Ball ball) {
        ball.placeAt(CENTER_X, BALL_DROP_Y);
    }

    /**
     * @param ball Balon.
     * @return Jugador (1 o 2) que anota si el balon esta completamente dentro de un arco;
     *         {@link #NO_GOAL} si no hay gol.
     */
    int scorerOf(Ball ball) {
        for (Goal goal : goals) {
            if (goal.isBallInside(ball)) {
                return goal.getScorer();
            }
        }
        return NO_GOAL;
    }
}
