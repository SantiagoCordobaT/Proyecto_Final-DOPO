package dominio;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Action;
import dominio.modalidades.GameMode;
import dominio.modalidades.GameModeCatalog;
import dominio.partidos.MatchType;
import dominio.partidos.MatchTypeCatalog;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Fachada (Facade / Controller) del dominio de POOBSoccer. La presentacion solo
 * usa tipos simples (String, int, boolean, double y arreglos) a traves de esta
 * clase: aqui se validan las selecciones y se delega el partido en {@link Match}.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class HeadSoccer {

    private final GameModeCatalog modes;
    private final MatchTypeCatalog matchTypes;
    private final CharacterCatalog characters;
    private final Settings settings;

    private GameMode selectedMode;
    private MatchType selectedMatchType;
    private Character player1;
    private Character player2;
    private Match match;

    /**
     * Crea el juego con sus catalogos de modos, tipos de partido y personajes.
     *
     * @throws POOBSoccerException Si algun dato del catalogo fuera invalido.
     */
    public HeadSoccer() throws POOBSoccerException {
        this.settings = new Settings();
        this.modes = new GameModeCatalog();
        this.matchTypes = new MatchTypeCatalog();
        this.characters = new CharacterCatalog();
    }

    // ------------------------------------------------------------ catalogos

    /** @return Nombres de las modalidades. */
    public String[] getModeNames() {
        return modes.names();
    }

    /** @return Nombres de los tipos de partido. */
    public String[] getMatchTypeNames() {
        return matchTypes.names();
    }

    /** @return Nombres de todos los personajes del catálogo (para uso directo en vistas). */
    public String[] getCharacterNames() {
        return characters.names();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Pais del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterCountry(int index) throws POOBSoccerException {
        return characters.get(index).getCountry();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Velocidad del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterSpeed(int index) throws POOBSoccerException {
        return characters.get(index).getSpeed();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Salto del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterJump(int index) throws POOBSoccerException {
        return characters.get(index).getJump();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Fuerza del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterStrength(int index) throws POOBSoccerException {
        return characters.get(index).getStrength();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Estadistica de Poder Unico del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterPowerLevel(int index) throws POOBSoccerException {
        return characters.get(index).getPowerLevel();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Nombre del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterPowerName(int index) throws POOBSoccerException {
        return characters.get(index).getPowerName();
    }

    /**
     * @param index Indice en el catalogo.
     * @return Descripcion del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterPowerDescription(int index) throws POOBSoccerException {
        return characters.get(index).getPowerDescription();
    }

    // ------------------------------------------------------------ selecciones

    /**
     * Elige la modalidad.
     *
     * @param index Indice de la modalidad.
     * @throws POOBSoccerException Si el indice es invalido o es incompatible con el tipo de partido elegido.
     */
    public void selectMode(int index) throws POOBSoccerException {
        GameMode mode = modes.get(index);
        if (selectedMatchType != null) {
            requireCompatible(mode, selectedMatchType);
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
        MatchType type = matchTypes.get(index);
        if (selectedMode != null) {
            requireCompatible(selectedMode, type);
        }
        selectedMatchType = type;
    }

    /** El tipo de partido decide si admite la modalidad (solo Supervivencia la restringe a PvsM). */
    private static void requireCompatible(GameMode mode, MatchType type) throws POOBSoccerException {
        if (!type.supports(mode)) {
            throw new POOBSoccerException(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM);
        }
    }

    /**
     * Elige el personaje del jugador 1 (se clona del catalogo).
     *
     * @param index Indice del catalogo.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public void selectPlayer1(int index) throws POOBSoccerException {
        player1 = characters.get(index).copy();
    }

    /**
     * Elige el personaje del jugador 2 (se clona del catalogo).
     *
     * @param index Indice del catalogo.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public void selectPlayer2(int index) throws POOBSoccerException {
        player2 = characters.get(index).copy();
    }

    // ------------------------------------------------------------ partido

    /**
     * Inicia un partido nuevo con la configuracion elegida (Creator del partido).
     *
     * @throws POOBSoccerException Si falta modalidad, tipo de partido o algun jugador.
     */
    public void startMatch() throws POOBSoccerException {
        if (selectedMode == null || selectedMatchType == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        if (player1 == null || player2 == null) {
            throw new POOBSoccerException(POOBSoccerException.JUGADORES_INCOMPLETOS);
        }
        match = new Match(selectedMode, selectedMatchType, player1, player2, characters);
    }

    private Match requireMatch() throws POOBSoccerException {
        if (match == null) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_NO_INICIADO);
        }
        return match;
    }

    /**
     * Ciclo principal del juego.
     *
     * @param deltaTimeSeconds Segundos transcurridos desde el tick anterior.
     * @throws POOBSoccerException Si el partido no inicio o el tiempo es negativo.
     */
    public void updateTick(double deltaTimeSeconds) throws POOBSoccerException {
        requireMatch().updateTick(deltaTimeSeconds);
    }

    /**
     * Pausa el partido y suelta las teclas mantenidas.
     *
     * @throws POOBSoccerException Si el partido no ha iniciado.
     */
    public void pause() throws POOBSoccerException {
        requireMatch().pause();
    }

    /**
     * Reanuda el partido.
     *
     * @throws POOBSoccerException Si el partido no ha iniciado.
     */
    public void resume() throws POOBSoccerException {
        requireMatch().resume();
    }

    /**
     * El jugador empieza a mantener una accion (tecla presionada). Solo la usa
     * un controlador humano; la maquina la ignora.
     *
     * @param number Jugador (1 o 2).
     * @param action LEFT, RIGHT, JUMP, KICK o POWER.
     * @throws POOBSoccerException Si no hay partido, el jugador no es valido o la accion no existe.
     */
    public void pressAction(int number, String action) throws POOBSoccerException {
        requireMatch().press(number, Action.named(action));
    }

    /**
     * El jugador deja de mantener una accion (tecla soltada).
     *
     * @param number Jugador (1 o 2).
     * @param action LEFT, RIGHT, JUMP, KICK o POWER.
     * @throws POOBSoccerException Si no hay partido, el jugador no es valido o la accion no existe.
     */
    public void releaseAction(int number, String action) throws POOBSoccerException {
        requireMatch().release(number, Action.named(action));
    }

    // ------------------------------------------------------------ consultas para la vista

    /**
     * @param number Jugador (1 o 2).
     * @return Nombre del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerName(int number) throws POOBSoccerException {
        return requireMatch().getPlayerName(number);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Pais del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerCountry(int number) throws POOBSoccerException {
        return requireMatch().getPlayerCountry(number);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Hitbox actual del jugador como {izquierda, arriba, ancho, alto}.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double[] getPlayerBounds(int number) throws POOBSoccerException {
        return requireMatch().getPlayerBounds(number);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Goles del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public int getPlayerGoals(int number) throws POOBSoccerException {
        return requireMatch().getGoals(number);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Fraccion llena de la barra de poder (0.0 a 1.0).
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double getPlayerPowerBar(int number) throws POOBSoccerException {
        return requireMatch().getPlayerPowerCharge(number);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Nombre del poder del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerPowerName(int number) throws POOBSoccerException {
        return requireMatch().getPlayerPowerName(number);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return true si el jugador tiene su poder armado.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean isPlayerPowerArmed(int number) throws POOBSoccerException {
        return requireMatch().isPlayerPowerArmed(number);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Nombres de los efectos activos del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String[] getPlayerEffects(int number) throws POOBSoccerException {
        return requireMatch().getPlayerEffects(number);
    }

    /**
     * @return Posicion horizontal del balon.
     * @throws POOBSoccerException Si no hay partido.
     */
    public double getBallX() throws POOBSoccerException {
        return requireMatch().getBallX();
    }

    /**
     * @return Posicion vertical del balon.
     * @throws POOBSoccerException Si no hay partido.
     */
    public double getBallY() throws POOBSoccerException {
        return requireMatch().getBallY();
    }

    /**
     * @return Estado del partido segun su tipo: tiempo restante o "GOL DE ORO", meta de
     *         goles y tiempo jugado, o balones y racha de Supervivencia.
     * @throws POOBSoccerException Si no hay partido.
     */
    public String getMatchStatus() throws POOBSoccerException {
        return requireMatch().getStatus();
    }

    /**
     * @return true si el partido ya termino.
     * @throws POOBSoccerException Si no hay partido.
     */
    public boolean isMatchFinished() throws POOBSoccerException {
        return requireMatch().isFinished();
    }

    /**
     * @return Nombre del personaje ganador.
     * @throws POOBSoccerException Si no hay partido o aun no termina.
     */
    public String getWinnerName() throws POOBSoccerException {
        return requireMatch().getWinnerName();
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
     * @return Configuracion del juego (sonido, musica e idioma).
     */
    public Settings getSettings() {
        return settings;
    }
}
