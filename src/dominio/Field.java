package dominio;

/**
 * Cancha 2D en vista lateral de 900 x 450 con paredes laterales, techo,
 * suelo y los dos arcos. Se encarga de mover el balon dentro de sus limites
 * y detectar cuando entra un gol completo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Field {

    /** Ancho total de la cancha en pixeles logicos. */
    public static final double ANCHO = 900.0;
    /** Coordenada vertical del suelo (Y crece hacia abajo). */
    public static final double SUELO_Y = 450.0;
    /** Valor devuelto por anotadorDe cuando ningun arco tiene el balon dentro. */
    static final int SIN_GOL = 0;
    private static final double PARED_IZQUIERDA_X = 0.0;
    private static final double TECHO_Y = 0.0;
    private static final double CENTRO_X = ANCHO / 2.0;
    private static final double CAIDA_BALON_Y = 60.0;
    private Goal arcoIzquierdo;
    private Goal arcoDerecho;

    public Field() {
        this.arcoIzquierdo = new Goal(PARED_IZQUIERDA_X, Goal.ANCHO, 2);
        this.arcoDerecho = new Goal(ANCHO, ANCHO - Goal.ANCHO, 1);
    }

    /**
     * Avanza un paso fisico del balon (gravedad, suelo y techo) y revisa si
     * rebota contra el borde superior de alguno de los dos arcos.
     *
     * @param balon Balon en juego.
     */
    void actualizarBalon(Ball balon) {
        balon.actualizarFisica(SUELO_Y, TECHO_Y);
        this.arcoIzquierdo.resolverBorde(balon);
        this.arcoDerecho.resolverBorde(balon);
    }

    /**
     * Rebota el balon contra las paredes laterales para que no salga de la
     * cancha. Se ejecuta una vez por paso, despues de los empujes y patadas,
     * para evitar que un jugador lo saque del campo.
     *
     * @param balon Balon en juego.
     */
    void mantenerBalonDentro(Ball balon) {
        balon.rebotarEnParedes(PARED_IZQUIERDA_X, ANCHO);
    }

    /**
     * Calcula la posicion del balon para dibujarlo entre dos ticks, evitando que
     * atraviese las paredes laterales o el suelo al interpolar.
     *
     * @param balon    Balon en juego.
     * @param fraccion Fraccion del siguiente paso ya transcurrida.
     * @return Centro visible del balon como {x, y}.
     */
    double[] balonVisible(Ball balon, double fraccion) {
        double xMinimo = PARED_IZQUIERDA_X + Ball.RADIO;
        double xMaximo = ANCHO - Ball.RADIO;
        double yMaximo = SUELO_Y - Ball.RADIO;
        double x = balon.xDespuesDe(fraccion);
        if (x < xMinimo) {
            x = xMinimo;
        } else if (x > xMaximo) {
            x = xMaximo;
        }
        double y = balon.yDespuesDe(fraccion);
        if (y > yMaximo) {
            y = yMaximo;
        }

        return new double[] { x, y };
    }

    /**
     * Devuelve los rectangulos de ambos arcos (izquierdo y derecho) para que la
     * vista pueda dibujarlos en pantalla.
     *
     * @return Matriz con {izquierda, arriba, ancho, alto} de cada arco.
     */
    double[][] obtenerLimitesArcos() {
        return new double[][] { arcoIzquierdo.obtenerLimites(), arcoDerecho.obtenerLimites() };
    }

    /**
     * Coloca el balon quieto en el centro superior de la cancha al iniciar o
     * despues de un gol.
     *
     * @param balon Balon a reiniciar.
     */
    void soltarBalon(Ball balon) {
        balon.ubicarEn(CENTRO_X, CAIDA_BALON_Y);
    }

    /**
     * Revisa si el balon entro por completo en alguno de los dos arcos e indica
     * a que jugador se le acredita el gol.
     *
     * @param balon Balon en juego.
     * @return Jugador (1 o 2) que anota, o SIN_GOL (0) si no hay gol.
     */
    int anotadorDe(Ball balon) {
        if (arcoIzquierdo.contieneBalon(balon)) {
            return arcoIzquierdo.obtenerAnotador();
        }
        if (arcoDerecho.contieneBalon(balon)) {
            return arcoDerecho.obtenerAnotador();
        }
        return SIN_GOL;
    }
}
