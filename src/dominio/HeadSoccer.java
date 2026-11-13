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
        this.ball = new Ball();

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
        characters.add(new Character("Jacob", "Canadá", 3, 3, 4, 3, new FreezePower()));
        characters.add(new Character("Kenji", "Japón", 5, 4, 2, 2, new StunPower()));
        characters.add(new Character("Mateo", "Colombia", 4, 3, 3, 4, new ConfusionPower()));
        characters.add(new Character("Rafael", "Brasil", 4, 5, 3, 3, new ShrinkPower()));
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

    /** @return Nombres de todos los personajes del catálogo (para uso directo en vistas). */
    public String[] getCharacterNames() {
        String[] names = new String[characters.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = characters.get(i).getName();
        }
        return names;
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
     * @return Estadistica de Poder Unico del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterPowerLevel(int index) throws POOBSoccerException {
        return catalogCharacter(index).getPowerLevel();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Nombre del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterPowerName(int index) throws POOBSoccerException {
        return catalogCharacter(index).getPowerName();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Descripcion del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterPowerDescription(int index) throws POOBSoccerException {
        return catalogCharacter(index).getPowerDescription();
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

        Side.LEFT.place(player1);
        Side.RIGHT.place(player2);
        field.dropBall(ball);
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

        controller1.control(player1, ball);
        controller2.control(player2, ball);

        player1.update(deltaTimeSeconds);
        player2.update(deltaTimeSeconds);

        physicsAccumulator += deltaTimeSeconds;
        while (physicsAccumulator >= PHYSICS_STEP_SECONDS - TIME_TOLERANCE) {
            physicsAccumulator -= PHYSICS_STEP_SECONDS;
            stepBall();
            int scorer = field.scorerOf(ball);
            if (scorer != Field.NO_GOAL) {
                registerGoal(scorer);
                physicsAccumulator = 0.0;
            }
        }

        selectedMatchType.tick(deltaTimeSeconds);
    }

    private void stepBall() {
        field.updateBall(ball);
        resolveContact(player1, player2);
        resolveContact(player2, player1);
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
     * Suma el gol a quien anota (el arco lo decide, asi el autogol queda cubierto) y reinicia la jugada.
     *
     * @param scorerNumber Jugador (1 o 2) que anota; el otro (3 - scorerNumber) recibe el gol.
     */
    private void registerGoal(int scorerNumber) throws POOBSoccerException {
        Character scorer = player(scorerNumber);
        scorer.addGoal();
        selectedMatchType.goalScored(scorer, player(3 - scorerNumber));
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
        Side.RIGHT.place(player2);
    }

    private void restartPlay() {
        player1.restart();
        player2.restart();
        field.dropBall(ball);
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
     * El jugador salta si esta en el suelo y ningun efecto lo impide.
     *
     * @param number Jugador (1 o 2).
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public void playerJump(int number) throws POOBSoccerException {
        player(number).jump();
    }

    /**
     * El jugador solicita una patada si ningun efecto lo impide.
     *
     * @param number Jugador (1 o 2).
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public void playerKick(int number) throws POOBSoccerException {
        player(number).requestKick();
    }

    /**
     * Arma el poder del jugador; con la barra incompleta no tiene efecto.
     *
     * @param number Jugador (1 o 2).
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public void activatePlayerPower(int number) throws POOBSoccerException {
        player(number).armPower();
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
     * @return Hitbox actual del jugador como {izquierda, arriba, ancho, alto}.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double[] getPlayerBounds(int number) throws POOBSoccerException {
        return player(number).getBounds();
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
     * @return Fraccion llena de la barra de poder (0.0 a 1.0).
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double getPlayerPowerBar(int number) throws POOBSoccerException {
        return player(number).getPowerCharge();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Nombre del poder del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerPowerName(int number) throws POOBSoccerException {
        return player(number).getPowerName();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return true si el jugador tiene su poder armado.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean isPlayerPowerArmed(int number) throws POOBSoccerException {
        return player(number).isPowerArmed();
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Nombres de los efectos activos del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String[] getPlayerEffects(int number) throws POOBSoccerException {
        return player(number).getEffectNames();
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
        return Ball.RADIUS;
    }

    /**
     * @return Ancho de la cancha.
     */
    public double getFieldWidth() {
        return Field.WIDTH;
    }

    /**
     * @return Coordenada Y del suelo.
     */
    public double getGroundY() {
        return Field.GROUND_Y;
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
}