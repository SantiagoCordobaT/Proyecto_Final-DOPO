package dominio;

/**
 * Representa un arco lateral de la cancha con su travesano superior.
 * Calcula los bordes horizontales segun si esta en la pared izquierda o
 * derecha,
 * detecta cuando entra un gol completo y resuelve los rebotes en el travesano.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class Goal {

    /** Ancho horizontal del arco. */
    static final double ANCHO = 90.0;
    /** Altura total del arco desde el suelo hasta lo alto del travesano. */
    static final double ALTO = 140.0;
    /** Grosor vertical del travesano. */
    static final double GROSOR_BORDE = 10.0;
    private static final double BORDE_ARRIBA = Field.SUELO_Y - ALTO;
    private static final double BORDE_ABAJO = BORDE_ARRIBA + GROSOR_BORDE;
    private final double izquierdaX;
    private final double derechaX;
    private final double bocaX;
    private final int direccionBoca;
    private final int anotador;

    /**
     * Construye un arco a partir de su pared de fondo y su entrada hacia la cancha,
     * determinando sus limites horizontales y el sentido de rebote frontal (1 o
     * -1).
     *
     * @param fondoX   Coordenada X pegada a la pared.
     * @param bocaX    Coordenada X de la entrada del arco (hacia la cancha).
     * @param anotador Jugador (1 o 2) que recibe el gol a favor cuando entra aqui
     *                 el balon.
     */
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

    /**
     * Comprueba si todo el balon quedo dentro del area valida del arco
     * (entre sus bordes laterales y por debajo del travesano hasta el suelo).
     *
     * @param balon Balon en juego.
     * @return true solo si el balon esta completamente dentro del arco.
     */
    boolean contieneBalon(Ball balon) {
        return balon.estaDentroDe(izquierdaX, BORDE_ABAJO, derechaX, Field.SUELO_Y);
    }

    /**
     * Resuelve la colision del balon contra el travesano: si pega por arriba
     * rebota hacia arriba; si pega por debajo cae hacia el arco; si pega de
     * frente en la punta sale rebotado horizontalmente hacia la cancha.
     *
     * @param balon Balon a corregir si toca el travesano.
     */
    void resolverTravesano(Ball balon) {
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

    /**
     * Entrega el rectangulo completo del arco (desde el techo del travesano hasta
     * el suelo) para que la vista pueda dibujarlo.
     *
     * @return Arreglo {izquierda, arriba, ancho, alto}.
     */
    double[] obtenerLimites() {
        return new double[] { izquierdaX, BORDE_ARRIBA, ANCHO, ALTO };
    }

    /**
     * Devuelve el numero del jugador al que se le suma el gol en este arco.
     *
     * @return Jugador anotador (1 o 2).
     */
    int obtenerAnotador() {
        return anotador;
    }
}
