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

    protected static final int PLAYER_ONE = 1;
    protected static final int PLAYER_TWO = 2;
    protected static final double TOLERANCE = 1e-9;

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int NO_WINNER = 0;

    private final String name;
    private MatchContext context;
    private boolean finished;
    private int winner;
    private double elapsedSeconds;

    /** @param name Nombre del tipo de partido. */
    protected MatchType(String name) {
        this.name = name;
    }

    /**
     * Por defecto todo tipo de partido admite cualquier modalidad; Supervivencia lo redefine.
     *
     * @param mode Modalidad elegida.
     * @return true si el tipo de partido se puede jugar en esa modalidad.
     */
    public boolean supports(GameMode mode) {
        return true;
    }

    /**
     * Inicia (o reinicia) el tipo de partido para un partido nuevo: sin ganador y con el reloj en cero.
     *
     * @param context Partido en curso.
     */
    public void begin(MatchContext context) {
        this.context = context;
        this.finished = false;
        this.winner = NO_WINNER;
        this.elapsedSeconds = 0.0;
    }

    /**
     * Avanza el reloj del partido.
     *
     * @param deltaTimeSeconds Segundos transcurridos.
     * @throws POOBSoccerException Si el marcador no se pudiera consultar.
     */
    public void tick(double deltaTimeSeconds) throws POOBSoccerException {
        elapsedSeconds += deltaTimeSeconds;
    }

    /**
     * Metodo plantilla: se llama despues de sumar un gol y solo lo procesa si el partido sigue en juego.
     *
     * @param scorer Jugador (1 o 2) que anota (ya tiene el gol sumado).
     * @throws POOBSoccerException Si el partido no pudiera reaccionar al gol.
     */
    public final void goalScored(int scorer) throws POOBSoccerException {
        if (!isFinished()) {
            onGoal(scorer);
        }
    }

    /**
     * Paso variable de {@link #goalScored}: reaccion propia de cada tipo de partido al gol.
     *
     * @param scorer Jugador (1 o 2) que anota.
     * @throws POOBSoccerException Si el partido no pudiera reaccionar al gol.
     */
    protected abstract void onGoal(int scorer) throws POOBSoccerException;

    /** @return Texto de estado para el marcador (reloj, meta, balones...). */
    public abstract String getStatusText();

    /**
     * Cierra el partido.
     *
     * @param winner Jugador (1 o 2) ganador.
     */
    protected final void finish(int winner) {
        this.winner = winner;
        this.finished = true;
    }

    /**
     * @param player Jugador (1 o 2).
     * @return Goles del jugador.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    protected final int goalsOf(int player) throws POOBSoccerException {
        return context.getGoals(player);
    }

    /**
     * Pide al partido el siguiente rival.
     *
     * @throws POOBSoccerException Si el catalogo no pudiera entregar el rival.
     */
    protected final void replaceRival() throws POOBSoccerException {
        context.replaceRival();
    }

    /** @return Segundos jugados desde {@link #begin}. */
    protected final double getElapsedSeconds() {
        return elapsedSeconds;
    }

    /**
     * Cada tipo de partido redondea sus segundos antes de mostrarlos: la cuenta
     * regresiva hacia arriba y el tiempo jugado hacia abajo.
     *
     * @param seconds Segundos enteros (no negativos).
     * @return Tiempo con formato "mm:ss".
     */
    protected static String clock(int seconds) {
        return String.format("%02d:%02d", seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE);
    }

    /** @return true si el partido termino. */
    public final boolean isFinished() {
        return finished;
    }

    /** @return Jugador (1 o 2) ganador; solo tiene sentido cuando {@link #isFinished()}. */
    public final int getWinner() {
        return winner;
    }

    /** @return Nombre del tipo de partido. */
    @Override
    public final String getName() {
        return name;
    }
}
