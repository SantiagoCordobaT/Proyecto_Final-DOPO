package dominio.partidos;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Modo Supervivencia (solo Jugador vs Máquina). Cuando la máquina se queda sin
 * balones, este tipo de partido entrega directamente el siguiente rival.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class SurvivalMatch extends MatchType {

    private static final int BALONES_INICIALES = 3;
    private int balonesJugador;
    private int balonesRival;
    private int racha;

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
    }

    @Override
    public Character registrarGol(int anotador, int golesAnotador, Character rivalActual,
            CharacterCatalog catalogo) throws POOBSoccerException {
        if (estaTerminado()) {
            return null;
        }
        if (anotador == 1) {
            balonesRival--;
            if (balonesRival == 0) {
                racha++;
                balonesRival = BALONES_INICIALES;
                return catalogo.siguienteDespuesDe(rivalActual);
            }
        } else {
            balonesJugador--;
            if (balonesJugador == 0) {
                finalizar(JUGADOR_DOS);
            }
        }
        return null;
    }

    @Override
    public String obtenerTextoEstado() {
        return "Balones " + balonesJugador + "-" + balonesRival + " | Racha " + racha;
    }
}
