package presentation;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Tecla de un jugador (Command): la accion del juego que la tecla representa. Al
 * presionarla el jugador empieza a mantener la accion y al soltarla deja de hacerlo;
 * la fachada decide si el controlador del jugador la usa (la maquina la ignora).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerKey {

    private final int player;
    private final String action;

    /**
     * @param player Jugador (1 o 2) al que pertenece la tecla.
     * @param action Accion del jugador: LEFT, RIGHT, JUMP, KICK o POWER.
     */
    PlayerKey(int player, String action) {
        this.player = player;
        this.action = action;
    }

    /**
     * La tecla se presiona: el jugador empieza a mantener la accion.
     *
     * @param game Fachada del juego.
     * @throws POOBSoccerException Si no hay partido.
     */
    void press(HeadSoccer game) throws POOBSoccerException {
        game.pressAction(player, action);
    }

    /**
     * La tecla se suelta: el jugador deja de mantener la accion.
     *
     * @param game Fachada del juego.
     * @throws POOBSoccerException Si no hay partido.
     */
    void release(HeadSoccer game) throws POOBSoccerException {
        game.releaseAction(player, action);
    }
}
