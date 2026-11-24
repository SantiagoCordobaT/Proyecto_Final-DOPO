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
import dominio.poderes.PowerCatalog;

/**
 * Fachada pública del dominio de POOBSoccer: la vista usa solo tipos simples a
 * través de esta clase para configurar el juego y consultar la partida.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public final class HeadSoccer {

    private static HeadSoccer instancia;

    private final GameModeCatalog modos;
    private final MatchTypeCatalog tiposPartido;
    private final PowerCatalog poderes;
    private final CharacterCatalog personajes;
    private final Character[] elegidos = new Character[Match.JUGADORES];

    private GameMode modoSeleccionado;
    private MatchType tipoPartidoSeleccionado;
    private Match partido;

    private HeadSoccer() throws POOBSoccerException {
        this.modos = new GameModeCatalog();
        this.tiposPartido = new MatchTypeCatalog();
        this.poderes = new PowerCatalog();
        this.personajes = new CharacterCatalog();
    }

    /** Devuelve la única instancia compartida del juego. */
    public static HeadSoccer obtenerInstancia() throws POOBSoccerException {
        if (instancia == null) {
            instancia = new HeadSoccer();
        }
        return instancia;
    }

    /**
     * Reinicia la sesión olvidando modalidad, tipo de partido, elegidos y partida
     * activa.
     */
    public void nuevoJuego() {
        modoSeleccionado = null;
        tipoPartidoSeleccionado = null;
        Arrays.fill(elegidos, null);
        partido = null;
    }

    public String[] obtenerNombresModos() {
        return modos.obtenerNombres();
    }

    public String[] obtenerNombresTiposPartido() {
        return tiposPartido.obtenerNombres();
    }

    public String[] obtenerNombresPersonajes() {
        return personajes.obtenerNombres();
    }

    public String obtenerPaisPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerPais(indice);
    }

    public int obtenerVelocidadPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerVelocidad(indice);
    }

    public int obtenerSaltoPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerSalto(indice);
    }

    public int obtenerFuerzaPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerFuerza(indice);
    }

    public int obtenerNivelPoderPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerNivelPoder(indice);
    }

    public String obtenerNombrePoderPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerNombrePoder(indice);
    }

    public String obtenerDescripcionPoderPersonaje(int indice) throws POOBSoccerException {
        return personajes.obtenerDescripcionPoder(indice);
    }

    public String[] obtenerNombresPoderes() {
        return poderes.obtenerNombres();
    }

    /** Crea un personaje personalizado y devuelve su índice en el catálogo. */
    public int crearPersonaje(String nombre, String pais, int velocidad, int salto, int fuerza, int nivelPoder,
            int indicePoder) throws POOBSoccerException {
        personajes.crear(nombre, pais, velocidad, salto, fuerza, nivelPoder, poderes.obtener(indicePoder));
        return personajes.cantidad() - 1;
    }

    /**
     * Edita un personaje del catálogo y limpia la selección actual para evitar
     * datos viejos.
     */
    public void editarPersonaje(int indice, String nombre, String pais, int velocidad, int salto, int fuerza,
            int nivelPoder, int indicePoder) throws POOBSoccerException {
        personajes.editar(indice, nombre, pais, velocidad, salto, fuerza, nivelPoder, poderes.obtener(indicePoder));
        Arrays.fill(elegidos, null);
    }

    /**
     * Elimina un personaje manteniendo al menos dos disponibles y limpia la
     * selección actual.
     */
    public void eliminarPersonaje(int indice) throws POOBSoccerException {
        personajes.eliminarPersonaje(indice);
        Arrays.fill(elegidos, null);
    }

    /** Elige la modalidad y borra el tipo de partido previo para revalidarlo. */
    public void seleccionarModo(int indice) throws POOBSoccerException {
        modoSeleccionado = modos.obtener(indice);
        tipoPartidoSeleccionado = null;
    }

    /**
     * Elige el tipo de partido verificando compatibilidad con la modalidad elegida.
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

    public void seleccionarJugador(int numero, int indicePersonaje) throws POOBSoccerException {
        elegidos[Match.indiceJugador(numero)] = personajes.obtener(indicePersonaje);
    }

    /**
     * Inicia un partido usando copias nuevas de los personajes elegidos para
     * permitir revanchas limpias.
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

    public void actualizarTick(double segundosTranscurridos) throws POOBSoccerException {
        requerirPartido().actualizarTick(segundosTranscurridos);
    }

    public void pausar() throws POOBSoccerException {
        requerirPartido().pausar();
    }

    /** Suelta teclas activas sin pausar (útil cuando la ventana pierde el foco). */
    public void soltarTodasLasAcciones() throws POOBSoccerException {
        requerirPartido().soltarTodo();
    }

    public void reanudar() throws POOBSoccerException {
        requerirPartido().reanudar();
    }

    public void presionarAccion(int numero, String accion) throws POOBSoccerException {
        requerirPartido().presionar(numero, Action.porNombre(accion));
    }

    public void soltarAccion(int numero, String accion) throws POOBSoccerException {
        requerirPartido().soltar(numero, Action.porNombre(accion));
    }

    public String obtenerNombreJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerNombreJugador(numero);
    }

    public String obtenerPaisJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerPaisJugador(numero);
    }

    public double[] obtenerLimitesJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerLimitesJugador(numero);
    }

    public int obtenerGolesJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerGoles(numero);
    }

    public double obtenerBarraPoderJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerCargaPoderJugador(numero);
    }

    public String obtenerNombrePoderJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerNombrePoderJugador(numero);
    }

    public boolean estaPoderJugadorArmado(int numero) throws POOBSoccerException {
        return requerirPartido().estaPoderJugadorArmado(numero);
    }

    public String[] obtenerEfectosJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerEfectosJugador(numero);
    }

    public int obtenerOrientacionJugador(int numero) throws POOBSoccerException {
        return requerirPartido().obtenerOrientacionJugador(numero);
    }

    public boolean estaJugadorPateando(int numero) throws POOBSoccerException {
        return requerirPartido().estaJugadorPateando(numero);
    }

    public double[] obtenerPosicionBalon() throws POOBSoccerException {
        return requerirPartido().obtenerPosicionBalon();
    }

    public String obtenerEstadoPartido() throws POOBSoccerException {
        return requerirPartido().obtenerEstado();
    }

    public boolean estaPartidoTerminado() throws POOBSoccerException {
        return requerirPartido().estaTerminado();
    }

    public double[][] obtenerLimitesArcos() throws POOBSoccerException {
        return requerirPartido().obtenerLimitesArcos();
    }

    public String obtenerNombreGanador() throws POOBSoccerException {
        return requerirPartido().obtenerNombreGanador();
    }

    public double obtenerRadioBalon() {
        return Ball.RADIO;
    }

    public double obtenerAnchoCancha() {
        return Field.ANCHO;
    }

    public double obtenerSueloY() {
        return Field.SUELO_Y;
    }

    public double obtenerGrosorTravesano() {
        return Goal.GROSOR_BORDE;
    }
}
