package dominio;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Action;
import dominio.modalidades.Controller;
import dominio.modalidades.GameMode;
import dominio.partidos.MatchType;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Simulación de un partido: cancha, balón, jugadores con sus controladores
 * y reglas de MatchType.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Match {

    private static final double PASO_BALON = 1.0 / 60.0;
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
    private double tiempoBalon;

    Match(GameMode modo, MatchType tipo, Character jugador1, Character jugador2, CharacterCatalog rivales) {
        this.jugadores = new Character[] { jugador1, jugador2 };
        this.controladores = new Controller[] { modo.crearControladorJugador1(), modo.crearControladorJugador2() };
        this.tipo = tipo;
        this.rivales = rivales;
        Side.LEFT.ubicar(jugador1);
        Side.RIGHT.ubicar(jugador2);
        cancha.soltarBalon(balon);
        tipo.iniciar();
    }

    /**
     * Avanza personajes con el tiempo real y el balón en pasos fijos de 1/60 s
     * para que no cambie su velocidad según los FPS del equipo.
     */
    void actualizarTick(double segundosTranscurridos) throws POOBSoccerException {
        if (segundosTranscurridos < 0) {
            throw new POOBSoccerException(POOBSoccerException.TIEMPO_INVALIDO);
        }
        if (pausado || tipo.estaTerminado()) {
            return;
        }
        for (int i = 0; i < jugadores.length; i++) {
            controladores[i].controlar(jugadores[i], balon);
            jugadores[i].actualizar(segundosTranscurridos);
        }
        tiempoBalon += segundosTranscurridos;
        while (tiempoBalon >= PASO_BALON) {
            tiempoBalon -= PASO_BALON;
            avanzarBalon();
            int anotador = cancha.anotadorDe(balon);
            if (anotador != Field.SIN_GOL) {
                anotarGol(anotador);
                tiempoBalon = 0.0;
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
     * Si patea queda como último pateador; si solo toca un balón rápido de otro, se
     * aturde.
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

    void anotarGol(int anotador) throws POOBSoccerException {
        Character jugadorAnotador = jugador(anotador);
        jugadorAnotador.sumarGol();
        tipo.golAnotado(anotador, jugadorAnotador.obtenerGoles());
        if (tipo.consumirCambioRival()) {
            reemplazarRival();
        }
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
     * Supervivencia: entra el siguiente rival y el marcador del jugador 1 vuelve a
     * cero.
     */
    private void reemplazarRival() throws POOBSoccerException {
        jugadores[JUGADOR_DERECHO] = rivales.siguienteDespuesDe(jugadores[JUGADOR_DERECHO]);
        Side.RIGHT.ubicar(jugadores[JUGADOR_DERECHO]);
        jugadores[JUGADOR_IZQUIERDO].reiniciarGoles();
    }

    void pausar() {
        pausado = true;
        soltarTodo();
    }

    void soltarTodo() {
        for (Controller controlador : controladores) {
            controlador.soltarTodo();
        }
    }

    void reanudar() {
        pausado = false;
    }

    void presionar(int numero, Action accion) throws POOBSoccerException {
        controladores[indiceJugador(numero)].presionar(accion);
    }

    void soltar(int numero, Action accion) throws POOBSoccerException {
        controladores[indiceJugador(numero)].soltar(accion);
    }

    /** Convierte jugador 1 o 2 en índice 0 o 1 validando el rango. */
    static int indiceJugador(int numero) throws POOBSoccerException {
        if (numero < 1 || numero > JUGADORES) {
            throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
        }
        return numero - 1;
    }

    private Character jugador(int numero) throws POOBSoccerException {
        return jugadores[indiceJugador(numero)];
    }

    int obtenerGoles(int jugador) throws POOBSoccerException {
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

    double[] obtenerPosicionBalon() {
        return new double[] { balon.obtenerX(), balon.obtenerY() };
    }

    String obtenerEstado() {
        return tipo.obtenerTextoEstado();
    }

    boolean estaTerminado() {
        return tipo.estaTerminado();
    }

    String obtenerNombreGanador() throws POOBSoccerException {
        if (!tipo.estaTerminado()) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_EN_CURSO);
        }
        return obtenerNombreJugador(tipo.obtenerGanador());
    }
}
