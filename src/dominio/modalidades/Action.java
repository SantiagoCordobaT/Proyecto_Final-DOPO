package dominio.modalidades;

import dominio.excepciones.POOBSoccerException;

/**
 * Accion que un jugador puede mantener presionada (contrato de entrada de los
 * controladores): moverse a la izquierda o a la derecha, saltar, patear y armar el poder.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public enum Action {
    LEFT, RIGHT, JUMP, KICK, POWER;

    /**
     * @param name Nombre de la accion (LEFT, RIGHT, JUMP, KICK o POWER).
     * @return Accion con ese nombre.
     * @throws POOBSoccerException Si el nombre es nulo o no corresponde a ninguna accion.
     */
    public static Action named(String name) throws POOBSoccerException {
        if (name == null) {
            throw new POOBSoccerException(POOBSoccerException.ACCION_INVALIDA);
        }
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new POOBSoccerException(POOBSoccerException.ACCION_INVALIDA);
        }
    }
}
