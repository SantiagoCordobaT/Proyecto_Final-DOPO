package presentacion;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Particulas de la cancha (polvo, chispas, papelitos). Los demas actores piden
 * rafagas; cada particula sale hacia arriba, cae por gravedad y se encoge hasta
 * desaparecer. Usa un grupo fijo que se recicla, sin crear objetos por cuadro.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Particles implements Actor {

    private static final int CAPACIDAD = 220;
    private static final double GRAVEDAD = 900.0;
    private static final double VIDA_MINIMA = 0.35;
    private static final double VARIACION_VIDA = 0.45;
    private static final double TAMANO_MINIMO = 3.0;
    private static final double VARIACION_TAMANO = 4.0;

    private final Random aleatorio = new Random();
    private final Ellipse2D.Double punto = new Ellipse2D.Double();
    private final double[] x = new double[CAPACIDAD];
    private final double[] y = new double[CAPACIDAD];
    private final double[] velocidadX = new double[CAPACIDAD];
    private final double[] velocidadY = new double[CAPACIDAD];
    private final double[] vida = new double[CAPACIDAD];
    private final double[] duracion = new double[CAPACIDAD];
    private final double[] tamano = new double[CAPACIDAD];
    private final Color[] color = new Color[CAPACIDAD];
    private int siguiente;

    /**
     * Lanza una rafaga de particulas desde un punto.
     *
     * @param origenX   Coordenada X de salida.
     * @param origenY   Coordenada Y de salida.
     * @param tinte     Color de las particulas.
     * @param cantidad  Cantidad de particulas.
     * @param rapidez   Rapidez maxima de salida, en pixeles por segundo.
     */
    void lanzarRafaga(double origenX, double origenY, Color tinte, int cantidad, double rapidez) {
        for (int i = 0; i < cantidad; i++) {
            double angulo = Math.PI * aleatorio.nextDouble();
            double impulso = rapidez * (0.3 + 0.7 * aleatorio.nextDouble());
            x[siguiente] = origenX;
            y[siguiente] = origenY;
            velocidadX[siguiente] = Math.cos(angulo) * impulso;
            velocidadY[siguiente] = -Math.sin(angulo) * impulso;
            duracion[siguiente] = VIDA_MINIMA + VARIACION_VIDA * aleatorio.nextDouble();
            vida[siguiente] = duracion[siguiente];
            tamano[siguiente] = TAMANO_MINIMO + VARIACION_TAMANO * aleatorio.nextDouble();
            color[siguiente] = tinte;
            siguiente = (siguiente + 1) % CAPACIDAD;
        }
    }

    @Override
    public void actualizar(double segundos) {
        for (int i = 0; i < CAPACIDAD; i++) {
            if (vida[i] > 0.0) {
                vida[i] -= segundos;
                velocidadY[i] += GRAVEDAD * segundos;
                x[i] += velocidadX[i] * segundos;
                y[i] += velocidadY[i] * segundos;
            }
        }
    }

    @Override
    public void dibujar(Graphics2D g) {
        for (int i = 0; i < CAPACIDAD; i++) {
            if (vida[i] > 0.0) {
                double diametro = tamano[i] * vida[i] / duracion[i];
                punto.setFrame(x[i] - diametro / 2.0, y[i] - diametro / 2.0, diametro, diametro);
                g.setColor(color[i]);
                g.fill(punto);
            }
        }
    }
}
