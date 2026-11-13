package dominio.partidos;

import dominio.Named;
import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;
import dominio.personajes.Character;

/**
 * Tipo de partido (Strategy): decide cuando termina el juego y quien gana.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class MatchType implements Named {

    private final String name;
    private Character player1;
    private Character player2;
    private Character winner;
    private boolean finished;

    /** @param name Nombre del tipo de partido. */
    protected MatchType(String name) {
        this.name = name;
    }

    /**
     * Verifica que el tipo de partido sea compatible con la modalidad.
     * Por defecto lo es; Supervivencia lo redefine.
     *
     * @param mode Modalidad elegida.
     * @throws POOBSoccerException Si no son compatibles.
     */
    public void validateMode(GameMode mode) throws POOBSoccerException {
        if (mode == null) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
    }

    /**
     * Inicia (o reinicia) el partido con los jugadores indicados.
     *
     * @param mode Modalidad.
     * @param player1 Jugador 1.
     * @param player2 Jugador 2.
     * @throws POOBSoccerException Si falta algun jugador o la modalidad no es compatible.
     */
    public void begin(GameMode mode, Character player1, Character player2) throws POOBSoccerException {
        validateMode(mode);
        if (player1 == null || player2 == null) {
            throw new POOBSoccerException(POOBSoccerException.JUGADORES_INCOMPLETOS);
        }
        this.player1 = player1;
        this.player2 = player2;
        this.winner = null;
        this.finished = false;
        onBegin();
    }

    /** Se llama cuando entra un nuevo rival; por defecto no hace nada. */
    protected void onRivalReplaced() {
        // Solo Supervivencia necesita reaccionar.
    }

    /** Reinicia el estado propio del tipo de partido. */
    protected abstract void onBegin();

    /**
     * Avanza el reloj del partido.
     *
     * @param deltaTimeSeconds Segundos transcurridos.
     */
    public abstract void tick(double deltaTimeSeconds);

    /**
     * Se llama despues de registrar un gol.
     *
     * @param scorer Jugador que anota (ya tiene el gol sumado).
     * @param conceded Jugador que recibe el gol.
     */
    public abstract void goalScored(Character scorer, Character conceded);

    /** @return Segundos restantes (0 si no hay limite de tiempo). */
    public double getRemainingSeconds() {
        return 0.0;
    }

    /** @return true si esta en Gol de Oro. */
    public boolean isGoldenGoal() {
        return false;
    }

    /**
     * @param player Jugador consultado.
     * @return Balones (vidas) restantes; 0 si el tipo de partido no los usa.
     */
    public int getBalls(Character player) {
        return 0;
    }

    /**
     * @return true si hay que cargar un nuevo rival de la maquina (solo Supervivencia).
     */
    public boolean needsNewRival() {
        return false;
    }

    /**
     * Reemplaza al jugador 2 (rival de la maquina) por uno nuevo.
     *
     * @param newRival Nuevo rival.
     * @throws POOBSoccerException Si el rival es nulo.
     */
    public void replaceRival(Character newRival) throws POOBSoccerException {
        if (newRival == null) {
            throw new POOBSoccerException(POOBSoccerException.JUGADORES_INCOMPLETOS);
        }
        this.player2 = newRival;
        onRivalReplaced();
    }

    /** @return Victorias seguidas en la serie de Supervivencia (0 en los demas tipos). */
    public int getStreak() {
        return 0;
    }

    /**
     * Cierra el partido.
     *
     * @param winner Ganador.
     */
    protected void finish(Character winner) {
        this.winner = winner;
        this.finished = true;
    }

    /**
     * @return Jugador 1.
     */
    protected Character getPlayer1() {
        return player1;
    }

    /**
     * @return Jugador 2 (rival actual).
     */
    protected Character getPlayer2() {
        return player2;
    }

    /** @return Ganador (null mientras no termine). */
    public Character getWinner() {
        return winner;
    }

    /** @return true si el partido termino. */
    public boolean isFinished() {
        return finished;
    }

    /** @return Nombre del tipo de partido. */
    @Override
    public String getName() {
        return name;
    }
}
