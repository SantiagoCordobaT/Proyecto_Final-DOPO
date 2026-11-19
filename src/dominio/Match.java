package dominio;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Action;
import dominio.modalidades.Controller;
import dominio.modalidades.GameMode;
import dominio.partidos.MatchContext;
import dominio.partidos.MatchType;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Simulacion de un partido (High Cohesion): cancha, balon, los dos jugadores
 * con
 * sus controladores y el tipo de partido. Los personajes se mueven con el
 * tiempo
 * real y el balon avanza en pasos fijos de 1/60 s para que la fisica sea
 * estable.
 * Es el {@link MatchContext} de su tipo de partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Match implements MatchContext {

    private static final double SEGUNDOS_PASO = 1.0 / 60.0;
    private static final double TOLERANCIA = 1e-9;
    /** Cantidad de jugadores de un partido, numerados 1 y 2. */
    static final int JUGADORES = 2;
    private static final int JUGADOR_IZQUIERDO = 0;
    private static final int JUGADOR_DERECHO = 1;

    private final Field cancha = new Field();
    private final Ball balon = new Ball();
    private final Character[] jugadores;
    private final Controller[] controladores;
    private final MatchType tipo;
    private final CharacterCatalog rivales;

    private Character ultimoPateador;
    private boolean pausado;
    private double acumulador;

    /**
     * Crea el partido (Creator de la cancha, el balon y los controladores), ubica a
     * los jugadores en su lado, deja caer el balon e inicia el tipo de partido.
     *
     * @param modo     Modalidad: decide los controladores.
     * @param tipo     Tipo de partido.
     * @param jugador1 Personaje del jugador 1 (lado izquierdo).
     * @param jugador2 Personaje del jugador 2 (lado derecho).
     * @param rivales  Catalogo del que salen los nuevos rivales en Supervivencia.
     */
    Match(GameMode modo, MatchType tipo, Character jugador1, Character jugador2, CharacterCatalog rivales) {
        this.jugadores = new Character[] { jugador1, jugador2 };
        this.controladores = new Controller[] { modo.crearControladorJugador1(), modo.crearControladorJugador2() };
        this.tipo = tipo;
        this.rivales = rivales;
        Side.LEFT.ubicar(jugador1);
        Side.RIGHT.ubicar(jugador2);
        cancha.soltarBalon(balon);
        tipo.iniciar(this);
    }

    /**
     * Ciclo del partido: controladores, personajes, pasos de fisica del balon con
     * deteccion de goles y reloj del tipo de partido.
     *
     * @param segundosTranscurridos Segundos transcurridos desde el tick anterior.
     * @throws POOBSoccerException Si el tiempo es negativo.
     */
    void actualizarTick(double segundosTranscurridos) throws POOBSoccerException {
        if (segundosTranscurridos < 0.0) {
            throw new POOBSoccerException(POOBSoccerException.TIEMPO_INVALIDO);
        }
        if (pausado || tipo.estaTerminado()) {
            return;
        }
        for (int i = 0; i < jugadores.length; i++) {
            controladores[i].controlar(jugadores[i], balon);
            jugadores[i].actualizar(segundosTranscurridos);
        }
        acumulador += segundosTranscurridos;
        while (acumulador >= SEGUNDOS_PASO - TOLERANCIA) {
            acumulador -= SEGUNDOS_PASO;
            avanzarBalon();
            int anotador = cancha.anotadorDe(balon);
            if (anotador != Field.SIN_GOL) {
                anotarGol(anotador);
                acumulador = 0.0;
            }
        }
        tipo.avanzar(segundosTranscurridos);
    }

    private void avanzarBalon() {
        cancha.actualizarBalon(balon);
        resolverContacto(jugadores[JUGADOR_IZQUIERDO], jugadores[JUGADOR_DERECHO]);
        resolverContacto(jugadores[JUGADOR_DERECHO], jugadores[JUGADOR_IZQUIERDO]);
        cancha.mantenerBalonDentro(balon);
    }

    /**
     * Resuelve la patada y el contacto de un personaje con el balon: si lo patea
     * queda
     * como ultimo pateador; si solo lo toca, un balonazo fuerte de otro lo aturde y
     * frena el balon, y luego el cuerpo lo empuja.
     */
    private void resolverContacto(Character personaje, Character oponente) {
        if (personaje.intentarPatear(balon, oponente)) {
            ultimoPateador = personaje;
            return;
        }
        if (!personaje.toca(balon)) {
            return;
        }
        if (personaje != ultimoPateador && balon.esGolpeFuerte()) {
            personaje.aturdir();
            balon.frenar();
        }
        personaje.empujar(balon);
    }

    /**
     * Suma el gol a quien anota (lo decide el arco, asi el autogol queda cubierto),
     * deja que el tipo de partido reaccione y reinicia la jugada.
     *
     * @param anotador Jugador (1 o 2) que anota.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void anotarGol(int anotador) throws POOBSoccerException {
        jugador(anotador).sumarGol();
        tipo.golAnotado(anotador);
        reiniciarJugada();
    }

    private void reiniciarJugada() {
        for (Character jugador : jugadores) {
            jugador.reiniciarJugada();
        }
        cancha.soltarBalon(balon);
        ultimoPateador = null;
    }

    /**
     * Supervivencia: entra el siguiente rival del catalogo, en su lado y con la
     * barra
     * vacia. Contra el nuevo rival empieza un encuentro nuevo, asi que el marcador
     * del
     * jugador 1 tambien vuelve a cero.
     */
    @Override
    public void reemplazarRival() throws POOBSoccerException {
        jugadores[JUGADOR_DERECHO] = rivales.siguienteDespuesDe(jugadores[JUGADOR_DERECHO]);
        Side.RIGHT.ubicar(jugadores[JUGADOR_DERECHO]);
        jugadores[JUGADOR_IZQUIERDO].reiniciarGoles();
    }

    /**
     * Pausa el partido y suelta las acciones mantenidas, para que nadie siga
     * moviendose al reanudar.
     */
    void pausar() {
        pausado = true;
        soltarTodo();
    }

    /** Suelta las acciones que los jugadores mantenian presionadas. */
    void soltarTodo() {
        for (Controller controlador : controladores) {
            controlador.soltarTodo();
        }
    }

    /** Reanuda el partido. */
    void reanudar() {
        pausado = false;
    }

    /**
     * @param numero Jugador (1 o 2).
     * @param accion Accion que empieza a mantenerse presionada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void presionar(int numero, Action accion) throws POOBSoccerException {
        controladores[indiceJugador(numero)].presionar(accion);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @param accion Accion que deja de mantenerse presionada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void soltar(int numero, Action accion) throws POOBSoccerException {
        controladores[indiceJugador(numero)].soltar(accion);
    }

    /**
     * Traduce el numero de jugador a su posicion en los arreglos (Information
     * Expert de
     * la numeracion; la fachada tambien la usa al elegir los personajes).
     *
     * @param numero Jugador (1 o 2).
     * @return Posicion del jugador en los arreglos.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    static int indiceJugador(int numero) throws POOBSoccerException {
        if (numero < 1 || numero > JUGADORES) {
            throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
        }
        return numero - 1;
    }

    private Character jugador(int numero) throws POOBSoccerException {
        return jugadores[indiceJugador(numero)];
    }

    @Override
    public int obtenerGoles(int jugador) throws POOBSoccerException {
        return jugador(jugador).obtenerGoles();
    }

    String obtenerNombreJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerNombre();
    }

    String obtenerPaisJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerPais();
    }

    double[] obtenerLimitesJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerLimites();
    }

    double obtenerCargaPoderJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerCargaPoder();
    }

    String obtenerNombrePoderJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerNombrePoder();
    }

    boolean estaPoderJugadorArmado(int numero) throws POOBSoccerException {
        return jugador(numero).estaPoderArmado();
    }

    String[] obtenerEfectosJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerNombresEfectos();
    }

    double[][] obtenerLimitesArcos() {
        return cancha.obtenerLimitesArcos();
    }

    int obtenerOrientacionJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerOrientacion();
    }

    boolean estaJugadorPateando(int numero) throws POOBSoccerException {
        return jugador(numero).estaPateando();
    }

    /**
     * @return Centro visible del balon como {x, y}: su posicion adelantada por la
     *         fraccion
     *         de paso de fisica pendiente, para que se vea fluido entre pasos.
     */
    double[] obtenerPosicionBalon() {
        return cancha.balonVisible(balon, Math.max(0.0, acumulador / SEGUNDOS_PASO));
    }

    /**
     * @return Estado que muestra el tipo de partido (reloj, meta, balones y
     *         racha...).
     */
    String obtenerEstado() {
        return tipo.obtenerTextoEstado();
    }

    boolean estaTerminado() {
        return tipo.estaTerminado();
    }

    /**
     * @return Nombre del personaje ganador.
     * @throws POOBSoccerException Si el partido aun no termina.
     */
    String obtenerNombreGanador() throws POOBSoccerException {
        if (!tipo.estaTerminado()) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_EN_CURSO);
        }
        return obtenerNombreJugador(tipo.obtenerGanador());
    }
}
