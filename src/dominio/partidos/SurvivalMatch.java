package dominio.partidos;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;

/**
 * Tipo de partido Supervivencia (exclusivo de Jugador vs Maquina).
 * Ambos empiezan con 3 balones (vidas). Quien recibe un gol pierde un balon:
 * si la maquina llega a 0 sube la racha y entra un nuevo rival con 3 balones;
 * si el jugador 1 llega a 0 termina la serie.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class SurvivalMatch extends MatchType {

    private static final int BALONES_INICIALES = 3;
    private int balonesJugador;
    private int balonesRival;
    private int racha;

    /** Crea el tipo de partido Supervivencia. */
    SurvivalMatch() {
        super("Supervivencia");
    }

    // Solo se permite en modalidades que admiten supervivencia (PvsM).
    @Override
    public boolean admite(GameMode modo) {
        return modo.admiteSupervivencia();
    }

    @Override
    public void iniciar(MatchContext contexto) {
        super.iniciar(contexto);
        balonesJugador = BALONES_INICIALES;
        balonesRival = BALONES_INICIALES;
        racha = 0;
    }

    /**
     * Descuenta un balon al equipo que recibio el gol.
     * Si cae el rival, suma racha y pide un nuevo oponente; si cae el jugador 1,
     * termina el partido dando la victoria al jugador 2.
     */
    @Override
    protected void alAnotarGol(int anotador) throws POOBSoccerException {
        if (anotador == 1) {
            balonesRival--;
            if (balonesRival == 0) {
                racha++;
                balonesRival = BALONES_INICIALES;
                reemplazarRival();
            }
        } else {
            balonesJugador--;
            if (balonesJugador == 0) {
                finalizar(JUGADOR_DOS);
            }
        }
    }

    // Formato mostrado en el marcador superior durante la serie.
    @Override
    public String obtenerTextoEstado() {
        return "Balones " + balonesJugador + "-" + balonesRival + " | Racha " + racha;
    }
}
