package dominio.modalidades;

import dominio.Named;

/**
 * Modalidad de juego. Define que controlador usa cada jugador y si admite
 * el tipo de partido Supervivencia (LSP/Strategy).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class GameMode implements Named {

    private final String name;

    /** @param name Nombre de la modalidad. */
    protected GameMode(String name) {
        this.name = name;
    }

    /** @return Controlador del jugador 1. */
    public abstract Controller createPlayer1Controller();

    /** @return Controlador del jugador 2. */
    public abstract Controller createPlayer2Controller();

    /** @return true si la modalidad permite el partido de Supervivencia. */
    public boolean supportsSurvival() {
        return false;
    }

    /** @return Nombre de la modalidad. */
    @Override
    public String getName() {
        return name;
    }
}
