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
 * Fachada principal de POOBSoccer. Conecta la interfaz gráfica con los
 * catálogos y el partido activo.
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
        modos = new GameModeCatalog();
        tiposPartido = new MatchTypeCatalog();
        poderes = new PowerCatalog();
        personajes = new CharacterCatalog();
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

    public String[] obtenerNombresPoderes() {
        return poderes.obtenerNombres();
    }

    public CharacterInfo obtenerFichaPersonaje(int indice) throws POOBSoccerException {
        Character p = personajes.obtener(indice);
        return new CharacterInfo(p.obtenerNombre(), p.obtenerPais(), p.obtenerVelocidad(), p.obtenerSalto(),
                p.obtenerFuerza(), p.obtenerNivelPoder(), p.obtenerNombrePoder(), p.obtenerDescripcionPoder());
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
        // Copiamos para que una revancha empiece desde cero
        partido = new Match(modoSeleccionado, tipoPartidoSeleccionado, elegidos[0].copiar(), elegidos[1].copiar(),
                personajes);
    }

    private Match partidoActivo() throws POOBSoccerException {
        if (partido == null) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_NO_INICIADO);
        }
        return partido;
    }

    public void actualizarTick(double segundos) throws POOBSoccerException {
        partidoActivo().actualizarTick(segundos);
    }

    public void pausar() throws POOBSoccerException {
        partidoActivo().pausar();
    }

    public void reanudar() throws POOBSoccerException {
        partidoActivo().reanudar();
    }

    public void soltarTodasLasAcciones() throws POOBSoccerException {
        partidoActivo().soltarTodo();
    }

    public void presionarAccion(int numero, String accion) throws POOBSoccerException {
        partidoActivo().presionar(numero, Action.porNombre(accion));
    }

    public void soltarAccion(int numero, String accion) throws POOBSoccerException {
        partidoActivo().soltar(numero, Action.porNombre(accion));
    }

    public PlayerSummary obtenerResumenJugador(int numero) throws POOBSoccerException {
        return partidoActivo().obtenerResumenJugador(numero);
    }

    public double[] obtenerPosicionBalon() throws POOBSoccerException {
        return partidoActivo().obtenerPosicionBalon();
    }

    public double[][] obtenerLimitesArcos() throws POOBSoccerException {
        return partidoActivo().obtenerLimitesArcos();
    }

    public String obtenerEstadoPartido() throws POOBSoccerException {
        return partidoActivo().obtenerEstado();
    }

    public boolean estaPartidoTerminado() throws POOBSoccerException {
        return partidoActivo().estaTerminado();
    }

    public String obtenerNombreGanador() throws POOBSoccerException {
        return partidoActivo().obtenerNombreGanador();
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
