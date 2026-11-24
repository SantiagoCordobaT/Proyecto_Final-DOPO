package dominio;

/**
 * Balón de fútbol 2D con física discreta por impulsos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Ball {

    static final double RADIO = 15.0;
    private static final double MARGEN_BORDE = 1e-6;
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

    /** Coloca el balón quieto en un punto de la cancha. */
    void ubicarEn(double x, double y) {
        this.x = x;
        this.y = y;
        impulsoDerecha = 0;
        impulsoIzquierda = 0;
        impulsoArriba = 0;
        velocidadX = RAPIDEZ_BASE_X;
    }

    /**
     * Mueve el balón un paso según sus impulsos activos, frenando en techo y suelo.
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

        boolean subio = false;
        if (impulsoArriba > 0) {
            subio = true;
            impulsoArriba--;
            y -= RAPIDEZ_BASE_Y;
        }
        if (y - RADIO <= techoY) {
            y = techoY + RADIO;
            impulsoArriba = 0;
        }
        if (!subio && y + RADIO < sueloY) {
            y = Math.min(sueloY - RADIO, y + RAPIDEZ_BASE_Y);
        }
    }

    /** Invierte la dirección horizontal si choca con las paredes laterales. */
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

    public void patear(int fuerza, int direccion) {
        aplicarPatada(direccion, MULTIPLICADOR_PATADA_X * fuerza, MULTIPLICADOR_PATADA_Y * fuerza);
    }

    public void patearArriba(int fuerza, int direccion) {
        aplicarPatada(direccion, MULTIPLICADOR_PATADA_ALTA_X * fuerza, MULTIPLICADOR_PATADA_ALTA_Y * fuerza);
    }

    private void aplicarPatada(int direccion, int impulsoHorizontal, int impulsoVertical) {
        establecerImpulsoHorizontal(direccion, impulsoHorizontal);
        impulsoArriba = impulsoVertical;
    }

    /** Comprueba si el círculo del balón toca o entra en un rectángulo. */
    public boolean seSolapaCon(double izquierda, double arriba, double derecha, double abajo) {
        double cercanoX = x;
        if (cercanoX < izquierda) {
            cercanoX = izquierda;
        } else if (cercanoX > derecha) {
            cercanoX = derecha;
        }

        double cercanoY = y;
        if (cercanoY < arriba) {
            cercanoY = arriba;
        } else if (cercanoY > abajo) {
            cercanoY = abajo;
        }

        double dx = x - cercanoX;
        double dy = y - cercanoY;
        return dx * dx + dy * dy <= RADIO * RADIO;
    }

    boolean estaDentroDe(double izquierda, double arriba, double derecha, double abajo) {
        return x - RADIO >= izquierda - MARGEN_BORDE
                && x + RADIO <= derecha + MARGEN_BORDE
                && y - RADIO >= arriba - MARGEN_BORDE
                && y + RADIO <= abajo + MARGEN_BORDE;
    }

    public boolean estaPorEncimaDe(double superficieY) {
        return y < superficieY;
    }

    boolean estaPorDebajoDe(double superficieY) {
        return y > superficieY;
    }

    public void rebotarArriba(double superficieY) {
        y = superficieY - RADIO;
        impulsoArriba = IMPULSO_REBOTE_ARRIBA;
    }

    void rebotarAbajo(double superficieY) {
        y = superficieY + RADIO;
        impulsoArriba = 0;
    }

    void rebotarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        int impulsoRebote = Math.max(direccion > 0 ? impulsoIzquierda : impulsoDerecha, IMPULSO_REBOTE_LADO);
        establecerImpulsoHorizontal(direccion, impulsoRebote);
    }

    public void empujarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        int impulsoEmpuje = Math.max(direccion > 0 ? impulsoDerecha : impulsoIzquierda, IMPULSO_EMPUJE);
        establecerImpulsoHorizontal(direccion, impulsoEmpuje);
    }

    private void establecerImpulsoHorizontal(int direccion, int impulso) {
        if (direccion > 0) {
            velocidadX = RAPIDEZ_BASE_X;
            impulsoDerecha = impulso;
            impulsoIzquierda = 0;
        } else if (direccion < 0) {
            velocidadX = -RAPIDEZ_BASE_X;
            impulsoIzquierda = impulso;
            impulsoDerecha = 0;
        }
    }

    public int ladoRespectoA(double referenciaX) {
        return x >= referenciaX ? 1 : -1;
    }

    boolean esGolpeFuerte() {
        return Math.max(impulsoDerecha, impulsoIzquierda) >= UMBRAL_GOLPE_FUERTE;
    }

    void frenar() {
        impulsoDerecha /= 2;
        impulsoIzquierda /= 2;
        impulsoArriba /= 2;
    }

    public double obtenerX() {
        return x;
    }

    public double obtenerY() {
        return y;
    }
}
