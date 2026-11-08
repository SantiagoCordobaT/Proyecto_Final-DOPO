package dominio;

import java.util.ArrayList;
import java.util.List;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Controller;
import dominio.modalidades.GameMode;
import dominio.modalidades.MvsM;
import dominio.modalidades.PvsM;
import dominio.modalidades.PvsP;
import dominio.partidos.GoalMatch;
import dominio.partidos.MatchType;
import dominio.partidos.SurvivalMatch;
import dominio.partidos.TimeMatch;
import dominio.personajes.Character;
import dominio.poderes.ConfusionPower;
import dominio.poderes.FreezePower;
import dominio.poderes.StunPower;
import dominio.poderes.ShrinkPower;

/**
 * Fachada (Facade) del dominio de POOBSoccer. La presentacion solo usa tipos
 * simples (String, int, boolean, double) a traves de esta clase.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class HeadSoccer {

    public static final double PHYSICS_STEP_SECONDS = 1.0 / 60.0;
    public static final double TIME_TOLERANCE = 1e-9;
    public static final double KICK_REACH = 15.0;
    public static final double PUSH_SPEED = 3.0;
    public static final double STUN_BALL_SPEED = 12.0;
    public static final double STUN_BALL_SLOWDOWN = 0.6;
    public static final double MELEE_RANGE = 10.0;

    private final List<GameMode> modes;
    private final List<MatchType> matchTypes;
    private final List<Character> characters;
    private final Settings settings;
    private final Field field;
    private final Ball ball;

    private GameMode selectedMode;
    private MatchType selectedMatchType;
    private Character player1;
    private Character player2;
    private Controller controller1;
    private Controller controller2;
    private Character lastKicker;
    private int rivalCatalogIndex;

    private boolean started;
    private boolean paused;
    private double physicsAccumulator;

    /**
     * Crea el juego con sus catalogos de modos, tipos de partido y personajes.
     *
     * @throws POOBSoccerException Si algun dato del catalogo fuera invalido.
     */
    public HeadSoccer() throws POOBSoccerException {
        this.settings = new Settings();
        this.field = new Field();
        this.ball = new Ball(field.getCenterX(), Field.BALL_DROP_Y, Field.BALL_RADIUS);

        this.modes = new ArrayList<GameMode>();
        modes.add(new PvsP());
        modes.add(new PvsM());
        modes.add(new MvsM());

        this.matchTypes = new ArrayList<MatchType>();
        matchTypes.add(new TimeMatch());
        matchTypes.add(new GoalMatch(GoalMatch.GOALS_SHORT));
        matchTypes.add(new GoalMatch(GoalMatch.GOALS_MEDIUM));
        matchTypes.add(new GoalMatch(GoalMatch.GOALS_LONG));
        matchTypes.add(new SurvivalMatch());

        this.characters = new ArrayList<Character>();
        characters.add(new Character("Jacob", "Canadá", 3, 3, 4, new FreezePower()));
        characters.add(new Character("Kenji", "Japón", 5, 4, 2, new StunPower()));
        characters.add(new Character("Mateo", "Colombia", 4, 3, 3, new ConfusionPower()));
        characters.add(new Character("Rafael", "Brasil", 4, 5, 3, new ShrinkPower()));
    }

    // ------------------------------------------------------------ catalogos

    /** @return Nombres de las modalidades. */
    public String[] getModeNames() {
        String[] names = new String[modes.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = modes.get(i).getName();
        }
        return names;
    }

    /** @return Nombres de los tipos de partido. */
    public String[] getMatchTypeNames() {
        String[] names = new String[matchTypes.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = matchTypes.get(i).getName();
        }
        return names;
    }

    /** @return Cantidad de personajes del catalogo. */
    public int getCharacterCount() {
        return characters.size();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Nombre del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterName(int index) throws POOBSoccerException {
        return catalogCharacter(index).getName();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Pais del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterCountry(int index) throws POOBSoccerException {
        return catalogCharacter(index).getCountry();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Velocidad del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterSpeed(int index) throws POOBSoccerException {
        return catalogCharacter(index).getSpeed();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Salto del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterJump(int index) throws POOBSoccerException {
        return catalogCharacter(index).getJump();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Fuerza del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterStrength(int index) throws POOBSoccerException {
        return catalogCharacter(index).getStrength();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Nombre del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterPowerName(int index) throws POOBSoccerException {
        return catalogCharacter(index).getPower().getName();
    }

    private Character catalogCharacter(int index) throws POOBSoccerException {
        if (index < 0 || index >= characters.size()) {
            throw new POOBSoccerException(POOBSoccerException.PERSONAJE_INVALIDO);
        }
        return characters.get(index);
    }

    // ------------------------------------------------------------ selecciones

    /**
     * Elige la modalidad.
     *
     * @param index Indice de la modalidad.
     * @throws POOBSoccerException Si el indice es invalido o es incompatible con el tipo de partido elegido.
     */
    public void selectMode(int index) throws POOBSoccerException {
        if (index < 0 || index >= modes.size()) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        GameMode mode = modes.get(index);
        if (selectedMatchType != null) {
            selectedMatchType.validateMode(mode);
        }
        selectedMode = mode;
    }

    /**
     * Elige el tipo de partido.
     *
     * @param index Indice del tipo de partido.
     * @throws POOBSoccerException Si el indice es invalido o es incompatible con la modalidad elegida.
     */
    public void selectMatchType(int index) throws POOBSoccerException {
        if (index < 0 || index >= matchTypes.size()) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        MatchType type = matchTypes.get(index);
        if (selectedMode != null) {
            type.validateMode(selectedMode);
        }
        selectedMatchType = type;
    }

    /**
     * Elige el personaje del jugador 1 (se clona del catalogo).
     *
     * @param index Indice del catalogo.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public void selectPlayer1(int index) throws POOBSoccerException {
        player1 = catalogCharacter(index).copy();
    }

    /**
     * Elige el personaje del jugador 2 (se clona del catalogo).
     *
     * @param index Indice del catalogo.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public void selectPlayer2(int index) throws POOBSoccerException {
        player2 = catalogCharacter(index).copy();
        rivalCatalogIndex = index;
    }

    // ------------------------------------------------------------ partido

    /**
     * Inicia el partido con la configuracion elegida.
     *
     * @throws POOBSoccerException Si falta modalidad, tipo de partido o algun jugador, o son incompatibles.
     */
    public void startMatch() throws POOBSoccerException {
        if (selectedMode == null || selectedMatchType == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        if (player1 == null || player2 == null) {
            throw new POOBSoccerException(POOBSoccerException.JUGADORES_INCOMPLETOS);
        }
        selectedMatchType.begin(selectedMode, player1, player2);

        controller1 = selectedMode.createPlayer1Controller();
        controller2 = selectedMode.createPlayer2Controller();

        player1.placeAt(Field.PLAYER1_START_X, field.getGroundY(), 1);
        player2.placeAt(Field.PLAYER2_START_X, field.getGroundY(), -1);
        player1.resetForNewMatch(field.getGroundY());
        player2.resetForNewMatch(field.getGroundY());
        field.getLeftGoal().setPlayers(player1, player2);
        field.getRightGoal().setPlayers(player2, player1);

        ball.resetPosition(field.getCenterX(), Field.BALL_DROP_Y);
        lastKicker = null;
        physicsAccumulator = 0.0;
        paused = false;
        started = true;
    }

    /**
     * Pausa el partido.
     *
     * @throws POOBSoccerException Si el partido no ha iniciado.
     */
    public void pause() throws POOBSoccerException {
        requireStarted();
        paused = true;
    }

    /**
     * Reanuda el partido.
     *
     * @throws POOBSoccerException Si el partido no ha iniciado.
     */
    public void resume() throws POOBSoccerException {
        requireStarted();
        paused = false;
    }

    /** @return true si esta en pausa. */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Ciclo principal del juego.
     *
     * @param deltaTimeSeconds Segundos transcurridos desde el tick anterior.
     * @throws POOBSoccerException Si el partido no inicio, el tiempo es negativo o una accion viola las reglas.
     */
    public void updateTick(double deltaTimeSeconds) throws POOBSoccerException {
        requireStarted();
        if (deltaTimeSeconds < 0.0) {
            throw new POOBSoccerException(POOBSoccerException.TIEMPO_INVALIDO);
        }
        if (paused || selectedMatchType.isFinished()) {
            return;
        }

        controller1.control(player1, player2, ball, field);
        controller2.control(player2, player1, ball, field);

        player1.update(deltaTimeSeconds, field);
        player2.update(deltaTimeSeconds, field);

        physicsAccumulator += deltaTimeSeconds;
        while (physicsAccumulator >= PHYSICS_STEP_SECONDS - TIME_TOLERANCE) {
            physicsAccumulator -= PHYSICS_STEP_SECONDS;
            stepBall();
            if (checkGoals()) {
                physicsAccumulator = 0.0;
            }
        }

        selectedMatchType.tick(deltaTimeSeconds);
    }

    private void stepBall() {
        field.updateBall(ball);
        resolveCharacterBall(player1, player2);
        resolveCharacterBall(player2, player1);
    }

    /**
     * Resuelve la patada y el contacto de un personaje: patea el balon si lo alcanza y
     * aturde al rival si esta en rango cuerpo a cuerpo; si no patea, solo empuja el balon.
     */
    private void resolveCharacterBall(Character character, Character opponent) {
        boolean ballInReach = character.touches(ball, KICK_REACH);
        boolean opponentInReach = character.isInMeleeRange(opponent, MELEE_RANGE);
        if (character.canKickNow() && (ballInReach || opponentInReach)) {
            kick(character, opponent, ballInReach, opponentInReach);
            return;
        }
        if (!character.touches(ball, 0.0)) {
            return;
        }
        double speed = Math.sqrt(ball.getSpeedX() * ball.getSpeedX() + ball.getSpeedY() * ball.getSpeedY());
        if (speed >= STUN_BALL_SPEED && lastKicker != character) {
            character.stun();
            ball.setSpeedX(ball.getSpeedX() * STUN_BALL_SLOWDOWN);
            ball.setSpeedY(ball.getSpeedY() * STUN_BALL_SLOWDOWN);
        }
        pushBall(character);
    }

    private void kick(Character character, Character opponent, boolean ballInReach, boolean opponentInReach) {
        character.registerKick();
        if (opponentInReach) {
            opponent.stun();
        }
        if (ballInReach) {
            ball.kick(character.getStrength(), character.getFacing());
            character.fireActivePower(opponent, ball);
            lastKicker = character;
        }
    }

    private void pushBall(Character character) {
        int side = ball.getX() >= character.getX() ? 1 : -1;
        if (ball.getY() < character.getTop()) {
            ball.setY(character.getTop() - ball.getRadius());
            ball.setSpeedY(-Math.abs(ball.getSpeedY()) * ball.getBounceFactor());
        } else {
            ball.setX(character.getX() + side * (character.getWidth() / 2.0 + ball.getRadius()));
            ball.setSpeedX(side * Math.max(Math.abs(ball.getSpeedX()), PUSH_SPEED));
        }
    }

    private boolean checkGoals() throws POOBSoccerException {
        Goal[] goals = { field.getLeftGoal(), field.getRightGoal() };
        for (Goal goal : goals) {
            if (goal.isBallInside(ball)) {
                registerGoal(goal);
                return true;
            }
        }
        return false;
    }

    /** Suma el gol al atacante del arco (cubre el autogol) y reinicia la jugada. */
    private void registerGoal(Goal goal) throws POOBSoccerException {
        Character scorer = goal.getAttacker();
        scorer.addGoal();
        selectedMatchType.goalScored(scorer, goal.getDefender());
        if (selectedMatchType.needsNewRival()) {
            loadNextRival();
        }
        restartPlay();
    }

    /** Supervivencia: entra un nuevo rival de la maquina (siguiente del catalogo) con balones llenos. */
    private void loadNextRival() throws POOBSoccerException {
        rivalCatalogIndex = (rivalCatalogIndex + 1) % characters.size();
        player2 = characters.get(rivalCatalogIndex).copy();
        selectedMatchType.replaceRival(player2);
        player2.placeAt(Field.PLAYER2_START_X, field.getGroundY(), -1);
        player2.resetForNewMatch(field.getGroundY());
        field.getLeftGoal().setPlayers(player1, player2);
        field.getRightGoal().setPlayers(player2, player1);
    }

    private void restartPlay() {
        player1.resetForRestart(field.getGroundY());
        player2.resetForRestart(field.getGroundY());
        ball.resetPosition(field.getCenterX(), Field.BALL_DROP_Y);
        lastKicker = null;
    }

    private void requireStarted() throws POOBSoccerException {
        if (!started) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_NO_INICIADO);
        }
    }

    // ------------------------------------------------------------ controles

    private Character player(int number) throws POOBSoccerException {
        if (number != 1 && number != 2) {
            throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
        }
        requireStarted();
        return number == 1 ? player1 : player2;
    }

    /**
     * @param number Jugador (1 o 2).
     * @param direction -1 izquierda, 0 quieto, 1 derecha.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public void setPlayerDirection(int number, int direction) throws POOBSoccerException {
        player(number).setMoveDirection(direction);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return true si salto.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean playerJump(int number) throws POOBSoccerException {
        return player(number).jump(field.getGroundY());
    }

    /**
     * @param number Jugador (1 o 2).
     * @return true si la patada fue aceptada.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean playerKick(int number) throws POOBSoccerException {
        return player(number).requestKick();
    }

    /**
     * @param number Jugador (1 o 2).
     * @throws POOBSoccerException Si la barra no esta llena, el jugador no es valido o no hay partido.
     */
    public void activatePlayerPower(int number) throws POOBSoccerException {
        player(number).activatePower();
    }

    // ------------------------------------------------------------ consultas para la vista

    /**
     * @param number Jugador (1 o 2).
     * @return Nombre del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerName(int number) throws POOBSoccerException {
        return player(number).getName();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Pais del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerCountry(int number) throws POOBSoccerException {
        return player(number).getCountry();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Posicion horizontal del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double getPlayerX(int number) throws POOBSoccerException {
        return player(number).getX();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Posicion vertical (pies) del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double getPlayerY(int number) throws POOBSoccerException {
        return player(number).getY();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Ancho actual del hitbox del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double getPlayerWidth(int number) throws POOBSoccerException {
        return player(number).getWidth();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Alto actual del hitbox del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double getPlayerHeight(int number) throws POOBSoccerException {
        return player(number).getHeight();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Goles del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public int getPlayerGoals(int number) throws POOBSoccerException {
        return player(number).getGoals();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Porcentaje de la barra de poder (0 a 100).
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double getPlayerPowerBar(int number) throws POOBSoccerException {
        return player(number).getPowerBar();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Nombre del poder del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerPowerName(int number) throws POOBSoccerException {
        return player(number).getPower().getName();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return true si el jugador tiene su poder armado.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean isPlayerPowerActive(int number) throws POOBSoccerException {
        return player(number).isPowerActive();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return true si el jugador esta aturdido.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean isPlayerStunned(int number) throws POOBSoccerException {
        return player(number).isStunned();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Nombres de los efectos activos del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String[] getPlayerEffects(int number) throws POOBSoccerException {
        List<String> effectNames = player(number).getActiveEffectNames();
        String[] result = new String[effectNames.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = effectNames.get(i);
        }
        return result;
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Balones (vidas) restantes en Supervivencia; 0 en los otros tipos.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public int getPlayerBalls(int number) throws POOBSoccerException {
        return selectedMatchType.getBalls(player(number));
    }

    /** @return Victorias seguidas de la serie de Supervivencia (0 en otros tipos de partido). */
    public int getSurvivalStreak() {
        return selectedMatchType == null ? 0 : selectedMatchType.getStreak();
    }

    /**
     * @return Posicion horizontal del balon.
     */
    public double getBallX() {
        return ball.getX();
    }

    /**
     * @return Posicion vertical del balon.
     */
    public double getBallY() {
        return ball.getY();
    }

    /**
     * @return Radio del balon.
     */
    public double getBallRadius() {
        return ball.getRadius();
    }

    /**
     * @return Ancho de la cancha.
     */
    public double getFieldWidth() {
        return field.getRightWallX() - field.getLeftWallX();
    }

    /**
     * @return Coordenada Y del suelo.
     */
    public double getGroundY() {
        return field.getGroundY();
    }

    /**
     * @return Segundos restantes del partido (0 si no hay limite de tiempo).
     */
    public double getRemainingSeconds() {
        return selectedMatchType == null ? 0.0 : selectedMatchType.getRemainingSeconds();
    }

    /**
     * @return true si el partido esta en Gol de Oro.
     */
    public boolean isGoldenGoal() {
        return selectedMatchType != null && selectedMatchType.isGoldenGoal();
    }

    /**
     * @return true si el partido ya termino.
     */
    public boolean isMatchFinished() {
        return started && selectedMatchType.isFinished();
    }

    /** @return Nombre del ganador, o cadena vacia si aun no hay. */
    public String getWinnerName() {
        return isMatchFinished() ? selectedMatchType.getWinner().getName() : "";
    }

    /**
     * @return Configuracion del juego (sonido, musica e idioma).
     */
    public Settings getSettings() {
        return settings;
    }

    // Acceso interno (mismo paquete) para pruebas unitarias.
    Ball getBall() {
        return ball;
    }

    Character getPlayer1() {
        return player1;
    }

    Character getPlayer2() {
        return player2;
    }

    MatchType getMatchType() {
        return selectedMatchType;
    }
}
