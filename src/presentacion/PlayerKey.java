package presentacion;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Tecla de un jugador (Command): la accion del juego que la tecla representa.
 * Al
 * presionarla el jugador empieza a mantener la accion y al soltarla deja de
 * hacerlo;
 * la fachada decide si el controlador del jugador la usa (la maquina la
 * ignora).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerKey {

    private final int jugador;
    private final String accion;

    /**
     * @param jugador Jugador (1 o 2) al que pertenece la tecla.
     * @param accion  Accion del jugador: LEFT, RIGHT, JUMP, KICK o POWER.
     */
    PlayerKey(int jugador, String accion) {
        this.jugador = jugador;
        this.accion = accion;
    }

    /**
     * La tecla se presiona: el jugador empieza a mantener la accion.
     *
     * @param juego Fachada del juego.
     * @throws POOBSoccerException Si no hay partido.
     */
    void presionar(HeadSoccer juego) throws POOBSoccerException {
        juego.presionarAccion(jugador, accion);
    }

    /**
     * La tecla se suelta: el jugador deja de mantener la accion.
     *
     * @param juego Fachada del juego.
     * @throws POOBSoccerException Si no hay partido.
     */
    void soltar(HeadSoccer juego) throws POOBSoccerException {
        juego.soltarAccion(jugador, accion);
    }
}
