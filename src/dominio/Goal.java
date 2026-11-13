package dominio;

/**
 * Arco de la cancha. El area interior esta bajo el travesaño; el balon solo es
 * gol cuando esta completamente dentro de esa area. El arco sabe que jugador
 * anota en el, asi el autogol queda cubierto sin codigo especial.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class Goal {

    static final double WIDTH = 90.0;
    static final double HEIGHT = 140.0;
    static final double CROSSBAR_THICKNESS = 10.0;

    private static final double CROSSBAR_TOP = Field.GROUND_Y - HEIGHT;
    private static final double CROSSBAR_BOTTOM = CROSSBAR_TOP + CROSSBAR_THICKNESS;

    private final double leftX;
    private final double rightX;
    private final double mouthX;
    private final int mouthDirection;
    private final int scorer;

    /**
     * @param backX Coordenada X del fondo del arco (pegado a la pared).
     * @param mouthX Coordenada X de la boca del arco (mira hacia la cancha).
     * @param scorer Jugador (1 o 2) que anota cuando el balon entra en este arco.
     */
    Goal(double backX, double mouthX, int scorer) {
        this.leftX = Math.min(backX, mouthX);
        this.rightX = Math.max(backX, mouthX);
        this.mouthX = mouthX;
        this.mouthDirection = (int) Math.signum(mouthX - backX);
        this.scorer = scorer;
    }

    /**
     * @param ball Balon.
     * @return true solo si el balon esta completamente dentro del area del arco.
     */
    boolean isBallInside(Ball ball) {
        return ball.isInside(leftX, CROSSBAR_BOTTOM, rightX, Field.GROUND_Y);
    }

    /**
     * Rebota el balon contra el travesaño (por arriba, por debajo o de frente).
     *
     * @param ball Balon a corregir.
     */
    void resolveCrossbar(Ball ball) {
        if (!ball.overlaps(leftX, CROSSBAR_TOP, rightX, CROSSBAR_BOTTOM)) {
            return;
        }
        if (ball.isAbove(CROSSBAR_TOP)) {
            ball.bounceUp(CROSSBAR_TOP);
        } else if (ball.isBelow(CROSSBAR_BOTTOM)) {
            ball.bounceDown(CROSSBAR_BOTTOM);
        } else {
            ball.bounceSideways(mouthX, mouthDirection);
        }
    }

    /** @return Jugador (1 o 2) que anota en este arco. */
    int getScorer() {
        return scorer;
    }
}
