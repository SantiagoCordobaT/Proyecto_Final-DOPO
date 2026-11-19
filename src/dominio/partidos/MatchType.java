package dominio.partidos;

import dominio.Named;
import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;

/**
 * Tipo de partido (Strategy): decide cuando termina el juego, quien gana y que
 * estado muestra. Lee el partido solo a traves de {@link MatchContext}.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class MatchType implements Named {

    protected static final int JUGADOR_UNO = 1;
    protected static final int JUGADOR_DOS = 2;
    protected static final double TOLERANCIA = 1e-9;

    private static final int SEGUNDOS_POR_MINUTO = 60;
    private static final int SIN_GANADOR = 0;

    private final String nombre;
    private MatchContext contexto;
    private boolean terminado;
    private int ganador;
    private double segundosTranscurridos;

    /** @param nombre Nombre del tipo de partido. */
    protected MatchType(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Por defecto todo tipo de partido admite cualquier modalidad; Supervivencia lo
     * redefine.
     *
     * @param modo Modalidad elegida.
     * @return true si el tipo de partido se puede jugar en esa modalidad.
     */
    public boolean admite(GameMode modo) {
        return true;
    }

    /**
     * Inicia (o reinicia) el tipo de partido para un partido nuevo: sin ganador y
     * con el reloj en cero.
     *
     * @param contexto Partido en curso.
     */
    public void iniciar(MatchContext contexto) {
        this.contexto = contexto;
        this.terminado = false;
        this.ganador = SIN_GANADOR;
        this.segundosTranscurridos = 0.0;
    }

    /**
     * Avanza el reloj del partido.
     *
     * @param segundos Segundos transcurridos.
     * @throws POOBSoccerException Si el marcador no se pudiera consultar.
     */
    public void avanzar(double segundos) throws POOBSoccerException {
        segundosTranscurridos += segundos;
    }

    /**
     * Metodo plantilla: se llama despues de sumar un gol y solo lo procesa si el
     * partido sigue en juego.
     *
     * @param anotador Jugador (1 o 2) que anota (ya tiene el gol sumado).
     * @throws POOBSoccerException Si el partido no pudiera reaccionar al gol.
     */
    public final void golAnotado(int anotador) throws POOBSoccerException {
        if (!estaTerminado()) {
            alAnotarGol(anotador);
        }
    }

    /**
     * Paso variable de {@link #golAnotado}: reaccion propia de cada tipo de partido
     * al gol.
     *
     * @param anotador Jugador (1 o 2) que anota.
     * @throws POOBSoccerException Si el partido no pudiera reaccionar al gol.
     */
    protected abstract void alAnotarGol(int anotador) throws POOBSoccerException;

    /** @return Texto de estado para el marcador (reloj, meta, balones...). */
    public abstract String obtenerTextoEstado();

    /**
     * Cierra el partido.
     *
     * @param ganador Jugador (1 o 2) ganador.
     */
    protected final void finalizar(int ganador) {
        this.ganador = ganador;
        this.terminado = true;
    }

    /**
     * @param jugador Jugador (1 o 2).
     * @return Goles del jugador.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    protected final int golesDe(int jugador) throws POOBSoccerException {
        return contexto.obtenerGoles(jugador);
    }

    /**
     * Pide al partido el siguiente rival.
     *
     * @throws POOBSoccerException Si el catalogo no pudiera entregar el rival.
     */
    protected final void reemplazarRival() throws POOBSoccerException {
        contexto.reemplazarRival();
    }

    /** @return Segundos jugados desde {@link #iniciar}. */
    protected final double obtenerSegundosTranscurridos() {
        return segundosTranscurridos;
    }

    /**
     * Cada tipo de partido redondea sus segundos antes de mostrarlos: la cuenta
     * regresiva hacia arriba y el tiempo jugado hacia abajo.
     *
     * @param segundos Segundos enteros (no negativos).
     * @return Tiempo con formato "mm:ss".
     */
    protected static String formatoReloj(int segundos) {
        return String.format("%02d:%02d", segundos / SEGUNDOS_POR_MINUTO, segundos % SEGUNDOS_POR_MINUTO);
    }

    /** @return true si el partido termino. */
    public final boolean estaTerminado() {
        return terminado;
    }

    /**
     * @return Jugador (1 o 2) ganador; solo tiene sentido cuando
     *         {@link #estaTerminado()}.
     */
    public final int obtenerGanador() {
        return ganador;
    }

    /** @return Nombre del tipo de partido. */
    @Override
    public final String obtenerNombre() {
        return nombre;
    }
}
