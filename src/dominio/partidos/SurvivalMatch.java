package dominio.partidos;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;

/**
 * Supervivencia (solo PvsM): serie de partidos contra la maquina jugada con balones.
 * Cada gol quita un balon a quien lo recibe. Si la maquina se queda sin balones se
 * suma una victoria a la racha y entra un nuevo rival con balones llenos; la serie
 * termina unicamente cuando el jugador 1 pierde todos sus balones.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class SurvivalMatch extends MatchType {

    private static final int INITIAL_BALLS = 3;

    // Balones de cada jugador, indexados por numero de jugador - 1
    private Lives[] lives;
    private int streak;

    /** Crea el partido de supervivencia; solo lo crea {@link MatchTypeCatalog}. */
    SurvivalMatch() {
        super("Supervivencia");
    }

    /** @return true solo si la modalidad admite Supervivencia. */
    @Override
    public boolean supports(GameMode mode) {
        return mode.supportsSurvival();
    }

    @Override
    public void begin(MatchContext context) {
        super.begin(context);
        lives = new Lives[] { new PlayerLives(), new RivalLives() };
        streak = 0;
    }

    /** Quien recibe el gol es el otro jugador: su indice (numero - 1) es {@code PLAYER_TWO - scorer}. */
    @Override
    protected void onGoal(int scorer) throws POOBSoccerException {
        lives[PLAYER_TWO - scorer].lose();
    }

    /** @return Balones de cada jugador y racha, por ejemplo "Balones 3-2 | Racha 1". */
    @Override
    public String getStatusText() {
        return String.format("Balones %d-%d | Racha %d", lives[0].balls, lives[1].balls, streak);
    }

    /**
     * Balones de un jugador (metodo plantilla): perder el ultimo balon dispara
     * la reaccion propia de cada lado.
     */
    private abstract class Lives {

        int balls = INITIAL_BALLS;

        final void lose() throws POOBSoccerException {
            balls--;
            if (balls == 0) {
                exhausted();
            }
        }

        /** Reaccion cuando el jugador se queda sin balones. */
        abstract void exhausted() throws POOBSoccerException;
    }

    /** Balones del jugador 1: al agotarse, la maquina gana la serie. */
    private final class PlayerLives extends Lives {

        @Override
        void exhausted() {
            finish(PLAYER_TWO);
        }
    }

    /** Balones de la maquina: al agotarse sube la racha y entra un nuevo rival con balones llenos. */
    private final class RivalLives extends Lives {

        @Override
        void exhausted() throws POOBSoccerException {
            streak++;
            balls = INITIAL_BALLS;
            replaceRival();
        }
    }
}
