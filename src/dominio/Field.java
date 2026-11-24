package dominio;

/**
 * Cancha 2D en vista lateral de 900 x 450 con paredes, techo, suelo y los dos
 * arcos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Field {

    public static final double ANCHO = 900.0;
    public static final double SUELO_Y = 450.0;
    static final int SIN_GOL = 0;
    private static final double PARED_IZQUIERDA_X = 0.0;
    private static final double TECHO_Y = 0.0;
    private static final double CENTRO_X = ANCHO / 2.0;
    private static final double CAIDA_BALON_Y = 60.0;
    private final Goal arcoIzquierdo;
    private final Goal arcoDerecho;

    /** Crea la cancha con los arcos izquierdo y derecho. */
    public Field() {
        this.arcoIzquierdo = new Goal(PARED_IZQUIERDA_X, Goal.ANCHO, 2);
        this.arcoDerecho = new Goal(ANCHO, ANCHO - Goal.ANCHO, 1);
    }

    /**
     * Mueve un paso el balón y resuelve rebotes contra los bordes de ambos arcos.
     */
    void actualizarBalon(Ball balon) {
        balon.actualizarFisica(SUELO_Y, TECHO_Y);
        this.arcoIzquierdo.resolverBorde(balon);
        this.arcoDerecho.resolverBorde(balon);
    }

    /**
     * Rebota en paredes después de empujes y patadas para que el balón no salga del
     * campo.
     */
    void mantenerBalonDentro(Ball balon) {
        balon.rebotarEnParedes(PARED_IZQUIERDA_X, ANCHO);
    }

    double[][] obtenerLimitesArcos() {
        return new double[][] { arcoIzquierdo.obtenerLimites(), arcoDerecho.obtenerLimites() };
    }

    void soltarBalon(Ball balon) {
        balon.ubicarEn(CENTRO_X, CAIDA_BALON_Y);
    }

    /** Devuelve 1 o 2 si el balón entró completo en un arco, o SIN_GOL (0). */
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
