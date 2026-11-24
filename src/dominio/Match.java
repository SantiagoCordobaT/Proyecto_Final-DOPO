package dominio;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Action;
import dominio.modalidades.Controller;
import dominio.modalidades.GameMode;
import dominio.partidos.MatchType;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Simula un partido entre el jugador izquierdo y el jugador derecho dentro de
 * la cancha.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Match {

    private final Field cancha = new Field();
    private final Ball balon = new Ball();
    private final ContactResolver contactos = new ContactResolver();
    private final Controller controlIzquierdo;
    private final Controller controlDerecho;
    private final MatchType tipo;
    private final CharacterCatalog catalogoRivales;

    private Character izquierdo;
    private Character derecho;
    private boolean pausado;

    Match(GameMode modo, MatchType tipo, Character izquierdo, Character derecho, CharacterCatalog catalogoRivales) {
        this.izquierdo = izquierdo;
        this.derecho = derecho;
        this.controlIzquierdo = modo.crearControladorJugador1();
        this.controlDerecho = modo.crearControladorJugador2();
        this.tipo = tipo;
        this.catalogoRivales = catalogoRivales;
        Side.LEFT.ubicar(izquierdo);
        Side.RIGHT.ubicar(derecho);
        cancha.soltarBalon(balon);
        tipo.iniciar();
    }

    void actualizarTick(double segundos) throws POOBSoccerException {
        if (segundos < 0.0) {
            throw new POOBSoccerException(POOBSoccerException.TIEMPO_INVALIDO);
        }
        if (pausado || tipo.estaTerminado()) {
            return;
        }
        controlIzquierdo.controlar(izquierdo, balon);
        controlDerecho.controlar(derecho, balon);
        izquierdo.actualizar(segundos);
        derecho.actualizar(segundos);

        cancha.actualizarBalon(balon, segundos);
        contactos.resolver(balon, izquierdo, derecho);
        cancha.mantenerBalonDentro(balon);

        int anotador = cancha.anotadorDe(balon);
        if (anotador != Field.SIN_GOL) {
            registrarGol(anotador);
        }
        tipo.avanzar(segundos);
    }

    void registrarGol(int anotador) throws POOBSoccerException {
        Character quienAnoto = obtenerPersonaje(anotador);
        quienAnoto.sumarGol();
        Character nuevoRival = tipo.registrarGol(anotador, quienAnoto.obtenerGoles(), derecho, catalogoRivales);
        if (nuevoRival != null) {
            derecho = nuevoRival;
            Side.RIGHT.ubicar(derecho);
            izquierdo.reiniciarGoles();
        }
        izquierdo.reiniciarJugada();
        derecho.reiniciarJugada();
        cancha.soltarBalon(balon);
        contactos.reiniciar();
    }

    void pausar() {
        pausado = true;
        soltarTodo();
    }

    void reanudar() {
        pausado = false;
    }

    void soltarTodo() {
        controlIzquierdo.soltarTodo();
        controlDerecho.soltarTodo();
    }

    void presionar(int jugador, Action accion) throws POOBSoccerException {
        obtenerControlador(jugador).presionar(accion);
    }

    void soltar(int jugador, Action accion) throws POOBSoccerException {
        obtenerControlador(jugador).soltar(accion);
    }

    private Character obtenerPersonaje(int jugador) throws POOBSoccerException {
        if (jugador == 1) {
            return izquierdo;
        }
        if (jugador == 2) {
            return derecho;
        }
        throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
    }

    private Controller obtenerControlador(int jugador) throws POOBSoccerException {
        if (jugador == 1) {
            return controlIzquierdo;
        }
        if (jugador == 2) {
            return controlDerecho;
        }
        throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
    }

    PlayerSummary obtenerResumenJugador(int jugador) throws POOBSoccerException {
        Character p = obtenerPersonaje(jugador);
        return new PlayerSummary(p.obtenerNombre(), p.obtenerPais(), p.obtenerLimites(), p.obtenerGoles(),
                p.obtenerCargaPoder(), p.obtenerNombrePoder(), p.estaPoderArmado(), p.obtenerNombresEfectos(),
                p.obtenerOrientacion(), p.estaPateando());
    }

    double[][] obtenerLimitesArcos() {
        return cancha.obtenerLimitesArcos();
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
        return obtenerPersonaje(tipo.obtenerGanador()).obtenerNombre();
    }
}
