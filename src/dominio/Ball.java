package dominio;

/**
 * Balón del partido. Se mueve por impulsos discretos y detecta choques usando
 * su caja alrededor del centro.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Ball {

    static final double RADIO = 15.0;
    private static final double RAPIDEZ_X = 6.0;
    private static final double RAPIDEZ_Y = 5.0;
    private static final int PATADA_X = 18;
    private static final int PATADA_Y = 8;
    private static final int PATADA_ALTA_X = 10;
    private static final int PATADA_ALTA_Y = 12;
    private static final int REBOTE_ARRIBA = 12;
    private static final int REBOTE_LADO = 10;
    private static final int EMPUJE = 8;
    private static final int UMBRAL_GOLPE_FUERTE = 40;

    private double x;
    private double y;
    private double velocidadX = RAPIDEZ_X;
    private int impulsoDerecha;
    private int impulsoIzquierda;
    private int impulsoArriba;

    void ubicarEn(double x, double y) {
        this.x = x;
        this.y = y;
        impulsoDerecha = 0;
        impulsoIzquierda = 0;
        impulsoArriba = 0;
        velocidadX = RAPIDEZ_X;
    }

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
            y -= RAPIDEZ_Y;
        }
        if (y - RADIO <= techoY) {
            y = techoY + RADIO;
            impulsoArriba = 0;
        }
        if (!subio && y + RADIO < sueloY) {
            y += RAPIDEZ_Y;
            if (y > sueloY - RADIO) {
                y = sueloY - RADIO;
            }
        }
    }

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
        establecerImpulsoHorizontal(direccion, PATADA_X * fuerza);
        impulsoArriba = PATADA_Y * fuerza;
    }

    public void patearArriba(int fuerza, int direccion) {
        establecerImpulsoHorizontal(direccion, PATADA_ALTA_X * fuerza);
        impulsoArriba = PATADA_ALTA_Y * fuerza;
    }

    // Colisión simple de rectángulos entre la caja del balón y un área dada
    public boolean seSolapaCon(double izquierda, double arriba, double derecha, double abajo) {
        return x + RADIO >= izquierda && x - RADIO <= derecha
                && y + RADIO >= arriba && y - RADIO <= abajo;
    }

    boolean estaDentroDe(double izquierda, double arriba, double derecha, double abajo) {
        return x - RADIO >= izquierda && x + RADIO <= derecha
                && y - RADIO >= arriba && y + RADIO <= abajo;
    }

    public boolean estaPorEncimaDe(double superficieY) {
        return y < superficieY;
    }

    boolean estaPorDebajoDe(double superficieY) {
        return y > superficieY;
    }

    public void rebotarArriba(double superficieY) {
        y = superficieY - RADIO;
        impulsoArriba = REBOTE_ARRIBA;
    }

    void rebotarAbajo(double superficieY) {
        y = superficieY + RADIO;
        impulsoArriba = 0;
    }

    void rebotarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        int impulsoRebote = direccion > 0 ? impulsoIzquierda : impulsoDerecha;
        if (impulsoRebote < REBOTE_LADO) {
            impulsoRebote = REBOTE_LADO;
        }
        establecerImpulsoHorizontal(direccion, impulsoRebote);
    }

    public void empujarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        int impulsoEmpuje = direccion > 0 ? impulsoDerecha : impulsoIzquierda;
        if (impulsoEmpuje < EMPUJE) {
            impulsoEmpuje = EMPUJE;
        }
        establecerImpulsoHorizontal(direccion, impulsoEmpuje);
    }

    private void establecerImpulsoHorizontal(int direccion, int impulso) {
        if (direccion > 0) {
            velocidadX = RAPIDEZ_X;
            impulsoDerecha = impulso;
            impulsoIzquierda = 0;
        } else if (direccion < 0) {
            velocidadX = -RAPIDEZ_X;
            impulsoIzquierda = impulso;
            impulsoDerecha = 0;
        }
    }

    public int ladoRespectoA(double referenciaX) {
        return x >= referenciaX ? 1 : -1;
    }

    boolean esGolpeFuerte() {
        return impulsoDerecha >= UMBRAL_GOLPE_FUERTE || impulsoIzquierda >= UMBRAL_GOLPE_FUERTE;
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
