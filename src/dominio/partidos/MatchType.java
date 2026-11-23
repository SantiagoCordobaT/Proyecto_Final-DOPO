package dominio.partidos;

import dominio.Named;
import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;

/**
 * Clase base para las reglas de partido (por goles o supervivencia).
 * Lleva el reloj, controla si el partido termino y quien gano, y se comunica
 * con Match a traves de MatchContext. Cada subclase define como reaccionar
 * al gol y que estado mostrar en pantalla.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class MatchType implements Named {

    /** Identificador del jugador 2 (rival). */
    protected static final int JUGADOR_DOS = 2;
    /** Margen pequeno para evitar errores decimales al redondear el reloj. */
    protected static final double TOLERANCIA = 1e-9;

    private final String nombre;
    private MatchContext contexto;
    private boolean terminado;
    private int ganador; // 0 mientras no haya ganador; 1 o 2 al finalizar.
    private double segundosTranscurridos;

    /**
     * Crea un tipo de partido con el nombre mostrado en el menu.
     *
     * @param nombre Nombre visible del tipo de partido.
     */
    protected MatchType(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Indica si este tipo de partido es compatible con la modalidad elegida.
     * Por defecto acepta cualquiera; Supervivencia lo restringe a PvsM.
     *
     * @param modo Modalidad seleccionada.
     * @return true si pueden jugarse juntos.
     */
    public boolean admite(GameMode modo) {
        return true;
    }

    /**
     * Reinicia el estado y el reloj para reutilizar este objeto al empezar un
     * partido nuevo.
     *
     * @param contexto Conexion con la partida actual (goles y cambio de rival).
     */
    public void iniciar(MatchContext contexto) {
        this.contexto = contexto;
        this.terminado = false;
        this.ganador = 0;
        this.segundosTranscurridos = 0.0;
    }

    /**
     * Acumula el tiempo jugado en segundos. Cada subclase decide luego si muestra
     * este reloj en pantalla.
     *
     * @param segundos Tiempo transcurrido en el paso actual.
     */
    public void avanzar(double segundos) {
        segundosTranscurridos += segundos;
    }

    /**
     * Punto de entrada cuando alguien anota un gol. Es final para ignorar goles
     * si el partido ya termino y delegar la regla concreta a la subclase.
     *
     * @param anotador Jugador que anoto (1 o 2).
     * @throws POOBSoccerException Si falla la actualizacion del partido.
     */
    public final void golAnotado(int anotador) throws POOBSoccerException {
        if (estaTerminado()) {
            return;
        }
        alAnotarGol(anotador);
    }

    /**
     * Regla propia de cada tipo de partido al producirse un gol.
     *
     * @param anotador Jugador que anoto (1 o 2).
     * @throws POOBSoccerException Si falla la accion sobre el partido.
     */
    protected abstract void alAnotarGol(int anotador) throws POOBSoccerException;

    /**
     * Construye el texto de estado para mostrar en el marcador superior.
     *
     * @return Texto de estado del partido actual.
     */
    public abstract String obtenerTextoEstado();

    /**
     * Cierra el partido y asigna el ganador en un solo paso para no dejar estados
     * a medias.
     *
     * @param ganador Jugador ganador (1 o 2).
     */
    protected void finalizar(int ganador) {
        this.ganador = ganador;
        this.terminado = true;
    }

    /**
     * Consulta los goles de un jugador a traves del contexto del partido.
     *
     * @param jugador Jugador a consultar (1 o 2).
     * @return Goles actuales del jugador.
     * @throws POOBSoccerException Si el numero de jugador es invalido.
     */
    protected int golesDe(int jugador) throws POOBSoccerException {
        return contexto.obtenerGoles(jugador);
    }

    /**
     * Solicita cambiar al rival por el siguiente del catalogo.
     *
     * @throws POOBSoccerException Si no se puede entregar otro rival.
     */
    protected void reemplazarRival() throws POOBSoccerException {
        contexto.reemplazarRival();
    }

    /**
     * Consulta el tiempo acumulado desde que comenzo el partido.
     *
     * @return Segundos acumulados desde iniciar.
     */
    protected double obtenerSegundosTranscurridos() {
        return segundosTranscurridos;
    }

    /**
     * Convierte una cantidad de segundos enteros a formato "mm:ss".
     *
     * @param segundos Segundos totales a formatear.
     * @return Cadena con minutos y segundos en dos digitos.
     */
    protected static String formatoReloj(int segundos) {
        if (segundos < 0) {
            segundos = 0;
        }
        int minutos = segundos / 60;
        int resto = segundos % 60;
        String textoMinutos = (minutos < 10) ? "0" + minutos : String.valueOf(minutos);
        String textoSegundos = (resto < 10) ? "0" + resto : String.valueOf(resto);
        return textoMinutos + ":" + textoSegundos;
    }

    /**
     * Indica si el partido ya finalizo.
     *
     * @return true si el partido termino.
     */
    public boolean estaTerminado() {
        return terminado;
    }

    /**
     * Devuelve el jugador que gano el partido.
     *
     * @return Jugador ganador (1 o 2), o 0 si el partido sigue activo.
     */
    public int obtenerGanador() {
        return ganador;
    }

    /**
     * Devuelve el nombre visible del tipo de partido para el menu.
     *
     * @return Nombre del tipo de partido.
     */
    @Override
    public String obtenerNombre() {
        return nombre;
    }
}
