package dominio;

/**
 * Cancha 2D en vista lateral con sus limites y dos arcos.
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
    private final Goal[] arcos = { new Goal(PARED_IZQUIERDA_X, Goal.ANCHO, 2), new Goal(ANCHO, ANCHO - Goal.ANCHO, 1) };

    /**
     * Avanza el balon un paso (tiene en cuenta rebotes en los bordes del arco)
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
     * Rebota el balon contra las paredes laterales y lo deja dentro de la cancha.
     * Se aplica una sola vez por paso, despues de los contactos, para cubrir
     * tambien los empujes.
     *
     * @param balon Balon.
     */
    void keepBallInside(Ball balon) {
        balon.bounceOffWalls(PARED_IZQUIERDA_X, ANCHO);
    }

    /**
     * Posicion con la que se ve el balon entre dos pasos: avanza la
     * fraccion de paso pendiente sin salirse de las paredes ni hundirse.
     *
     * @param balon    Balon.
     * @param fraccion Fraccion del siguiente paso ya transcurrida.
     * @return Centro visible del balon como {x, y}.
     */
    double[] visibleBall(Ball balon, double fraccion) {
        double x = Math.max(PARED_IZQUIERDA_X + Ball.RADIO, Math.min(ANCHO - Ball.RADIO, balon.xAfter(fraccion)));
        double y = Math.min(SUELO_Y - Ball.RADIO, balon.yAfter(fraccion));
        return new double[] { x, y };
    }

    /**
     * @return Dimensiones y posicion de cada arco.
     */
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
     * @return Jugador (1 o 2) que anota si el balon esta completamente dentro de un
     *         arco y por defecto retorna que no hay gol.
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
