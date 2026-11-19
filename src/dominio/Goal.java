package dominio;

/**
 * Arco de la cancha.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class Goal {

    static final double ANCHO = 90.0;
    static final double ALTO = 140.0;
    static final double GROSOR_BORDE = 10.0;
    private static final double BORDE_ARRIBA = Field.SUELO_Y - ALTO;
    private static final double BORDE_ABAJO = BORDE_ARRIBA + GROSOR_BORDE;
    private final double izquierdaX;
    private final double derechaX;
    private final double bocaX;
    private final int direccionBoca;
    private final int anotador;

    /**
     * @param fondoX   Coordenada X pegado a la pared.
     * @param bocaX    Coordenada X de la boca del arco (hacia la cancha).
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
    boolean contieneBalon(Ball balon) {
        return balon.estaDentroDe(izquierdaX, BORDE_ABAJO, derechaX, Field.SUELO_Y);
    }

    /**
     * Rebota el balon contra el travesaño (por arriba, por debajo o de frente).
     *
     * @param balon Balon a corregir.
     */
    void resolverTravesano(Ball balon) {
        if (!balon.seSolapaCon(izquierdaX, BORDE_ARRIBA, derechaX, BORDE_ABAJO)) {
            return;
        }
        if (balon.estaPorEncimaDe(BORDE_ABAJO)) {
            balon.rebotarArriba(BORDE_ARRIBA);
        } else if (balon.estaPorDebajoDe(BORDE_ABAJO)) {
            balon.rebotarAbajo(BORDE_ABAJO);
        } else {
            balon.rebotarDeLado(bocaX, direccionBoca);
        }
    }

    /**
     * @return Arco desde el suelo hasta lo alto del travesaño, como {izquierda,
     *         arriba, ancho, alto}.
     */
    double[] obtenerLimites() {
        return new double[] { izquierdaX, BORDE_ARRIBA, derechaX - izquierdaX, Field.SUELO_Y - BORDE_ARRIBA };
    }

    /** @return Jugador (1 o 2) que anota en este arco. */
    int obtenerAnotador() {
        return anotador;
    }
}
