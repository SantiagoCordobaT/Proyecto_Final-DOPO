package dominio;

/**
 * Representa el balon de futbol 2D.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Ball {

    static final double RADIO = 15.0;
    private static final double GRAVEDAD = 0.45;
    private static final double REBOTE = 0.75;
    private static final double FRICCION_SUELO = 0.98;
    private static final double VELOCIDAD_REPOSO = 0.5;
    private static final double VELOCIDAD_PATADA_POR_FUERZA = 2.0;
    private static final double VELOCIDAD_EMPUJE = 3.0;
    private static final double VELOCIDAD_GOLPE_FUERTE = 12.0;
    private static final double FRENADO_GOLPE_FUERTE = 0.6;
    private static final double EPSILON = 1e-6;
    private double x;
    private double y;
    private double velocidadX;
    private double velocidadY;

    /**
     * Ubica el balon en un punto y cae limpiamente.
     *
     * @param x Coordenada horizontal del centro.
     * @param y Coordenada vertical del centro.
     */
    void ubicarEn(double x, double y) {
        this.x = x;
        this.y = y;
        this.velocidadX = 0.0;
        this.velocidadY = 0.0;
    }

    /**
     * Actualiza la posicion del balon aplicando gravedad, teniendo en cuenta
     * colisiones contra el suelo, el techo y las paredes.
     *
     * @param sueloY Coordenada Y de la superficie del suelo.
     * @param techoY Coordenada Y del limite superior de la cancha.
     */
    void actualizarFisica(double sueloY, double techoY) {
        velocidadY += GRAVEDAD;
        x += velocidadX;
        y += velocidadY;
        if (y + RADIO >= sueloY) {
            y = sueloY - RADIO;
            velocidadY = -velocidadY * REBOTE;
            velocidadX = velocidadX * FRICCION_SUELO;
            if (Math.abs(velocidadY) < VELOCIDAD_REPOSO) {
                velocidadY = 0.0;
            }
        }
        if (y - RADIO <= techoY) {
            y = techoY + RADIO;
            velocidadY = -velocidadY * REBOTE;
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
            velocidadX = -velocidadX * REBOTE;
        }
        if (x + RADIO >= paredDerechaX) {
            x = paredDerechaX - RADIO;
            velocidadX = -velocidadX * REBOTE;
        }
    }

    /**
     * Aplica el impacto de una patada (lanza el balon en el sentido de vista).
     *
     * @param fuerza    Estadística de fuerza del personaje.
     * @param direccion 1 derecha o -1 izquierda.
     */
    public void patear(int fuerza, int direccion) {
        double velocidadPatada = fuerza * VELOCIDAD_PATADA_POR_FUERZA;
        velocidadX += direccion * velocidadPatada;
        velocidadY -= velocidadPatada;
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
        velocidadY = -Math.abs(velocidadY) * REBOTE;
    }

    /**
     * Rebota hacia abajo segun una superficie.
     *
     * @param superficieY Altura de la superficie.
     */
    void rebotarAbajo(double superficieY) {
        y = superficieY + RADIO;
        velocidadY = Math.abs(velocidadY) * REBOTE;
    }

    /**
     * Rebota horizontalmente contra un borde vertical.
     *
     * @param bordeX    Coordenada X del borde.
     * @param direccion Sentido de salida: 1 derecha, -1 izquierda.
     */
    void rebotarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        velocidadX = direccion * Math.abs(velocidadX) * REBOTE;
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
        velocidadX = direccion * Math.max(Math.abs(velocidadX), VELOCIDAD_EMPUJE);
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
        return Math.sqrt(velocidadX * velocidadX + velocidadY * velocidadY) >= VELOCIDAD_GOLPE_FUERTE;
    }

    /** Frena el balon tras golpear con fuerza a un personaje. */
    void frenar() {
        velocidadX *= FRENADO_GOLPE_FUERTE;
        velocidadY *= FRENADO_GOLPE_FUERTE;
    }

    /**
     * @param fraccion Fraccion del siguiente paso. (horizontalmente)
     * 
     * @return Coordenada horizontal que tendra el centro al avanzar esa fraccion de
     *         paso.
     */
    double xDespuesDe(double fraccion) {
        return x + velocidadX * fraccion;
    }

    /**
     * @param fraccion Fraccion del siguiente paso. (verticalmente)
     * @return Coordenada vertical que tendra el centro al avanzar esa fraccion de
     *         paso.
     */
    double yDespuesDe(double fraccion) {
        return y + velocidadY * fraccion;
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
