package presentation;

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

    private static final int CAPACITY = 220;
    private static final double GRAVITY = 900.0;
    private static final double MIN_LIFE = 0.35;
    private static final double LIFE_SPREAD = 0.45;
    private static final double MIN_SIZE = 3.0;
    private static final double SIZE_SPREAD = 4.0;

    private final Random random = new Random();
    private final Ellipse2D.Double dot = new Ellipse2D.Double();
    private final double[] x = new double[CAPACITY];
    private final double[] y = new double[CAPACITY];
    private final double[] speedX = new double[CAPACITY];
    private final double[] speedY = new double[CAPACITY];
    private final double[] life = new double[CAPACITY];
    private final double[] span = new double[CAPACITY];
    private final double[] size = new double[CAPACITY];
    private final Color[] color = new Color[CAPACITY];
    private int next;

    /**
     * Lanza una rafaga de particulas desde un punto.
     *
     * @param originX Coordenada X de salida.
     * @param originY Coordenada Y de salida.
     * @param tint Color de las particulas.
     * @param count Cantidad de particulas.
     * @param speed Rapidez maxima de salida, en pixeles por segundo.
     */
    void burst(double originX, double originY, Color tint, int count, double speed) {
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * random.nextDouble();
            double push = speed * (0.3 + 0.7 * random.nextDouble());
            x[next] = originX;
            y[next] = originY;
            speedX[next] = Math.cos(angle) * push;
            speedY[next] = -Math.sin(angle) * push;
            span[next] = MIN_LIFE + LIFE_SPREAD * random.nextDouble();
            life[next] = span[next];
            size[next] = MIN_SIZE + SIZE_SPREAD * random.nextDouble();
            color[next] = tint;
            next = (next + 1) % CAPACITY;
        }
    }

    @Override
    public void update(double seconds) {
        for (int i = 0; i < CAPACITY; i++) {
            if (life[i] > 0.0) {
                life[i] -= seconds;
                speedY[i] += GRAVITY * seconds;
                x[i] += speedX[i] * seconds;
                y[i] += speedY[i] * seconds;
            }
        }
    }

    @Override
    public void paint(Graphics2D g) {
        for (int i = 0; i < CAPACITY; i++) {
            if (life[i] > 0.0) {
                double diameter = size[i] * life[i] / span[i];
                dot.setFrame(x[i] - diameter / 2.0, y[i] - diameter / 2.0, diameter, diameter);
                g.setColor(color[i]);
                g.fill(dot);
            }
        }
    }
}
