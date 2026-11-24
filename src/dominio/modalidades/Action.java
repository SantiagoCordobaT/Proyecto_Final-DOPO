package dominio.modalidades;

import dominio.excepciones.POOBSoccerException;

/**
 * Acciones disponibles para controlar a un jugador.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public enum Action {
    LEFT, RIGHT, JUMP, KICK, POWER;

    public static Action porNombre(String nombre) throws POOBSoccerException {
        if (nombre != null) {
            for (Action accion : values()) {
                if (accion.name().equals(nombre)) {
                    return accion;
                }
            }
        }
        throw new POOBSoccerException(POOBSoccerException.ACCION_INVALIDA);
    }
}
