package dominio;

import dominio.personajes.Character;

/**
 * Lado de la cancha de cada jugador: posicion inicial y sentido de vista.
 * Reemplaza los enteros sueltos por un tipo con nombre.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
enum Side {
    LEFT(200.0, 1),
    RIGHT(700.0, -1);

    private final double xInicial;
    private final int orientacion;

    Side(double xInicial, int orientacion) {
        this.xInicial = xInicial;
        this.orientacion = orientacion;
    }

    /**
     * Ubica al personaje en este lado para empezar un partido.
     *
     * @param personaje Personaje que entra a la cancha.
     */
    void place(Character personaje) {
        personaje.enterAt(xInicial, orientacion);
    }
}
