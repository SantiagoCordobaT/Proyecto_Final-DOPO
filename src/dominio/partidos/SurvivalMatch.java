package dominio.partidos;

import dominio.modalidades.GameMode;

/**
 * Partido Supervivencia (solo Jugador vs Máquina).
 * Ambos inician con 3 balones; quien recibe gol pierde uno. Si cae el rival
 * sube la racha y entra otro oponente; si cae el jugador 1 termina la serie.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class SurvivalMatch extends MatchType {

    private static final int BALONES_INICIALES = 3;
    private int balonesJugador;
    private int balonesRival;
    private int racha;
    private boolean cambioRivalPendiente;

    SurvivalMatch() {
        super("Supervivencia");
    }

    @Override
    public boolean admite(GameMode modo) {
        return modo.admiteSupervivencia();
    }

    @Override
    public void iniciar() {
        super.iniciar();
        balonesJugador = BALONES_INICIALES;
        balonesRival = BALONES_INICIALES;
        racha = 0;
        cambioRivalPendiente = false;
    }

    @Override
    public void golAnotado(int anotador, int golesAnotador) {
        if (estaTerminado()) {
            return;
        }
        if (anotador == 1) {
            balonesRival--;
            if (balonesRival == 0) {
                racha++;
                balonesRival = BALONES_INICIALES;
                cambioRivalPendiente = true;
            }
        } else {
            balonesJugador--;
            if (balonesJugador == 0) {
                finalizar(JUGADOR_DOS);
            }
        }
    }

    @Override
    public boolean consumirCambioRival() {
        if (!cambioRivalPendiente) {
            return false;
        }
        cambioRivalPendiente = false;
        return true;
    }

    @Override
    public String obtenerTextoEstado() {
        return "Balones " + balonesJugador + "-" + balonesRival + " | Racha " + racha;
    }
}
