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
 * Simulacion de un partido (High Cohesion): cancha, balon, los dos jugadores con
 * sus controladores y el tipo de partido. Los personajes se mueven con el tiempo
 * real y el balon avanza en pasos fijos de 1/60 s para que la fisica sea estable.
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
        this.controladores = new Controller[] { modo.createPlayer1Controller(), modo.createPlayer2Controller() };
        this.tipo = tipo;
        this.rivales = rivales;
        Side.LEFT.place(jugador1);
        Side.RIGHT.place(jugador2);
        cancha.dropBall(balon);
        tipo.begin(this);
    }

    /**
     * Ciclo del partido: controladores, personajes, pasos de fisica del balon con
     * deteccion de goles y reloj del tipo de partido.
     *
     * @param segundosTranscurridos Segundos transcurridos desde el tick anterior.
     * @throws POOBSoccerException Si el tiempo es negativo.
     */
    void updateTick(double segundosTranscurridos) throws POOBSoccerException {
        if (segundosTranscurridos < 0.0) {
            throw new POOBSoccerException(POOBSoccerException.TIEMPO_INVALIDO);
        }
        if (pausado || tipo.isFinished()) {
            return;
        }
        for (int i = 0; i < jugadores.length; i++) {
            controladores[i].control(jugadores[i], balon);
            jugadores[i].update(segundosTranscurridos);
        }
        acumulador += segundosTranscurridos;
        while (acumulador >= SEGUNDOS_PASO - TOLERANCIA) {
            acumulador -= SEGUNDOS_PASO;
            stepBall();
            int anotador = cancha.scorerOf(balon);
            if (anotador != Field.SIN_GOL) {
                scoreGoal(anotador);
                acumulador = 0.0;
            }
        }
        tipo.tick(segundosTranscurridos);
    }

    private void stepBall() {
        cancha.updateBall(balon);
        resolveContact(jugadores[JUGADOR_IZQUIERDO], jugadores[JUGADOR_DERECHO]);
        resolveContact(jugadores[JUGADOR_DERECHO], jugadores[JUGADOR_IZQUIERDO]);
        cancha.keepBallInside(balon);
    }

    /**
     * Resuelve la patada y el contacto de un personaje con el balon: si lo patea queda
     * como ultimo pateador; si solo lo toca, un balonazo fuerte de otro lo aturde y
     * frena el balon, y luego el cuerpo lo empuja.
     */
    private void resolveContact(Character personaje, Character oponente) {
        if (personaje.tryKick(balon, oponente)) {
            ultimoPateador = personaje;
            return;
        }
        if (!personaje.touches(balon)) {
            return;
        }
        if (personaje != ultimoPateador && balon.isHardHit()) {
            personaje.stun();
            balon.slowDown();
        }
        personaje.push(balon);
    }

    /**
     * Suma el gol a quien anota (lo decide el arco, asi el autogol queda cubierto),
     * deja que el tipo de partido reaccione y reinicia la jugada.
     *
     * @param anotador Jugador (1 o 2) que anota.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void scoreGoal(int anotador) throws POOBSoccerException {
        player(anotador).addGoal();
        tipo.goalScored(anotador);
        restartPlay();
    }

    private void restartPlay() {
        for (Character jugador : jugadores) {
            jugador.restart();
        }
        cancha.dropBall(balon);
        ultimoPateador = null;
    }

    /**
     * Supervivencia: entra el siguiente rival del catalogo, en su lado y con la barra
     * vacia. Contra el nuevo rival empieza un encuentro nuevo, asi que el marcador del
     * jugador 1 tambien vuelve a cero.
     */
    @Override
    public void replaceRival() throws POOBSoccerException {
        jugadores[JUGADOR_DERECHO] = rivales.nextAfter(jugadores[JUGADOR_DERECHO]);
        Side.RIGHT.place(jugadores[JUGADOR_DERECHO]);
        jugadores[JUGADOR_IZQUIERDO].resetGoals();
    }

    /** Pausa el partido y suelta las acciones mantenidas, para que nadie siga moviendose al reanudar. */
    void pause() {
        pausado = true;
        releaseAll();
    }

    /** Suelta las acciones que los jugadores mantenian presionadas. */
    void releaseAll() {
        for (Controller controlador : controladores) {
            controlador.releaseAll();
        }
    }

    /** Reanuda el partido. */
    void resume() {
        pausado = false;
    }

    /**
     * @param numero Jugador (1 o 2).
     * @param accion Accion que empieza a mantenerse presionada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void press(int numero, Action accion) throws POOBSoccerException {
        controladores[index(numero)].press(accion);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @param accion Accion que deja de mantenerse presionada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void release(int numero, Action accion) throws POOBSoccerException {
        controladores[index(numero)].release(accion);
    }

    /**
     * Traduce el numero de jugador a su posicion en los arreglos (Information Expert de
     * la numeracion; la fachada tambien la usa al elegir los personajes).
     *
     * @param numero Jugador (1 o 2).
     * @return Posicion del jugador en los arreglos.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    static int index(int numero) throws POOBSoccerException {
        if (numero < 1 || numero > JUGADORES) {
            throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
        }
        return numero - 1;
    }

    private Character player(int numero) throws POOBSoccerException {
        return jugadores[index(numero)];
    }

    @Override
    public int getGoals(int jugador) throws POOBSoccerException {
        return player(jugador).getGoals();
    }

    String getPlayerName(int numero) throws POOBSoccerException {
        return player(numero).getName();
    }

    String getPlayerCountry(int numero) throws POOBSoccerException {
        return player(numero).getCountry();
    }

    double[] getPlayerBounds(int numero) throws POOBSoccerException {
        return player(numero).getBounds();
    }

    double getPlayerPowerCharge(int numero) throws POOBSoccerException {
        return player(numero).getPowerCharge();
    }

    String getPlayerPowerName(int numero) throws POOBSoccerException {
        return player(numero).getPowerName();
    }

    boolean isPlayerPowerArmed(int numero) throws POOBSoccerException {
        return player(numero).isPowerArmed();
    }

    String[] getPlayerEffects(int numero) throws POOBSoccerException {
        return player(numero).getEffectNames();
    }

    double[][] getGoalBounds() {
        return cancha.getGoalBounds();
    }

    int getPlayerFacing(int numero) throws POOBSoccerException {
        return player(numero).getFacing();
    }

    boolean isPlayerKicking(int numero) throws POOBSoccerException {
        return player(numero).isKicking();
    }

    /**
     * @return Centro visible del balon como {x, y}: su posicion adelantada por la fraccion
     *         de paso de fisica pendiente, para que se vea fluido entre pasos.
     */
    double[] getBallPosition() {
        return cancha.visibleBall(balon, Math.max(0.0, acumulador / SEGUNDOS_PASO));
    }

    /** @return Estado que muestra el tipo de partido (reloj, meta, balones y racha...). */
    String getStatus() {
        return tipo.getStatusText();
    }

    boolean isFinished() {
        return tipo.isFinished();
    }

    /**
     * @return Nombre del personaje ganador.
     * @throws POOBSoccerException Si el partido aun no termina.
     */
    String getWinnerName() throws POOBSoccerException {
        if (!tipo.isFinished()) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_EN_CURSO);
        }
        return getPlayerName(tipo.getWinner());
    }
}
