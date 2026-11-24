package dominio;

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
 * Punto de entrada del juego para la interfaz gráfica.
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

    public static HeadSoccer obtenerInstancia() throws POOBSoccerException {
        if (instancia == null) {
            instancia = new HeadSoccer();
        }
        return instancia;
    }

    public void nuevoJuego() {
        modoSeleccionado = null;
        tipoPartidoSeleccionado = null;
        elegidos[0] = null;
        elegidos[1] = null;
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

    public int crearPersonaje(String nombre, String pais, int velocidad, int salto, int fuerza, int nivelPoder,
            int indicePoder) throws POOBSoccerException {
        personajes.crear(nombre, pais, velocidad, salto, fuerza, nivelPoder, poderes.obtener(indicePoder));
        return personajes.cantidad() - 1;
    }

    public void editarPersonaje(int indice, String nombre, String pais, int velocidad, int salto, int fuerza,
            int nivelPoder, int indicePoder) throws POOBSoccerException {
        personajes.editar(indice, nombre, pais, velocidad, salto, fuerza, nivelPoder, poderes.obtener(indicePoder));
        elegidos[0] = null;
        elegidos[1] = null;
    }

    public void eliminarPersonaje(int indice) throws POOBSoccerException {
        personajes.eliminarPersonaje(indice);
        elegidos[0] = null;
        elegidos[1] = null;
    }

    public void seleccionarModo(int indice) throws POOBSoccerException {
        modoSeleccionado = modos.obtener(indice);
        tipoPartidoSeleccionado = null;
    }

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

    public void iniciarPartido() throws POOBSoccerException {
        if (tipoPartidoSeleccionado == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        if (elegidos[0] == null || elegidos[1] == null) {
            throw new POOBSoccerException(POOBSoccerException.JUGADORES_INCOMPLETOS);
        }
        // Se copian los personajes para que una revancha no conserve goles ni efectos
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
