package presentacion;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Maneja pequeñas partículas visuales temporales en la cancha.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Particles implements Actor {

    private static final int CAPACIDAD = 50;
    private final double[] x = new double[CAPACIDAD];
    private final double[] y = new double[CAPACIDAD];
    private final double[] velocidadY = new double[CAPACIDAD];
    private final double[] vida = new double[CAPACIDAD];
    private final Color[] color = new Color[CAPACIDAD];
    private int siguiente;

    void lanzarRafaga(double origenX, double origenY, Color tinte, int cantidad, double rapidez) {
        for (int i = 0; i < cantidad; i++) {
            x[siguiente] = origenX;
            y[siguiente] = origenY;
            velocidadY[siguiente] = -rapidez / 2.0;
            vida[siguiente] = 0.5;
            color[siguiente] = tinte;
            siguiente = (siguiente + 1) % CAPACIDAD;
        }
    }

    @Override
    public void actualizar(double segundos) {
        for (int i = 0; i < CAPACIDAD; i++) {
            if (vida[i] > 0.0) {
                vida[i] -= segundos;
                y[i] += velocidadY[i] * segundos;
            }
        }
    }

    @Override
    public void dibujar(Graphics2D g) {
        for (int i = 0; i < CAPACIDAD; i++) {
            if (vida[i] > 0.0) {
                g.setColor(color[i]);
                g.fillRect((int) x[i], (int) y[i], 4, 4);
            }
        }
    }
}
