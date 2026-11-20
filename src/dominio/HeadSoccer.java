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
 * {@link #nuevoJuego()} reinicia.
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
    public static HeadSoccer obtenerInstancia() throws POOBSoccerException {
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
    public void nuevoJuego() {
        modoSeleccionado = null;
        tipoPartidoSeleccionado = null;
        Arrays.fill(elegidos, null);
        partido = null;
    }

    // ------------------------------------------------------------ catalogos

    /** @return Nombres de las modalidades. */
    public String[] obtenerNombresModos() {
        return modos.obtenerNombres();
    }

    /** @return Nombres de los tipos de partido. */
    public String[] obtenerNombresTiposPartido() {
        return tiposPartido.obtenerNombres();
    }

    /**
     * @return Nombres de todos los personajes del catálogo (para uso directo en
     *         vistas).
     */
    public String[] obtenerNombresPersonajes() {
        return personajes.obtenerNombres();
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Pais del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String obtenerPaisPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerPais(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Velocidad del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int obtenerVelocidadPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerVelocidad(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Salto del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int obtenerSaltoPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerSalto(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Fuerza del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int obtenerFuerzaPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerFuerza(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Estadistica de Poder Unico del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int obtenerNivelPoderPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerNivelPoder(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Nombre del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String obtenerNombrePoderPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerNombrePoder(indice);
    }

    /**
     * @param indice Indice en el catalogo.
     * @return Descripcion del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String obtenerDescripcionPoderPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerDescripcionPoder(indice);
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
    public void seleccionarModo(int indice) throws POOBSoccerException {
        modoSeleccionado = modos.obtener(indice);
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
    public void seleccionarTipoPartido(int indice) throws POOBSoccerException {
        if (modoSeleccionado == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        MatchType tipo = tiposPartido.obtener(indice);
        if (!tipo.admite(modoSeleccionado)) {
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
    public void seleccionarJugador(int numero, int indicePersonaje) throws POOBSoccerException {
        elegidos[Match.indiceJugador(numero)] = personajes.obtener(indicePersonaje);
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
    public void iniciarPartido() throws POOBSoccerException {
        if (tipoPartidoSeleccionado == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        if (Arrays.asList(elegidos).contains(null)) {
            throw new POOBSoccerException(POOBSoccerException.JUGADORES_INCOMPLETOS);
        }
        partido = new Match(modoSeleccionado, tipoPartidoSeleccionado, elegidos[0].copiar(), elegidos[1].copiar(),
                personajes);
    }

    private Match requerirPartido() throws POOBSoccerException {
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
    public void actualizarTick(double segundosTranscurridos) throws POOBSoccerException {
        requerirPartido().actualizarTick(segundosTranscurridos);
    }

    /**
     * Pausa el partido y suelta las teclas mantenidas.
     *
     * @throws POOBSoccerException Si el partido no ha iniciado.
     */
    public void pausar() throws POOBSoccerException {
        requerirPartido().pausar();
    }

    /**
     * Suelta las teclas mantenidas sin pausar, por ejemplo cuando la ventana pierde
     * el
     * foco y ya no recibira las teclas que se suelten fuera de ella.
     *
     * @throws POOBSoccerException Si el partido no ha iniciado.
     */
    public void soltarTodasLasAcciones() throws POOBSoccerException {
        requerirPartido().soltarTodo();
    }

    /**
     * Reanuda el partido.
     *
     * @throws POOBSoccerException Si el partido no ha iniciado.
     */
    public void reanudar() throws POOBSoccerException {
        requerirPartido().reanudar();
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
    public void presionarAccion(int numero, String accion) throws POOBSoccerException {
        requerirPartido().presionar(numero, Action.porNombre(accion));
    }

    /**
     * El jugador deja de mantener una accion (tecla soltada).
     *
     * @param numero Jugador (1 o 2).
     * @param accion LEFT, RIGHT, JUMP, KICK o POWER.
     * @throws POOBSoccerException Si no hay partido, el jugador no es valido o la
     *                             accion no existe.
     */
    public void soltarAccion(int numero, String accion) throws POOBSoccerException {
        requerirPartido().soltar(numero, Action.porNombre(accion));
    }

    // ------------------------------------------------------------ consultas para
    // la vista

    /**
     * @param numero Jugador (1 o 2).
     * @return Nombre del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String obtenerNombreJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerNombreJugador(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Pais del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String obtenerPaisJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerPaisJugador(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Hitbox actual del jugador como {izquierda, arriba, ancho, alto}.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double[] obtenerLimitesJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerLimitesJugador(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Goles del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public int obtenerGolesJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerGoles(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Fraccion llena de la barra de poder (0.0 a 1.0).
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public double obtenerBarraPoderJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerCargaPoderJugador(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Nombre del poder del personaje del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String obtenerNombrePoderJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerNombrePoderJugador(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return true si el jugador tiene su poder armado.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean estaPoderJugadorArmado(int numero) throws POOBSoccerException {
        return requerirPartido().estaPoderJugadorArmado(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Nombres de los efectos activos del jugador.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public String[] obtenerEfectosJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerEfectosJugador(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Sentido de ataque del jugador: 1 hacia la derecha, -1 hacia la
     *         izquierda.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public int obtenerOrientacionJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerOrientacionJugador(numero);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return true mientras dura la patada que el jugador acaba de ejecutar.
     * @throws POOBSoccerException Si el jugador no es valido o no hay partido.
     */
    public boolean estaJugadorPateando(int numero) throws POOBSoccerException {
        return requerirPartido().estaJugadorPateando(numero);
    }

    /**
     * @return Centro del balon como {x, y}, suavizado entre pasos de fisica.
     * @throws POOBSoccerException Si no hay partido.
     */
    public double[] obtenerPosicionBalon() throws POOBSoccerException {
        return requerirPartido().obtenerPosicionBalon();
    }

    /**
     * @return Estado del partido segun su tipo: meta de goles y tiempo jugado, o
     *         balones y racha de Supervivencia.
     * @throws POOBSoccerException Si no hay partido.
     */
    public String obtenerEstadoPartido() throws POOBSoccerException {
        return requerirPartido().obtenerEstado();
    }

    /**
     * @return true si el partido ya termino.
     * @throws POOBSoccerException Si no hay partido.
     */
    public boolean estaPartidoTerminado() throws POOBSoccerException {
        return requerirPartido().estaTerminado();
    }

    /**
     * @return Cada arco de la cancha (el izquierdo y luego el derecho)
     * 
     */
    public double[][] obtenerLimitesArcos() throws POOBSoccerException {
        return requerirPartido().obtenerLimitesArcos();
    }

    /**
     * @return Nombre del personaje ganador.
     * @throws POOBSoccerException Si no hay partido o aun no termina.
     */
    public String obtenerNombreGanador() throws POOBSoccerException {
        return requerirPartido().obtenerNombreGanador();
    }

    /**
     * @return Radio del balon.
     */
    public double obtenerRadioBalon() {
        return Ball.RADIO;
    }

    /**
     * @return Ancho de la cancha.
     */
    public double obtenerAnchoCancha() {
        return Field.ANCHO;
    }

    /**
     * @return Coordenada Y del suelo.
     */
    public double obtenerSueloY() {
        return Field.SUELO_Y;
    }

    /**
     * @return Grosor del travesaño de cada arco.
     */
    public double obtenerGrosorTravesano() {
        return Goal.GROSOR_BORDE;
    }
}
