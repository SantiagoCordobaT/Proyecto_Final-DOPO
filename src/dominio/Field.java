package dominio;

/**
 * Cancha 2D en vista lateral con sus limites y dos arcos. Es experta en saber
 * desde donde cae el balon y quien anota cuando entra en un arco.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Field {

    public static final double ANCHO = 900.0;
    public static final double SUELO_Y = 450.0;
    static final int SIN_GOL = 0;

    private static final double PARED_IZQUIERDA_X = 0.0;
    private static final double TECHO_Y = 0.0;
    private static final double CENTRO_X = ANCHO / 2.0;
    private static final double CAIDA_BALON_Y = 60.0;

    // Arco izquierdo (defiende el jugador 1, anota el 2) y derecho (defiende el 2, anota el 1)
    private final Goal[] arcos = { new Goal(PARED_IZQUIERDA_X, Goal.ANCHO, 2), new Goal(ANCHO, ANCHO - Goal.ANCHO, 1) };

    /**
     * Avanza el balon un paso de fisica (gravedad, suelo, techo) y el rebote en los travesaños.
     *
     * @param balon Balon.
     */
    void updateBall(Ball balon) {
        balon.updatePhysics(SUELO_Y, TECHO_Y);
        for (Goal arco : arcos) {
            arco.resolveCrossbar(balon);
        }
    }

    /**
     * Rebota el balon contra las paredes laterales y lo deja dentro de la cancha. Se aplica
     * una sola vez por paso, despues de los contactos, para cubrir tambien los empujes.
     *
     * @param balon Balon.
     */
    void keepBallInside(Ball balon) {
        balon.bounceOffWalls(PARED_IZQUIERDA_X, ANCHO);
    }

    /**
     * Posicion con la que se ve el balon entre dos pasos de fisica: avanza la fraccion
     * de paso pendiente sin salirse de las paredes ni hundirse en el suelo.
     *
     * @param balon    Balon.
     * @param fraccion Fraccion (0 a 1) del siguiente paso ya transcurrida.
     * @return Centro visible del balon como {x, y}.
     */
    double[] visibleBall(Ball balon, double fraccion) {
        double x = Math.max(PARED_IZQUIERDA_X + Ball.RADIO, Math.min(ANCHO - Ball.RADIO, balon.xAfter(fraccion)));
        double y = Math.min(SUELO_Y - Ball.RADIO, balon.yAfter(fraccion));
        return new double[] { x, y };
    }

    /** @return Cada arco (el izquierdo y luego el derecho) como {izquierda, arriba, ancho, alto}. */
    double[][] getGoalBounds() {
        double[][] limites = new double[arcos.length][];
        for (int i = 0; i < arcos.length; i++) {
            limites[i] = arcos[i].getBounds();
        }
        return limites;
    }

    /**
     * Deja caer el balon desde el centro superior de la cancha, sin inercia.
     *
     * @param balon Balon.
     */
    void dropBall(Ball balon) {
        balon.placeAt(CENTRO_X, CAIDA_BALON_Y);
    }

    /**
     * @param balon Balon.
     * @return Jugador (1 o 2) que anota si el balon esta completamente dentro de un arco;
     *         {@link #SIN_GOL} si no hay gol.
     */
    int scorerOf(Ball balon) {
        for (Goal arco : arcos) {
            if (arco.isBallInside(balon)) {
                return arco.getScorer();
            }
        }
        return SIN_GOL;
    }
}
