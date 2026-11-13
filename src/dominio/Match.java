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

    private static final double STEP_SECONDS = 1.0 / 60.0;
    private static final double TOLERANCE = 1e-9;
    /** Cantidad de jugadores de un partido, numerados 1 y 2. */
    static final int PLAYERS = 2;
    private static final int LEFT_PLAYER = 0;
    private static final int RIGHT_PLAYER = 1;

    private final Field field = new Field();
    private final Ball ball = new Ball();
    private final Character[] players;
    private final Controller[] controllers;
    private final MatchType type;
    private final CharacterCatalog rivals;

    private Character lastKicker;
    private boolean paused;
    private double accumulator;

    /**
     * Crea el partido (Creator de la cancha, el balon y los controladores), ubica a
     * los jugadores en su lado, deja caer el balon e inicia el tipo de partido.
     *
     * @param mode Modalidad: decide los controladores.
     * @param type Tipo de partido.
     * @param player1 Personaje del jugador 1 (lado izquierdo).
     * @param player2 Personaje del jugador 2 (lado derecho).
     * @param rivals Catalogo del que salen los nuevos rivales en Supervivencia.
     */
    Match(GameMode mode, MatchType type, Character player1, Character player2, CharacterCatalog rivals) {
        this.players = new Character[] { player1, player2 };
        this.controllers = new Controller[] { mode.createPlayer1Controller(), mode.createPlayer2Controller() };
        this.type = type;
        this.rivals = rivals;
        Side.LEFT.place(player1);
        Side.RIGHT.place(player2);
        field.dropBall(ball);
        type.begin(this);
    }

    /**
     * Ciclo del partido: controladores, personajes, pasos de fisica del balon con
     * deteccion de goles y reloj del tipo de partido.
     *
     * @param deltaTimeSeconds Segundos transcurridos desde el tick anterior.
     * @throws POOBSoccerException Si el tiempo es negativo.
     */
    void updateTick(double deltaTimeSeconds) throws POOBSoccerException {
        if (deltaTimeSeconds < 0.0) {
            throw new POOBSoccerException(POOBSoccerException.TIEMPO_INVALIDO);
        }
        if (paused || type.isFinished()) {
            return;
        }
        for (int i = 0; i < players.length; i++) {
            controllers[i].control(players[i], ball);
            players[i].update(deltaTimeSeconds);
        }
        accumulator += deltaTimeSeconds;
        while (accumulator >= STEP_SECONDS - TOLERANCE) {
            accumulator -= STEP_SECONDS;
            stepBall();
            int scorer = field.scorerOf(ball);
            if (scorer != Field.NO_GOAL) {
                scoreGoal(scorer);
                accumulator = 0.0;
            }
        }
        type.tick(deltaTimeSeconds);
    }

    private void stepBall() {
        field.updateBall(ball);
        resolveContact(players[LEFT_PLAYER], players[RIGHT_PLAYER]);
        resolveContact(players[RIGHT_PLAYER], players[LEFT_PLAYER]);
    }

    /**
     * Resuelve la patada y el contacto de un personaje con el balon: si lo patea queda
     * como ultimo pateador; si solo lo toca, un balonazo fuerte de otro lo aturde y
     * frena el balon, y luego el cuerpo lo empuja.
     */
    private void resolveContact(Character character, Character opponent) {
        if (character.tryKick(ball, opponent)) {
            lastKicker = character;
            return;
        }
        if (!character.touches(ball)) {
            return;
        }
        if (character != lastKicker && ball.isHardHit()) {
            character.stun();
            ball.slowDown();
        }
        character.push(ball);
    }

    /**
     * Suma el gol a quien anota (lo decide el arco, asi el autogol queda cubierto),
     * deja que el tipo de partido reaccione y reinicia la jugada.
     *
     * @param scorer Jugador (1 o 2) que anota.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void scoreGoal(int scorer) throws POOBSoccerException {
        player(scorer).addGoal();
        type.goalScored(scorer);
        restartPlay();
    }

    private void restartPlay() {
        for (Character player : players) {
            player.restart();
        }
        field.dropBall(ball);
        lastKicker = null;
    }

    /** Supervivencia: entra el siguiente rival del catalogo, en su lado y con la barra vacia. */
    @Override
    public void replaceRival() throws POOBSoccerException {
        players[RIGHT_PLAYER] = rivals.nextAfter(players[RIGHT_PLAYER]);
        Side.RIGHT.place(players[RIGHT_PLAYER]);
    }

    /** Pausa el partido y suelta las acciones mantenidas, para que nadie siga moviendose al reanudar. */
    void pause() {
        paused = true;
        for (Controller controller : controllers) {
            controller.releaseAll();
        }
    }

    /** Reanuda el partido. */
    void resume() {
        paused = false;
    }

    /**
     * @param number Jugador (1 o 2).
     * @param action Accion que empieza a mantenerse presionada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void press(int number, Action action) throws POOBSoccerException {
        controllers[index(number)].press(action);
    }

    /**
     * @param number Jugador (1 o 2).
     * @param action Accion que deja de mantenerse presionada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void release(int number, Action action) throws POOBSoccerException {
        controllers[index(number)].release(action);
    }

    /**
     * Traduce el numero de jugador a su posicion en los arreglos (Information Expert de
     * la numeracion; la fachada tambien la usa al elegir los personajes).
     *
     * @param number Jugador (1 o 2).
     * @return Posicion del jugador en los arreglos.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    static int index(int number) throws POOBSoccerException {
        if (number < 1 || number > PLAYERS) {
            throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
        }
        return number - 1;
    }

    private Character player(int number) throws POOBSoccerException {
        return players[index(number)];
    }

    @Override
    public int getGoals(int player) throws POOBSoccerException {
        return player(player).getGoals();
    }

    String getPlayerName(int number) throws POOBSoccerException {
        return player(number).getName();
    }

    String getPlayerCountry(int number) throws POOBSoccerException {
        return player(number).getCountry();
    }

    double[] getPlayerBounds(int number) throws POOBSoccerException {
        return player(number).getBounds();
    }

    double getPlayerPowerCharge(int number) throws POOBSoccerException {
        return player(number).getPowerCharge();
    }

    String getPlayerPowerName(int number) throws POOBSoccerException {
        return player(number).getPowerName();
    }

    boolean isPlayerPowerArmed(int number) throws POOBSoccerException {
        return player(number).isPowerArmed();
    }

    String[] getPlayerEffects(int number) throws POOBSoccerException {
        return player(number).getEffectNames();
    }

    double getBallX() {
        return ball.getX();
    }

    double getBallY() {
        return ball.getY();
    }

    /** @return Estado que muestra el tipo de partido (reloj, meta, balones y racha...). */
    String getStatus() {
        return type.getStatusText();
    }

    boolean isFinished() {
        return type.isFinished();
    }

    /**
     * @return Nombre del personaje ganador.
     * @throws POOBSoccerException Si el partido aun no termina.
     */
    String getWinnerName() throws POOBSoccerException {
        if (!type.isFinished()) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_EN_CURSO);
        }
        return getPlayerName(type.getWinner());
    }
}
