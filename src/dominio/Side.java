package dominio;

import dominio.personajes.Character;

/**
 * Lado inicial de cada jugador en la cancha: coordenada X de arranque y
 * orientación hacia el arco rival (1 derecha, -1 izquierda).
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

    void ubicar(Character personaje) {
        personaje.entrarEn(xInicial, orientacion);
    }
}
