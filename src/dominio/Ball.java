package dominio;

/**
 * Representa el balon de futbol 2D.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Ball {

    static final double RADIO = 15.0;
    private static final double EPSILON = 1e-6;
    private static final double RAPIDEZ_BASE_X = 6.0;
    private static final double RAPIDEZ_BASE_Y = 5.0;
    private static final int MULTIPLICADOR_PATADA_X = 18;
    private static final int MULTIPLICADOR_PATADA_Y = 8;
    private static final int MULTIPLICADOR_PATADA_ALTA_X = 10;
    private static final int MULTIPLICADOR_PATADA_ALTA_Y = 12;
    private static final int IMPULSO_REBOTE_ARRIBA = 12;
    private static final int IMPULSO_REBOTE_LADO = 10;
    private static final int IMPULSO_EMPUJE = 8;
    private static final int UMBRAL_GOLPE_FUERTE = 40;
    private double x;
    private double y;
    private double velocidadX = RAPIDEZ_BASE_X;
    private int impulsoDerecha;
    private int impulsoIzquierda;
    private int impulsoArriba;
    private boolean enElAire;

    /**
     * Ubica el balon en un punto y cae limpiamente.
     *
     * @param x Coordenada horizontal del centro.
     * @param y Coordenada vertical del centro.
     */
    void ubicarEn(double x, double y) {
        this.x = x;
        this.y = y;
        impulsoDerecha = 0;
        impulsoIzquierda = 0;
        impulsoArriba = 0;
        enElAire = false;
        velocidadX = RAPIDEZ_BASE_X;
    }

    /**
     * Actualiza la posicion del balon aplicando gravedad, teniendo en cuenta
     * colisiones contra el suelo, el techo y las paredes.
     *
     * @param sueloY Coordenada Y de la superficie del suelo.
     * @param techoY Coordenada Y del limite superior de la cancha.
     */
    void actualizarFisica(double sueloY, double techoY) {
        if (impulsoDerecha > 0) {
            x += velocidadX;
            impulsoDerecha--;
            impulsoIzquierda = 0;
        } else if (impulsoIzquierda > 0) {
            x += velocidadX;
            impulsoIzquierda--;
            impulsoDerecha = 0;
        }

        if (impulsoArriba > 0) {
            enElAire = true;
            impulsoArriba--;
            y -= RAPIDEZ_BASE_Y;
        } else {
            enElAire = false;
        }
        if (y - RADIO <= techoY) {
            y = techoY + RADIO;
            impulsoArriba = 0;
        }
        if (!enElAire && y + RADIO < sueloY) {
            y = Math.min(sueloY - RADIO, y + RAPIDEZ_BASE_Y);
        }
    }

    /**
     * Rebota contra las paredes laterales si el balon las toca.
     *
     * @param paredIzquierdaX Coordenada X del límite lateral izquierdo.
     * @param paredDerechaX   Coordenada X del límite lateral derecho.
     */
    void rebotarEnParedes(double paredIzquierdaX, double paredDerechaX) {
        if (x - RADIO <= paredIzquierdaX) {
            x = paredIzquierdaX + RADIO;
            velocidadX = Math.abs(velocidadX);
            if (impulsoIzquierda > 0) {
                impulsoDerecha = impulsoIzquierda;
                impulsoIzquierda = 0;
            }
        }

        if (x + RADIO >= paredDerechaX) {
            x = paredDerechaX - RADIO;
            velocidadX = -Math.abs(velocidadX);
            if (impulsoDerecha > 0) {
                impulsoIzquierda = impulsoDerecha;
                impulsoDerecha = 0;
            }
        }
    }

    /**
     * Aplica el impacto de una patada (lanza el balon en el sentido de vista).
     *
     * @param fuerza    Estadística de fuerza del personaje.
     * @param direccion 1 derecha o -1 izquierda.
     */
    public void patear(int fuerza, int direccion) {
        aplicarPatada(direccion, MULTIPLICADOR_PATADA_X * fuerza, MULTIPLICADOR_PATADA_Y * fuerza);
    }

    /**
     * Aplica una patada elevada o cabezazo (mas impulso vertical y menor alcance
     * horizontal para superar al rival por aire).
     *
     * @param fuerza    Estadística de fuerza del personaje.
     * @param direccion 1 derecha o -1 izquierda.
     */
    public void patearArriba(int fuerza, int direccion) {
        aplicarPatada(direccion, MULTIPLICADOR_PATADA_ALTA_X * fuerza, MULTIPLICADOR_PATADA_ALTA_Y * fuerza);
    }

    /** Ejecuta una patada parametrizada por su impulso horizontal y vertical. */
    private void aplicarPatada(int direccion, int impulsoHorizontal, int impulsoVertical) {
        establecerImpulsoHorizontal(direccion, impulsoHorizontal);
        impulsoArriba = impulsoVertical;
    }

    /**
     * @param izquierda Borde izquierdo del rectangulo.
     * @param arriba    Borde superior del rectangulo.
     * @param derecha   Borde derecho del rectangulo.
     * @param abajo     Borde inferior del rectangulo.
     * @return true si el balon toca o se solapa con el rectangulo.
     */
    public boolean seSolapaCon(double izquierda, double arriba, double derecha, double abajo) {
        double dx = x - Math.max(izquierda, Math.min(x, derecha));
        double dy = y - Math.max(arriba, Math.min(y, abajo));
        return dx * dx + dy * dy <= RADIO * RADIO;
    }

    /**
     * @param izquierda Borde izquierdo del rectangulo.
     * @param arriba    Borde superior del rectangulo.
     * @param derecha   Borde derecho del rectangulo.
     * @param abajo     Borde inferior del rectangulo.
     * @return true solo si el balon esta completamente dentro del rectangulo.
     */
    boolean estaDentroDe(double izquierda, double arriba, double derecha, double abajo) {
        return x - RADIO >= izquierda - EPSILON
                && x + RADIO <= derecha + EPSILON
                && y - RADIO >= arriba - EPSILON
                && y + RADIO <= abajo + EPSILON;
    }

    /**
     * @param superficieY Altura de referencia.
     * @return true si el centro del balon esta por encima de esa altura.
     */
    public boolean estaPorEncimaDe(double superficieY) {
        return y < superficieY;
    }

    /**
     * @param superficieY Altura de referencia.
     * @return true si el centro del balon esta por debajo de esa altura.
     */
    boolean estaPorDebajoDe(double superficieY) {
        return y > superficieY;
    }

    /**
     * Rebota hacia arriba sobre una superficie (cabeza de un personaje o borde de
     * la cancha).
     *
     * @param superficieY Altura de la superficie.
     */
    public void rebotarArriba(double superficieY) {
        y = superficieY - RADIO;
        enElAire = true;
        impulsoArriba = IMPULSO_REBOTE_ARRIBA;
    }

    /**
     * Rebota hacia abajo segun una superficie.
     *
     * @param superficieY Altura de la superficie.
     */
    void rebotarAbajo(double superficieY) {
        y = superficieY + RADIO;
        impulsoArriba = 0;
        enElAire = false;
    }

    /**
     * Rebota horizontalmente contra un borde vertical.
     *
     * @param bordeX    Coordenada X del borde.
     * @param direccion Sentido de salida: 1 derecha, -1 izquierda.
     */
    void rebotarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        int impulsoRebote = Math.max(direccion > 0 ? impulsoIzquierda : impulsoDerecha, IMPULSO_REBOTE_LADO);
        establecerImpulsoHorizontal(direccion, impulsoRebote);
    }

    /**
     * Sale empujado por un cuerpo hacia un lado, con al menos la velocidad de
     * empuje.
     *
     * @param bordeX    Coordenada X del borde del cuerpo que empuja.
     * @param direccion Sentido de salida: 1 derecha, -1 izquierda.
     */
    public void empujarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        int impulsoEmpuje = Math.max(direccion > 0 ? impulsoDerecha : impulsoIzquierda, IMPULSO_EMPUJE);
        establecerImpulsoHorizontal(direccion, impulsoEmpuje);
    }

    /** Asigna la direccion horizontal y activa solo el impulso hacia ese lado. */
    private void establecerImpulsoHorizontal(int direccion, int impulso) {
        if (direccion > 0) {
            velocidadX = Math.abs(RAPIDEZ_BASE_X);
            impulsoDerecha = impulso;
            impulsoIzquierda = 0;
        } else if (direccion < 0) {
            velocidadX = -Math.abs(RAPIDEZ_BASE_X);
            impulsoIzquierda = impulso;
            impulsoDerecha = 0;
        }
    }

    /**
     * @param referenciaX Coordenada X de referencia.
     * @return 1 derecha de la referencia (o sobre ella) y -1 si esta a la
     *         izquierda.
     */
    public int ladoRespectoA(double referenciaX) {
        return x >= referenciaX ? 1 : -1;
    }

    /**
     * @return true si el balon va tan rapido como para aturdir a otro jugador.
     */
    boolean esGolpeFuerte() {
        return Math.max(impulsoDerecha, impulsoIzquierda) >= UMBRAL_GOLPE_FUERTE;
    }

    /** Frena el balon tras golpear con fuerza a un personaje. */
    void frenar() {
        impulsoDerecha /= 2;
        impulsoIzquierda /= 2;
        impulsoArriba /= 2;
    }

    /**
     * @param fraccion Fraccion del siguiente paso. (horizontalmente)
     * 
     * @return Coordenada horizontal que tendra el centro al avanzar esa fraccion de
     *         paso.
     */
    double xDespuesDe(double fraccion) {
        if (impulsoDerecha > 0 || impulsoIzquierda > 0) {
            return x + velocidadX * fraccion;
        }
        return x;
    }

    /**
     * @param fraccion Fraccion del siguiente paso. (verticalmente)
     * @return Coordenada vertical que tendra el centro al avanzar esa fraccion de
     *         paso.
     */
    double yDespuesDe(double fraccion) {
        if (impulsoArriba > 0) {
            return y - RAPIDEZ_BASE_Y * fraccion;
        }
        if (!enElAire) {
            return y + RAPIDEZ_BASE_Y * fraccion;
        }
        return y;
    }

    /** @return Coordenada horizontal del centro. */
    public double obtenerX() {
        return x;
    }

    /** @return Coordenada vertical del centro. */
    public double obtenerY() {
        return y;
    }
}
