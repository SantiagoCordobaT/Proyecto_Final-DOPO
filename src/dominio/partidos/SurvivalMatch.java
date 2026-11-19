package dominio.partidos;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;

/**
 * Supervivencia (solo PvsM): serie de partidos contra la maquina jugada con
 * balones.
 * Cada gol quita un balon a quien lo recibe. Si la maquina se queda sin balones
 * se
 * suma una victoria a la racha y entra un nuevo rival con balones llenos; la
 * serie
 * termina unicamente cuando el jugador 1 pierde todos sus balones.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class SurvivalMatch extends MatchType {

    private static final int BALONES_INICIALES = 3;

    // Balones de cada jugador, indexados por numero de jugador - 1
    private Lives[] vidas;
    private int racha;

    /** Crea el partido de supervivencia; solo lo crea {@link MatchTypeCatalog}. */
    SurvivalMatch() {
        super("Supervivencia");
    }

    /** @return true solo si la modalidad admite Supervivencia. */
    @Override
    public boolean admite(GameMode modo) {
        return modo.admiteSupervivencia();
    }

    @Override
    public void iniciar(MatchContext contexto) {
        super.iniciar(contexto);
        vidas = new Lives[] { new PlayerLives(), new RivalLives() };
        racha = 0;
    }

    /**
     * Quien recibe el gol es el otro jugador: su indice (numero - 1) es
     * {@code JUGADOR_DOS - anotador}.
     */
    @Override
    protected void alAnotarGol(int anotador) throws POOBSoccerException {
        vidas[JUGADOR_DOS - anotador].perder();
    }

    /**
     * @return Balones de cada jugador y racha, por ejemplo "Balones 3-2 | Racha 1".
     */
    @Override
    public String obtenerTextoEstado() {
        return String.format("Balones %d-%d | Racha %d", vidas[0].balones, vidas[1].balones, racha);
    }

    /**
     * Balones de un jugador (metodo plantilla): perder el ultimo balon dispara
     * la reaccion propia de cada lado.
     */
    private abstract class Lives {

        int balones = BALONES_INICIALES;

        final void perder() throws POOBSoccerException {
            balones--;
            if (balones == 0) {
                agotado();
            }
        }

        /** Reaccion cuando el jugador se queda sin balones. */
        abstract void agotado() throws POOBSoccerException;
    }

    /** Balones del jugador 1: al agotarse, la maquina gana la serie. */
    private final class PlayerLives extends Lives {

        @Override
        void agotado() {
            finalizar(JUGADOR_DOS);
        }
    }

    /**
     * Balones de la maquina: al agotarse sube la racha y entra un nuevo rival con
     * balones llenos.
     */
    private final class RivalLives extends Lives {

        @Override
        void agotado() throws POOBSoccerException {
            racha++;
            balones = BALONES_INICIALES;
            reemplazarRival();
        }
    }
}
