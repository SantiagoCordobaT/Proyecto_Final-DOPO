package dominio;

import java.util.Arrays;

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
 * clase: aqui se validan las selecciones y se delega el partido en
 * {@link Match}.
 * Es Singleton: toda la aplicacion comparte una unica sesion de juego, que
 * {@link #newGame()} reinicia.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public final class HeadSoccer {

    private static HeadSoccer instancia;

    private final GameModeCatalog modos;
    private final MatchTypeCatalog tiposPartido;
    private final CharacterCatalog personajes;
    private final Character[] elegidos = new Character[Match.JUGADORES];

    private GameMode modoSeleccionado;
    private MatchType tipoPartidoSeleccionado;
    private Match partido;

    /**
     * Crea el juego con sus catalogos de modos, tipos de partido y personajes
     * (Creator).
     *
     * @throws POOBSoccerException Si algun dato del catalogo fuera invalido.
     */
    private HeadSoccer() throws POOBSoccerException {
        this.modos = new GameModeCatalog();
        this.tiposPartido = new MatchTypeCatalog();
        this.personajes = new CharacterCatalog();
    }

    /**
     * @return La unica instancia del juego; se crea en la primera llamada.
     * @throws POOBSoccerException Si algun dato del catalogo fuera invalido.
     */
    public static HeadSoccer getInstance() throws POOBSoccerException {
        if (instancia == null) {
            instancia = new HeadSoccer();
        }
        return instancia;
    }

    /**
     * Empieza una sesion nueva: olvida la modalidad, el tipo de partido, los
     * personajes
     * elegidos y el partido anterior.
     */
    public void newGame() {
        modoSeleccionado = null;
        tipoPartidoSeleccionado = null;
        Arrays.fill(elegidos, null);
        partido = null;
    }

    // ------------------------------------------------------------ catalogos

    /** @return Nombres de las modalidades. */
    public String[] getModeNames() {
        return modos.names();
    }

    /** @return Nombres de los tipos de partido. */
    public String[] getMatchTypeNames() {
        return tiposPartido.names();
    }

    /**
     * @return Nombres de todos los personajes del catálogo (para uso directo en
     *         vistas).
     */
    public String[] getCharacterNames() {
        return personajes.names();
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Pais del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterCountry(int indice) throws POOBSoccerException {
        return personajes.getCountry(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Velocidad del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterSpeed(int indice) throws POOBSoccerException {
        return personajes.getSpeed(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Salto del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterJump(int indice) throws POOBSoccerException {
        return personajes.getJump(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Fuerza del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterStrength(int indice) throws POOBSoccerException {
        return personajes.getStrength(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Estadistica de Poder Unico del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getCharacterPowerLevel(int indice) throws POOBSoccerException {
        return personajes.getPowerLevel(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Nombre del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterPowerName(int indice) throws POOBSoccerException {
        return personajes.getPowerName(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Descripcion del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCharacterPowerDescription(int indice) throws POOBSoccerException {
        return personajes.getPowerDescription(indice);
    }

    // ------------------------------------------------------------ selecciones

    /**
     * Elige la modalidad (primer paso de la configuracion). Olvida el tipo de
     * partido
     * elegido, que debe volver a validarse contra la nueva modalidad.
     *
     * @param indice Indice de la modalidad.
     * @throws POOBSoccerException Si el indice es invalido.
     */
    public void selectMode(int indice) throws POOBSoccerException {
        modoSeleccionado = modos.get(indice);
        tipoPartidoSeleccionado = null;
    }

    /**
     * Elige el tipo de partido para la modalidad ya elegida; el tipo decide si la
     * admite (solo Supervivencia la restringe a PvsM).
     *
     * @param indice Indice del tipo de partido.
     * @throws POOBSoccerException Si no hay modalidad, el indice es invalido o el
     *                             tipo no admite la modalidad.
     */
    public void selectMatchType(int indice) throws POOBSoccerException {
        if (modoSeleccionado == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        MatchType tipo = tiposPartido.get(indice);
        if (!tipo.supports(modoSeleccionado)) {
            throw new POOBSoccerException(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM);
        }
        tipoPartidoSeleccionado = tipo;
    }

    /**
     * Elige el personaje de un jugador.
     *
     * @param numero          Jugador (1 o 2).
     * @param indicePersonaje Indice del personaje en el catalogo.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2 o el personaje no
     *                             existe.
     */
    public void selectPlayer(int numero, int indicePersonaje) throws POOBSoccerException {
        elegidos[Match.index(numero)] = personajes.get(indicePersonaje);
    }

    // ------------------------------------------------------------ partido

    /**
     * Inicia un partido nuevo con la configuracion elegida (Creator del partido).
     * Cada
     * partido recibe copias de los personajes elegidos (Prototype), asi una
     * revancha
     * empieza con los mismos personajes y sin estado del partido anterior.
     *
     * @throws POOBSoccerException Si falta el tipo de partido (o la modalidad) o
     *                             algun jugador.
     */
    public void startMatch() throws POOBSoccerException {
        if (tipoPartidoSeleccionado == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        if (Arrays.asList(elegidos).contains(null)) {
            throw new POOBSoccerException(POOBSoccerException.JUGADORES_INCOMPLETOS);
        }
        partido = new Match(modoSeleccionado, tipoPartidoSeleccionado, elegidos[0].copy(), elegidos[1].copy(),
                personajes);
    }

    private Match requireMatch() throws POOBSoccerException {
        if (partido == null) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_NO_INICIADO);
        }
        return partido;
    }

    /**
     * Ciclo principal del juego.
     *
     * @param segundosTranscurridos Segundos transcurridos desde el tick anterior.
     * @throws POOBSoccerException Si el partido no inicio o el tiempo es negativo.
     */
    public void updateTick(double segundosTranscurridos) throws POOBSoccerException {
        requireMatch().updateTick(segundosTranscurridos);
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
     * Suelta las teclas mantenidas sin pausar, por ejemplo cuando la ventana pierde
     * el
     * foco y ya no recibira las teclas que se suelten fuera de ella.
     *
     * @throws POOBSoccerException Si el partido no ha iniciado.
     */
    public void releaseAllActions() throws POOBSoccerException {
        requireMatch().releaseAll();
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
     * @param numero Jugador (1 o 2).
     * @param accion LEFT, RIGHT, JUMP, KICK o POWER.
     * @throws POOBSoccerException Si no hay partido, el jugador no es valido o la
     *                             accion no existe.
     */
    public void pressAction(int numero, String accion) throws POOBSoccerException {
        requireMatch().press(numero, Action.named(accion));
    }

    /**
     * El jugador deja de mantener una accion (tecla soltada).
     *
     * @param numero Jugador (1 o 2).
     * @param accion LEFT, RIGHT, JUMP, KICK o POWER.
     * @throws POOBSoccerException Si no hay partido, el jugador no es valido o la
     *                             accion no existe.
     */
    public void releaseAction(int numero, String accion) throws POOBSoccerException {
        requireMatch().release(numero, Action.named(accion));
    }

    // ------------------------------------------------------------ consultas para
    // la vista

    /**
     * @param numero Jugador (1 o 2).
     * @return Nombre del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerName(int numero) throws POOBSoccerException {
        return requireMatch().getPlayerName(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Pais del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerCountry(int numero) throws POOBSoccerException {
        return requireMatch().getPlayerCountry(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Hitbox actual del jugador como {izquierda, arriba, ancho, alto}.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double[] getPlayerBounds(int numero) throws POOBSoccerException {
        return requireMatch().getPlayerBounds(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Goles del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public int getPlayerGoals(int numero) throws POOBSoccerException {
        return requireMatch().getGoals(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Fraccion llena de la barra de poder (0.0 a 1.0).
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double getPlayerPowerBar(int numero) throws POOBSoccerException {
        return requireMatch().getPlayerPowerCharge(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Nombre del poder del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String getPlayerPowerName(int numero) throws POOBSoccerException {
        return requireMatch().getPlayerPowerName(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return true si el jugador tiene su poder armado.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean isPlayerPowerArmed(int numero) throws POOBSoccerException {
        return requireMatch().isPlayerPowerArmed(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Nombres de los efectos activos del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String[] getPlayerEffects(int numero) throws POOBSoccerException {
        return requireMatch().getPlayerEffects(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Sentido de ataque del jugador: 1 hacia la derecha, -1 hacia la
     *         izquierda.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public int getPlayerFacing(int numero) throws POOBSoccerException {
        return requireMatch().getPlayerFacing(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return true mientras dura la patada que el jugador acaba de ejecutar.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean isPlayerKicking(int numero) throws POOBSoccerException {
        return requireMatch().isPlayerKicking(numero);
    }

    /**
     * @return Centro del balon como {x, y}, suavizado entre pasos de fisica.
     * @throws POOBSoccerException Si no hay partido.
     */
    public double[] getBallPosition() throws POOBSoccerException {
        return requireMatch().getBallPosition();
    }

    /**
     * @return Estado del partido segun su tipo: tiempo restante o "GOL DE ORO",
     *         meta de
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
     * @return Cada arco de la cancha (el izquierdo y luego el derecho)
     * 
     */
    public double[][] getGoalBounds() throws POOBSoccerException {
        return requireMatch().getGoalBounds();
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
        return Ball.RADIO;
    }

    /**
     * @return Ancho de la cancha.
     */
    public double getFieldWidth() {
        return Field.ANCHO;
    }

    /**
     * @return Coordenada Y del suelo.
     */
    public double getGroundY() {
        return Field.SUELO_Y;
    }

    /**
     * @return Grosor del travesaño de cada arco.
     */
    public double getCrossbarThickness() {
        return Goal.GROSOR_TRAVESANO;
    }
}
