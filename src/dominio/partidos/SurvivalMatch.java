package dominio.partidos;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;
import dominio.personajes.Character;

/**
 * Supervivencia (solo PvsM): serie de partidos contra la maquina jugada con balones.
 * Cada gol quita un balon al rival. Si la maquina se queda sin balones se suma una
 * victoria a la racha y entra un nuevo rival con balones llenos; la serie termina
 * unicamente cuando el jugador 1 pierde todos sus balones.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class SurvivalMatch extends MatchType {

    public static final int INITIAL_BALLS = 3;

    private int balls1;
    private int balls2;
    private int streak;
    private boolean newRivalRequested;

    /** Crea el partido de supervivencia. */
    public SurvivalMatch() {
        super("Supervivencia");
    }

    @Override
    public void validateMode(GameMode mode) throws POOBSoccerException {
        super.validateMode(mode);
        if (!mode.supportsSurvival()) {
            throw new POOBSoccerException(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM);
        }
    }

    @Override
    protected void onBegin() {
        balls1 = INITIAL_BALLS;
        balls2 = INITIAL_BALLS;
        streak = 0;
        newRivalRequested = false;
    }

    @Override
    public void tick(double deltaTimeSeconds) {
        // Sin limite de tiempo.
    }

    @Override
    public void goalScored(Character scorer, Character conceded) {
        if (isFinished()) {
            return;
        }
        if (conceded == getPlayer1()) {
            balls1--;
            if (balls1 <= 0) {
                finish(scorer);
            }
            return;
        }
        balls2--;
        if (balls2 <= 0) {
            streak++;
            balls2 = INITIAL_BALLS;
            newRivalRequested = true;
        }
    }

    @Override
    public boolean needsNewRival() {
        return newRivalRequested;
    }

    @Override
    protected void onRivalReplaced() {
        newRivalRequested = false;
    }

    @Override
    public int getStreak() {
        return streak;
    }

    @Override
    public int getBalls(Character player) {
        return player == getPlayer1() ? balls1 : balls2;
    }
}
