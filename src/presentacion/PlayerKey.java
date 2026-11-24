package presentacion;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Asocia una tecla con el jugador y la acción que debe enviar al dominio.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerKey {

    private final int jugador;
    private final String accion;

    PlayerKey(int jugador, String accion) {
        this.jugador = jugador;
        this.accion = accion;
    }

    void presionar(HeadSoccer juego) throws POOBSoccerException {
        juego.presionarAccion(jugador, accion);
    }

    void soltar(HeadSoccer juego) throws POOBSoccerException {
        juego.soltarAccion(jugador, accion);
    }
}
