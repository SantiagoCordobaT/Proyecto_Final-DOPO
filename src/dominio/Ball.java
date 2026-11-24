package dominio;

/**
 * Balón del partido. Se mueve con velocidad continua en píxeles por segundo,
 * gravedad y rozamiento horizontal.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Ball {

    static final double RADIO = 15.0;
    private static final double GRAVEDAD = 1150.0;
    private static final double ROZAMIENTO_X = 280.0;
    private static final double REBOTE_SUELO = 0.55;
    private static final double REBOTE_PARED = 0.85;
    private static final double UMBRAL_GOLPE_FUERTE = 480.0;

    private double x;
    private double y;
    private double vx;
    private double vy;

    void ubicarEn(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = 0.0;
        this.vy = 0.0;
    }

    void avanzar(double segundos, double sueloY, double techoY) {
        if (segundos <= 0.0) {
            return;
        }
        vy += GRAVEDAD * segundos;
        x += vx * segundos;
        y += vy * segundos;

        aplicarRozamiento(segundos);

        if (y - RADIO <= techoY) {
            y = techoY + RADIO;
            if (vy < 0.0) {
                vy = -vy * 0.5;
            }
        }
        if (y + RADIO >= sueloY) {
            y = sueloY - RADIO;
            if (Math.abs(vy) > 90.0) {
                vy = -vy * REBOTE_SUELO;
            } else {
                vy = 0.0;
            }
        }
    }

    private void aplicarRozamiento(double segundos) {
        double perdida = ROZAMIENTO_X * segundos;
        if (Math.abs(vx) <= perdida) {
            vx = 0.0;
        } else if (vx > 0.0) {
            vx -= perdida;
        } else {
            vx += perdida;
        }
    }

    void rebotarEnParedes(double paredIzquierdaX, double paredDerechaX) {
        if (x - RADIO <= paredIzquierdaX) {
            x = paredIzquierdaX + RADIO;
            vx = Math.abs(vx) * REBOTE_PARED;
        }
        if (x + RADIO >= paredDerechaX) {
            x = paredDerechaX - RADIO;
            vx = -Math.abs(vx) * REBOTE_PARED;
        }
    }

    public void patear(int fuerza, int direccion) {
        vx = direccion * (260.0 + fuerza * 85.0);
        vy = -(110.0 + fuerza * 20.0);
    }

    public void patearArriba(int fuerza, int direccion) {
        vx = direccion * (140.0 + fuerza * 45.0);
        vy = -(310.0 + fuerza * 45.0);
    }

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
        vy = -220.0;
    }

    void rebotarSobreTravesano(double superficieY, int haciaCancha) {
        rebotarArriba(superficieY);
        if (Math.abs(vx) < 140.0) {
            vx = haciaCancha * 140.0;
        }
    }

    void rebotarAbajo(double superficieY) {
        y = superficieY + RADIO;
        if (vy < 0.0) {
            vy = 0.0;
        }
    }

    void rebotarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        double rapidez = Math.abs(vx) * REBOTE_PARED;
        if (rapidez < 220.0) {
            rapidez = 220.0;
        }
        vx = direccion * rapidez;
    }

    public void empujarDeLado(double bordeX, int direccion) {
        x = bordeX + direccion * RADIO;
        double rapidez = Math.abs(vx);
        if (rapidez < 180.0) {
            rapidez = 180.0;
        }
        vx = direccion * rapidez;
    }

    public int ladoRespectoA(double referenciaX) {
        return x >= referenciaX ? 1 : -1;
    }

    boolean esGolpeFuerte() {
        return Math.abs(vx) >= UMBRAL_GOLPE_FUERTE;
    }

    void frenar() {
        vx *= 0.5;
        vy *= 0.5;
    }

    public double obtenerX() {
        return x;
    }

    public double obtenerY() {
        return y;
    }
}
