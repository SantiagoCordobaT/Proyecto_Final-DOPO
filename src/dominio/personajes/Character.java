package dominio.personajes;

import dominio.Ball;
import dominio.Field;
import dominio.Named;
import dominio.efectos.Effect;
import dominio.efectos.StatusEffects;
import dominio.efectos.StunEffect;
import dominio.excepciones.POOBSoccerException;
import dominio.poderes.Power;
import dominio.poderes.PowerBar;

/**
 * Personaje jugable de POOBSoccer. Su posicion (x, y) es el centro horizontal
 * y los pies del personaje. Es experto en su cuerpo: se mueve, salta, patea y
 * empuja el balon. Todo es matematico (sin AWT ni Swing).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Character implements Named {

    private static final int ESTADISTICA_MIN = 1;
    private static final int ESTADISTICA_MAX = 5;

    private static final double ANCHO_BASE = 60.0;
    private static final double ALTO_BASE = 90.0;
    private static final double VELOCIDAD_POR_PUNTO = 70.0;
    private static final double VELOCIDAD_SALTO_BASE = 450.0;
    private static final double VELOCIDAD_SALTO_POR_PUNTO = 70.0;
    private static final double GRAVEDAD = 1800.0;
    private static final double VENTANA_PATADA_SEGUNDOS = 0.15;
    private static final double ENFRIAMIENTO_PATADA_SEGUNDOS = 0.4;

    // Alcance de la patada alrededor del hitbox y alcance cuerpo a cuerpo contra el
    // rival
    private static final double ALCANCE_PATADA = 15.0;
    private static final double ALCANCE_CUERPO_A_CUERPO = 10.0;

    private final String nombre;
    private final String pais;
    private final int velocidad;
    private final int salto;
    private final int fuerza;
    private int goles;
    private final PowerBar barraPoder;
    private final StatusEffects efectos = new StatusEffects();

    private double x;
    private double y;
    private double velocidadY;
    private double xInicial;
    private int orientacion = 1;
    private int direccionMovimiento;
    private double esperaPatada;
    private double enfriamientoPatada;

    /**
     * Crea un personaje validando sus datos.
     *
     * @param nombre     Nombre.
     * @param pais       Pais.
     * @param velocidad  Velocidad (1 a 5).
     * @param salto      Salto (1 a 5).
     * @param fuerza     Fuerza (1 a 5).
     * @param nivelPoder Poder Unico (1 a 5): rapidez con la que se recarga la barra
     *                   de poder.
     * @param poder      Poder del personaje.
     * @throws POOBSoccerException Si las estadisticas estan fuera de rango o faltan
     *                             datos.
     */
    public Character(String nombre, String pais, int velocidad, int salto, int fuerza, int nivelPoder,
            Power poder) throws POOBSoccerException {
        this(nombre, pais, velocidad, salto, fuerza, new PowerBar(poder, nivelPoder));
        if (nombre == null || nombre.trim().isEmpty() || pais == null || pais.trim().isEmpty() || poder == null) {
            throw new POOBSoccerException(POOBSoccerException.PERSONAJE_INVALIDO);
        }
        validarEstadistica(velocidad);
        validarEstadistica(salto);
        validarEstadistica(fuerza);
        validarEstadistica(nivelPoder);
    }

    /**
     * Constructor base, sin validacion: lo usan el constructor publico (que valida
     * despues) y {@link #copiar()} (Prototype). El personaje empieza sin efectos.
     *
     * @param nombre     Nombre.
     * @param pais       Pais.
     * @param velocidad  Velocidad.
     * @param salto      Salto.
     * @param fuerza     Fuerza.
     * @param barraPoder Barra de poder propia de la copia.
     */
    private Character(String nombre, String pais, int velocidad, int salto, int fuerza, PowerBar barraPoder) {
        this.nombre = nombre;
        this.pais = pais;
        this.velocidad = velocidad;
        this.salto = salto;
        this.fuerza = fuerza;
        this.barraPoder = barraPoder;
    }

    /**
     * Crea una copia independiente (sin estado compartido) del personaje.
     *
     * @return Nueva instancia con los mismos datos base y la barra vacia.
     */
    public Character copiar() {
        return new Character(nombre, pais, velocidad, salto, fuerza, barraPoder.nuevaCopia());
    }

    private static void validarEstadistica(int valor) throws POOBSoccerException {
        if (valor < ESTADISTICA_MIN || valor > ESTADISTICA_MAX) {
            throw new POOBSoccerException(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO);
        }
    }

    /**
     * Entra a la cancha para un partido nuevo: fija su lado, pone goles y barra
     * en cero y queda en su posicion inicial.
     *
     * @param xInicial    Posicion horizontal inicial.
     * @param orientacion 1 si ataca hacia la derecha, -1 hacia la izquierda.
     */
    public void entrarEn(double xInicial, int orientacion) {
        this.xInicial = xInicial;
        this.orientacion = orientacion;
        reiniciarGoles();
        barraPoder.reiniciar();
        reiniciarJugada();
    }

    /** Pone el marcador del personaje en cero para un encuentro nuevo. */
    public void reiniciarGoles() {
        goles = 0;
    }

    /**
     * Reinicio tras un gol: vuelve a su posicion inicial, en el suelo, y limpia
     * efectos temporales.
     */
    public void reiniciarJugada() {
        this.x = xInicial;
        this.y = Field.SUELO_Y;
        this.velocidadY = 0.0;
        this.direccionMovimiento = 0;
        this.esperaPatada = 0.0;
        this.enfriamientoPatada = 0.0;
        this.efectos.limpiar();
    }

    /**
     * Establece la intencion de movimiento horizontal.
     *
     * @param direccion -1 izquierda, 0 quieto, 1 derecha (otros valores se
     *                  normalizan por signo).
     */
    public void establecerDireccionMovimiento(int direccion) {
        this.direccionMovimiento = Integer.signum(direccion);
    }

    /** Salta; solo es posible en el suelo y sin efectos que lo impidan. */
    public void saltar() {
        boolean enSuelo = y >= Field.SUELO_Y;
        if (puedeActuar() && enSuelo) {
            velocidadY = -(VELOCIDAD_SALTO_BASE + salto * VELOCIDAD_SALTO_POR_PUNTO);
        }
    }

    /**
     * Solicita una patada (queda en espera un instante corto) si ningun efecto lo
     * impide.
     */
    public void solicitarPatada() {
        if (puedeActuar()) {
            esperaPatada = VENTANA_PATADA_SEGUNDOS;
        }
    }

    /**
     * Ejecuta la patada solicitada si alcanza el balon o al rival: aturde al rival
     * en rango cuerpo a cuerpo y patea el balon en alcance, disparando el poder
     * armado.
     *
     * @param balon    Balon.
     * @param oponente Rival.
     * @return true si pateo el balon.
     */
    public boolean intentarPatear(Ball balon, Character oponente) {
        boolean balonAlAlcance = seSolapaCon(balon, ALCANCE_PATADA);
        boolean oponenteAlAlcance = estaEnRangoCuerpoACuerpo(oponente);
        if (!puedePatearAhora() || !(balonAlAlcance || oponenteAlAlcance)) {
            return false;
        }
        esperaPatada = 0.0;
        enfriamientoPatada = ENFRIAMIENTO_PATADA_SEGUNDOS;
        if (oponenteAlAlcance) {
            oponente.aturdir();
        }
        if (balonAlAlcance) {
            balon.patear(fuerza, orientacion);
            barraPoder.dispararSobre(oponente);
        }
        return balonAlAlcance;
    }

    /**
     * @param balon Balon.
     * @return true si el hitbox toca el balon.
     */
    public boolean toca(Ball balon) {
        return seSolapaCon(balon, 0.0);
    }

    /**
     * Empuja el balon que toca: rebota en la cabeza si viene desde arriba; si no,
     * sale hacia un lado.
     *
     * @param balon Balon en contacto.
     */
    public void empujar(Ball balon) {
        if (balon.estaPorEncimaDe(obtenerBordeSuperior())) {
            balon.rebotarArriba(obtenerBordeSuperior());
        } else {
            int lado = balon.ladoRespectoA(x);
            balon.empujarDeLado(x + lado * obtenerAncho() / 2.0, lado);
        }
    }

    /** Arma el poder; solo tiene efecto con la barra llena. */
    public void armarPoder() {
        barraPoder.armar();
    }

    /**
     * Agrega un efecto; si ya existe uno del mismo tipo se reemplaza (se reinicia
     * su tiempo).
     *
     * @param efecto Efecto a aplicar.
     */
    public void agregarEfecto(Effect efecto) {
        efectos.agregar(efecto);
    }

    /** Aturde al personaje durante 3 segundos. */
    public void aturdir() {
        agregarEfecto(new StunEffect());
    }

    /**
     * Actualiza el personaje: efectos, recarga de la barra, movimiento dentro de la
     * cancha y gravedad.
     *
     * @param segundosTranscurridos Segundos transcurridos.
     */
    public void actualizar(double segundosTranscurridos) {
        efectos.transcurrir(segundosTranscurridos);
        barraPoder.recargar(segundosTranscurridos);
        esperaPatada = Math.max(0.0, esperaPatada - segundosTranscurridos);
        enfriamientoPatada = Math.max(0.0, enfriamientoPatada - segundosTranscurridos);

        if (puedeActuar()) {
            x += direccionMovimiento * efectos.signoHorizontal() * velocidad * VELOCIDAD_POR_PUNTO
                    * segundosTranscurridos;
        }
        double mitad = obtenerAncho() / 2.0;
        x = Math.max(mitad, Math.min(Field.ANCHO - mitad, x));

        velocidadY += GRAVEDAD * segundosTranscurridos;
        y += velocidadY * segundosTranscurridos;
        if (y >= Field.SUELO_Y) {
            y = Field.SUELO_Y;
            velocidadY = 0.0;
        }
    }

    /** Suma un gol al personaje. */
    public void sumarGol() {
        goles++;
    }

    /** @return true si ningun efecto impide moverse, saltar o patear. */
    private boolean puedeActuar() {
        return efectos.permitenAcciones();
    }

    /** @return true mientras dura la patada que el personaje acaba de ejecutar. */
    public boolean estaPateando() {
        return enfriamientoPatada > 0.0;
    }

    /**
     * @return true si hay una patada solicitada y el personaje puede ejecutarla.
     */
    private boolean puedePatearAhora() {
        return puedeActuar() && esperaPatada > 0.0 && enfriamientoPatada <= 0.0;
    }

    /**
     * @param balon  Balon.
     * @param margen Margen extra en cada lado del hitbox.
     * @return true si el hitbox ampliado por el margen toca el balon.
     */
    private boolean seSolapaCon(Ball balon, double margen) {
        return balon.seSolapaCon(obtenerIzquierda() - margen, obtenerBordeSuperior() - margen,
                obtenerDerecha() + margen, obtenerAbajo() + margen);
    }

    /**
     * @param otro Otro personaje.
     * @return true si los hitbox, ampliados por el alcance cuerpo a cuerpo, se
     *         solapan.
     */
    private boolean estaEnRangoCuerpoACuerpo(Character otro) {
        return obtenerIzquierda() - ALCANCE_CUERPO_A_CUERPO <= otro.obtenerDerecha()
                && obtenerDerecha() + ALCANCE_CUERPO_A_CUERPO >= otro.obtenerIzquierda()
                && obtenerBordeSuperior() - ALCANCE_CUERPO_A_CUERPO <= otro.obtenerAbajo()
                && obtenerAbajo() + ALCANCE_CUERPO_A_CUERPO >= otro.obtenerBordeSuperior();
    }

    /** @return Escala actual del cuerpo (producto de los efectos activos). */
    private double obtenerEscala() {
        return efectos.escala();
    }

    /** @return Nombres de los efectos activos. */
    public String[] obtenerNombresEfectos() {
        return efectos.obtenerNombres();
    }

    /**
     * @return Nombre del personaje.
     */
    @Override
    public String obtenerNombre() {
        return nombre;
    }

    /**
     * @return Pais del personaje.
     */
    public String obtenerPais() {
        return pais;
    }

    /**
     * @return Estadistica de velocidad (1 a 5).
     */
    public int obtenerVelocidad() {
        return velocidad;
    }

    /**
     * @return Estadistica de salto (1 a 5).
     */
    public int obtenerSalto() {
        return salto;
    }

    /**
     * @return Estadistica de fuerza (1 a 5).
     */
    public int obtenerFuerza() {
        return fuerza;
    }

    /**
     * @return Estadistica de Poder Unico (1 a 5).
     */
    public int obtenerNivelPoder() {
        return barraPoder.obtenerNivel();
    }

    /**
     * @return Goles anotados.
     */
    public int obtenerGoles() {
        return goles;
    }

    /**
     * @return Fraccion llena de la barra de poder (0.0 a 1.0).
     */
    public double obtenerCargaPoder() {
        return barraPoder.obtenerCarga();
    }

    /**
     * @return true si el poder esta armado.
     */
    public boolean estaPoderArmado() {
        return barraPoder.estaArmado();
    }

    /**
     * @return Nombre del poder del personaje.
     */
    public String obtenerNombrePoder() {
        return barraPoder.obtenerNombrePoder();
    }

    /**
     * @return Descripcion del poder del personaje.
     */
    public String obtenerDescripcionPoder() {
        return barraPoder.obtenerDescripcionPoder();
    }

    /**
     * @return Hitbox actual (con efectos) como {izquierda, arriba, ancho, alto}.
     */
    public double[] obtenerLimites() {
        return new double[] { obtenerIzquierda(), obtenerBordeSuperior(), obtenerAncho(), obtenerAlto() };
    }

    /**
     * @return Posicion horizontal (centro).
     */
    public double obtenerX() {
        return x;
    }

    /**
     * @return Borde superior del hitbox.
     */
    public double obtenerBordeSuperior() {
        return y - obtenerAlto();
    }

    /**
     * @return Altura del centro del hitbox.
     */
    public double obtenerCentroY() {
        return obtenerBordeSuperior() + obtenerAlto() / 2.0;
    }

    /**
     * @return Sentido de ataque: 1 derecha, -1 izquierda.
     */
    public int obtenerOrientacion() {
        return orientacion;
    }

    private double obtenerAncho() {
        return ANCHO_BASE * obtenerEscala();
    }

    private double obtenerAlto() {
        return ALTO_BASE * obtenerEscala();
    }

    private double obtenerIzquierda() {
        return x - obtenerAncho() / 2.0;
    }

    private double obtenerDerecha() {
        return x + obtenerAncho() / 2.0;
    }

    private double obtenerAbajo() {
        return y;
    }
}
