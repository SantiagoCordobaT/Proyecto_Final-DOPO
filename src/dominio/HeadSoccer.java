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
 * Fachada principal de POOBSoccer. Administra los catálogos y crea y coordina
 * el partido activo a partir de un MatchSetup.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public final class HeadSoccer {

    private final GameModeCatalog modos;
    private final MatchTypeCatalog tiposPartido;
    private final PowerCatalog poderes;
    private final CharacterCatalog personajes;

    private Match partido;

    public HeadSoccer() throws POOBSoccerException {
        modos = new GameModeCatalog();
        tiposPartido = new MatchTypeCatalog();
        poderes = new PowerCatalog();
        personajes = new CharacterCatalog();
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
    }

    public void eliminarPersonaje(int indice) throws POOBSoccerException {
        personajes.eliminarPersonaje(indice);
    }

    public void validarModoYTipo(int indiceModo, int indiceTipo) throws POOBSoccerException {
        GameMode modo = modos.obtener(indiceModo);
        MatchType tipo = tiposPartido.obtener(indiceTipo);
        if (!tipo.admite(modo)) {
            throw new POOBSoccerException(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM);
        }
    }

    public void iniciarPartido(MatchSetup configuracion) throws POOBSoccerException {
        if (configuracion == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        GameMode modo = modos.obtener(configuracion.obtenerIndiceModo());
        MatchType tipo = tiposPartido.obtener(configuracion.obtenerIndiceTipo());
        if (!tipo.admite(modo)) {
            throw new POOBSoccerException(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM);
        }
        Character j1 = personajes.obtener(configuracion.obtenerIndiceJugador1()).copiar();
        Character j2 = personajes.obtener(configuracion.obtenerIndiceJugador2()).copiar();
        partido = new Match(modo, tipo, j1, j2, personajes);
    }

    public void iniciarPartido(int indiceModo, int indiceTipo, int indiceJugador1, int indiceJugador2)
            throws POOBSoccerException {
        iniciarPartido(new MatchSetup(indiceModo, indiceTipo, indiceJugador1, indiceJugador2));
    }

    public boolean hayPartidoActivo() {
        return partido != null;
    }

    private Match partidoActual() throws POOBSoccerException {
        if (partido == null) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_NO_INICIADO);
        }
        return partido;
    }

    public void actualizarTick(double segundos) throws POOBSoccerException {
        partidoActual().actualizarTick(segundos);
    }

    public void pausar() throws POOBSoccerException {
        partidoActual().pausar();
    }

    public void reanudar() throws POOBSoccerException {
        partidoActual().reanudar();
    }

    public void soltarTodasLasAcciones() throws POOBSoccerException {
        partidoActual().soltarTodo();
    }

    public void presionarAccion(int numero, String accion) throws POOBSoccerException {
        partidoActual().presionar(numero, Action.porNombre(accion));
    }

    public void soltarAccion(int numero, String accion) throws POOBSoccerException {
        partidoActual().soltar(numero, Action.porNombre(accion));
    }

    public PlayerSummary obtenerResumenJugador(int numero) throws POOBSoccerException {
        return partidoActual().obtenerResumenJugador(numero);
    }

    public double[] obtenerPosicionBalon() throws POOBSoccerException {
        return partidoActual().obtenerPosicionBalon();
    }

    public double[][] obtenerLimitesArcos() throws POOBSoccerException {
        return partidoActual().obtenerLimitesArcos();
    }

    public String obtenerEstadoPartido() throws POOBSoccerException {
        return partidoActual().obtenerEstado();
    }

    public boolean estaPartidoTerminado() throws POOBSoccerException {
        return partidoActual().estaTerminado();
    }

    public String obtenerNombreGanador() throws POOBSoccerException {
        return partidoActual().obtenerNombreGanador();
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
