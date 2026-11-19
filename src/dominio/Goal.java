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

    static final double ANCHO = 90.0;
    static final double ALTO = 140.0;
    static final double GROSOR_TRAVESANO = 10.0;

    private static final double TRAVESANO_ARRIBA = Field.SUELO_Y - ALTO;
    private static final double TRAVESANO_ABAJO = TRAVESANO_ARRIBA + GROSOR_TRAVESANO;

    private final double izquierdaX;
    private final double derechaX;
    private final double bocaX;
    private final int direccionBoca;
    private final int anotador;

    /**
     * @param fondoX   Coordenada X del fondo del arco (pegado a la pared).
     * @param bocaX    Coordenada X de la boca del arco (mira hacia la cancha).
     * @param anotador Jugador (1 o 2) que anota cuando el balon entra en este arco.
     */
    Goal(double fondoX, double bocaX, int anotador) {
        this.izquierdaX = Math.min(fondoX, bocaX);
        this.derechaX = Math.max(fondoX, bocaX);
        this.bocaX = bocaX;
        this.direccionBoca = (int) Math.signum(bocaX - fondoX);
        this.anotador = anotador;
    }

    /**
     * @param balon Balon.
     * @return true solo si el balon esta completamente dentro del area del arco.
     */
    boolean isBallInside(Ball balon) {
        return balon.isInside(izquierdaX, TRAVESANO_ABAJO, derechaX, Field.SUELO_Y);
    }

    /**
     * Rebota el balon contra el travesaño (por arriba, por debajo o de frente).
     *
     * @param balon Balon a corregir.
     */
    void resolveCrossbar(Ball balon) {
        if (!balon.overlaps(izquierdaX, TRAVESANO_ARRIBA, derechaX, TRAVESANO_ABAJO)) {
            return;
        }
        if (balon.isAbove(TRAVESANO_ARRIBA)) {
            balon.bounceUp(TRAVESANO_ARRIBA);
        } else if (balon.isBelow(TRAVESANO_ABAJO)) {
            balon.bounceDown(TRAVESANO_ABAJO);
        } else {
            balon.bounceSideways(bocaX, direccionBoca);
        }
    }

    /** @return Arco desde el suelo hasta lo alto del travesaño, como {izquierda, arriba, ancho, alto}. */
    double[] getBounds() {
        return new double[] { izquierdaX, TRAVESANO_ARRIBA, derechaX - izquierdaX, Field.SUELO_Y - TRAVESANO_ARRIBA };
    }

    /** @return Jugador (1 o 2) que anota en este arco. */
    int getScorer() {
        return anotador;
    }
}
