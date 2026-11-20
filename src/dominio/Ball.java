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
    private double x;
    private double y;
    private static final double RAPIDEZ_BASE_X = 6.0;
    private static final double RAPIDEZ_BASE_Y = 5.0;
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
        // Paso 3: mantener entre paredIzquierdaX y paredDerechaX e invertir direccion
        // horizontal
    }

    /**
     * Aplica el impacto de una patada (lanza el balon en el sentido de vista).
     *
     * @param fuerza    Estadística de fuerza del personaje.
     * @param direccion 1 derecha o -1 izquierda.
     */
    public void patear(int fuerza, int direccion) {
        // Paso 4: asignar impulsos horizontal y vertical segun fuerza y direccion
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
        // Paso 5: activar enElAire / impulsoArriba si cae sobre travesano o cabeza
    }

    /**
     * Rebota hacia abajo segun una superficie.
     *
     * @param superficieY Altura de la superficie.
     */
    void rebotarAbajo(double superficieY) {
        y = superficieY + RADIO;
        // Paso 5: cortar impulsoArriba y enElAire al golpear por debajo
    }

    /**
     * Rebota horizontalmente contra un borde vertical.
     *
     * @param bordeX    Coordenada X del borde.
     * @param direccion Sentido de salida: 1 derecha, -1 izquierda.
     */
    void rebotarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        // Paso 5: dirigir el movimiento horizontal hacia direccion
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
        // Paso 5: dar impulso hacia direccion al ser empujado por un jugador
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
        // Paso 6: indicar si el balon aun tiene suficiente impulso como para aturdir
        return false;
    }

    /** Frena el balon tras golpear con fuerza a un personaje. */
    void frenar() {
        // Paso 6: reducir los impulsos activos tras golpear a un personaje
    }

    /**
     * @param fraccion Fraccion del siguiente paso. (horizontalmente)
     * 
     * @return Coordenada horizontal que tendra el centro al avanzar esa fraccion de
     *         paso.
     */
    double xDespuesDe(double fraccion) {
        // Paso 7 (opcional): sumar el avance horizontal del paso * fraccion
        return x;
    }

    /**
     * @param fraccion Fraccion del siguiente paso. (verticalmente)
     * @return Coordenada vertical que tendra el centro al avanzar esa fraccion de
     *         paso.
     */
    double yDespuesDe(double fraccion) {
        // Paso 7 (opcional): sumar el avance vertical del paso * fraccion
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
