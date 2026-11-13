package dominio;

import dominio.personajes.Character;

/**
 * Lado de la cancha de cada jugador: posicion inicial y sentido de ataque.
 * Reemplaza los enteros sueltos (200/700 y +1/-1) por un tipo con nombre.
 * Las posiciones 200 y 700 son las del README; el jugador del lado izquierdo
 * es el 1 (anota en el arco derecho) y el del lado derecho es el 2.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
enum Side {
    LEFT(200.0, 1),
    RIGHT(700.0, -1);

    private final double homeX;
    private final int facing;

    Side(double homeX, int facing) {
        this.homeX = homeX;
        this.facing = facing;
    }

    /**
     * Ubica al personaje en este lado para empezar un partido.
     *
     * @param character Personaje que entra a la cancha.
     */
    void place(Character character) {
        character.enterAt(homeX, facing);
    }
}
