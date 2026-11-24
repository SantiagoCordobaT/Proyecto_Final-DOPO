package dominio;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Action;
import dominio.modalidades.Controller;
import dominio.modalidades.GameMode;
import dominio.partidos.MatchType;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Lleva la simulación de un partido: cancha, balón, los dos personajes y las
 * reglas del tipo de partido elegido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Match {

    private static final double PASO_BALON = 1.0 / 60.0;
    static final int JUGADORES = 2;

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

    void actualizarTick(double segundos) throws POOBSoccerException {
        if (segundos < 0) {
            throw new POOBSoccerException(POOBSoccerException.TIEMPO_INVALIDO);
        }
        if (pausado || tipo.estaTerminado()) {
            return;
        }
        for (int i = 0; i < jugadores.length; i++) {
            controladores[i].controlar(jugadores[i], balon);
            jugadores[i].actualizar(segundos);
        }

        // El balón avanza a pasos fijos de 1/60 s para que la física sea estable
        tiempoBalon += segundos;
        while (tiempoBalon >= PASO_BALON) {
            tiempoBalon -= PASO_BALON;
            cancha.actualizarBalon(balon);
            procesarContacto(jugadores[0], jugadores[1]);
            procesarContacto(jugadores[1], jugadores[0]);
            cancha.mantenerBalonDentro(balon);

            int anotador = cancha.anotadorDe(balon);
            if (anotador != Field.SIN_GOL) {
                anotarGol(anotador);
                tiempoBalon = 0.0;
            }
        }
        tipo.avanzar(segundos);
    }

    private void procesarContacto(Character personaje, Character oponente) {
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
        Character jugadorAnotador = jugadores[indiceJugador(anotador)];
        jugadorAnotador.sumarGol();
        tipo.golAnotado(anotador, jugadorAnotador.obtenerGoles());

        // En supervivencia, cuando cae la máquina entra el siguiente rival
        if (tipo.consumirCambioRival()) {
            jugadores[1] = rivales.siguienteDespuesDe(jugadores[1]);
            Side.RIGHT.ubicar(jugadores[1]);
            jugadores[0].reiniciarGoles();
        }

        jugadores[0].reiniciarJugada();
        jugadores[1].reiniciarJugada();
        cancha.soltarBalon(balon);
        ultimoPateador = null;
    }

    void pausar() {
        pausado = true;
        soltarTodo();
    }

    void soltarTodo() {
        for (int i = 0; i < controladores.length; i++) {
            controladores[i].soltarTodo();
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

    static int indiceJugador(int numero) throws POOBSoccerException {
        if (numero < 1 || numero > JUGADORES) {
            throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
        }
        return numero - 1;
    }

    PlayerSummary obtenerResumenJugador(int numero) throws POOBSoccerException {
        Character j = jugadores[indiceJugador(numero)];
        return new PlayerSummary(j.obtenerNombre(), j.obtenerPais(), j.obtenerLimites(), j.obtenerGoles(),
                j.obtenerCargaPoder(), j.obtenerNombrePoder(), j.estaPoderArmado(), j.obtenerNombresEfectos(),
                j.obtenerOrientacion(), j.estaPateando());
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
        return jugadores[indiceJugador(tipo.obtenerGanador())].obtenerNombre();
    }
}
