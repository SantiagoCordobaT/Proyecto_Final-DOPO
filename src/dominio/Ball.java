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
    void placeAt(double x, double y) {
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
    void updatePhysics(double sueloY, double techoY) {
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
    void bounceOffWalls(double paredIzquierdaX, double paredDerechaX) {
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
     * Aplica el impacto de una patada: empuja en la dirección de ataque y levanta
     * el balón.
     *
     * @param fuerza    Estadística de fuerza del personaje (de 1 a 5).
     * @param direccion Sentido horizontal: 1 (hacia la derecha) o -1 (hacia la
     *                  izquierda).
     */
    public void kick(int fuerza, int direccion) {
        double velocidadPatada = fuerza * VELOCIDAD_PATADA_POR_FUERZA;
        velocidadX += direccion * velocidadPatada;
        velocidadY -= velocidadPatada;
    }

    /**
     * @param izquierda Borde izquierdo del rectángulo.
     * @param arriba    Borde superior del rectángulo.
     * @param derecha   Borde derecho del rectángulo.
     * @param abajo     Borde inferior del rectángulo.
     * @return true si el balón toca o se solapa con el rectángulo.
     */
    public boolean overlaps(double izquierda, double arriba, double derecha, double abajo) {
        double dx = x - Math.max(izquierda, Math.min(x, derecha));
        double dy = y - Math.max(arriba, Math.min(y, abajo));
        return dx * dx + dy * dy <= RADIO * RADIO;
    }

    /**
     * @param izquierda Borde izquierdo del rectángulo.
     * @param arriba    Borde superior del rectángulo.
     * @param derecha   Borde derecho del rectángulo.
     * @param abajo     Borde inferior del rectángulo.
     * @return true solo si el balón está completamente dentro del rectángulo.
     */
    boolean isInside(double izquierda, double arriba, double derecha, double abajo) {
        return x - RADIO >= izquierda - EPSILON
                && x + RADIO <= derecha + EPSILON
                && y - RADIO >= arriba - EPSILON
                && y + RADIO <= abajo + EPSILON;
    }

    /**
     * @param superficieY Altura de referencia.
     * @return true si el centro del balón está por encima de esa altura.
     */
    public boolean isAbove(double superficieY) {
        return y < superficieY;
    }

    /**
     * @param superficieY Altura de referencia.
     * @return true si el centro del balón está por debajo de esa altura.
     */
    boolean isBelow(double superficieY) {
        return y > superficieY;
    }

    /**
     * Rebota hacia arriba sobre una superficie (cabeza de un personaje o
     * travesaño).
     *
     * @param superficieY Altura de la superficie.
     */
    public void bounceUp(double superficieY) {
        y = superficieY - RADIO;
        velocidadY = -Math.abs(velocidadY) * REBOTE;
    }

    /**
     * Rebota hacia abajo bajo una superficie (parte inferior del travesaño).
     *
     * @param superficieY Altura de la superficie.
     */
    void bounceDown(double superficieY) {
        y = superficieY + RADIO;
        velocidadY = Math.abs(velocidadY) * REBOTE;
    }

    /**
     * Rebota horizontalmente contra un borde vertical (frente del travesaño).
     *
     * @param bordeX    Coordenada X del borde.
     * @param direccion Sentido de salida: 1 derecha, -1 izquierda.
     */
    void bounceSideways(double bordeX, int direccion) {
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
    public void pushSideways(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        velocidadX = direccion * Math.max(Math.abs(velocidadX), VELOCIDAD_EMPUJE);
    }

    /**
     * @param referenciaX Coordenada X de referencia.
     * @return 1 si el balón está a la derecha de la referencia (o justo sobre
     *         ella), -1 si está a la izquierda.
     */
    public int sideOf(double referenciaX) {
        return x >= referenciaX ? 1 : -1;
    }

    /**
     * @return true si el balón va lo bastante rápido para aturdir a quien golpea.
     */
    boolean isHardHit() {
        return Math.sqrt(velocidadX * velocidadX + velocidadY * velocidadY) >= VELOCIDAD_GOLPE_FUERTE;
    }

    /** Frena el balón tras golpear con fuerza a un personaje. */
    void slowDown() {
        velocidadX *= FRENADO_GOLPE_FUERTE;
        velocidadY *= FRENADO_GOLPE_FUERTE;
    }

    /**
     * @param fraccion Fraccion (0 a 1) del siguiente paso de fisica ya
     *                 transcurrida.
     * @return Coordenada horizontal que tendra el centro al avanzar esa fraccion de
     *         paso.
     */
    double xAfter(double fraccion) {
        return x + velocidadX * fraccion;
    }

    /**
     * @param fraccion Fraccion (0 a 1) del siguiente paso de fisica ya
     *                 transcurrida.
     * @return Coordenada vertical que tendra el centro al avanzar esa fraccion de
     *         paso.
     */
    double yAfter(double fraccion) {
        return y + velocidadY * fraccion;
    }

    /** @return Coordenada horizontal del centro. */
    public double getX() {
        return x;
    }

    /** @return Coordenada vertical del centro. */
    public double getY() {
        return y;
    }
}
