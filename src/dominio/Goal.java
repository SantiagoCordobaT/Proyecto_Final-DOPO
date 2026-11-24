package dominio;

/**
 * Modela un arco lateral para detectar goles y rebotes en el travesaño.
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

    Goal(double fondoX, double bocaX, int anotador) {
        this.bocaX = bocaX;
        if (fondoX < bocaX) {
            this.izquierdaX = fondoX;
            this.derechaX = bocaX;
            this.direccionBoca = 1;
        } else {
            this.izquierdaX = bocaX;
            this.derechaX = fondoX;
            this.direccionBoca = -1;
        }
        this.anotador = anotador;
    }

    boolean contieneBalon(Ball balon) {
        return balon.estaDentroDe(izquierdaX, BORDE_ABAJO, derechaX, Field.SUELO_Y);
    }

    void resolverBorde(Ball balon) {
        if (!balon.seSolapaCon(izquierdaX, BORDE_ARRIBA, derechaX, BORDE_ABAJO)) {
            return;
        }
        if (balon.estaPorEncimaDe(BORDE_ARRIBA)) {
            balon.rebotarArriba(BORDE_ARRIBA);
        } else if (balon.estaPorDebajoDe(BORDE_ABAJO)) {
            balon.rebotarAbajo(BORDE_ABAJO);
        } else {
            balon.rebotarDeLado(bocaX, direccionBoca);
        }
    }

    double[] obtenerLimites() {
        return new double[] { izquierdaX, BORDE_ARRIBA, ANCHO, ALTO };
    }

    int obtenerAnotador() {
        return anotador;
    }
}
