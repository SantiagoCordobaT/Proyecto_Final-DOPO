package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Dibujo de los efectos de estado sobre un personaje. Cada efecto conocido tiene
 * su propia animacion en una tabla por nombre; un efecto sin animacion se muestra
 * con su nombre, de modo que un efecto nuevo del dominio siempre se ve.
 *
 * Todo se pinta en las coordenadas nominales del personaje: origen en los pies,
 * 60 de ancho y 90 de alto, con la cabeza arriba (y negativa).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class EffectOverlay {

    private static final Color ICE = new Color(170, 225, 255, 125);
    private static final Color ICE_EDGE = new Color(225, 246, 255);
    private static final Color STAR = new Color(255, 226, 70);
    private static final Color DIZZY = new Color(214, 140, 255);
    private static final Color SHRINK = new Color(110, 235, 255);
    private static final Color TAG = new Color(0, 0, 0, 170);
    private static final BasicStroke EDGE = new BasicStroke(2f);
    private static final RoundRectangle2D ICE_BLOCK = new RoundRectangle2D.Double(-37.0, -97.0, 74.0, 99.0, 12.0, 12.0);
    private static final Line2D[] ICE_SHINE = { new Line2D.Double(-26.0, -70.0, -8.0, -88.0),
        new Line2D.Double(-28.0, -48.0, -2.0, -74.0), new Line2D.Double(10.0, -12.0, 28.0, -30.0) };
    private static final Polygon STAR_SHAPE = new Polygon(new int[] { 0, 2, 7, 3, 4, 0, -4, -3, -7, -2 },
            new int[] { -7, -2, -2, 1, 6, 3, 6, 1, -2, -2 }, 10);
    private static final Polygon ARROW = new Polygon(new int[] { -6, 6, 0 }, new int[] { -8, -8, 2 }, 3);
    private static final String QUESTION = "?";
    private static final int ORBITERS = 3;
    private static final double ORBIT_Y = -100.0;
    private static final double ORBIT_WIDTH = 24.0;
    private static final double ORBIT_HEIGHT = 6.0;
    private static final double ORBIT_SPEED = 5.0;
    private static final double TAG_Y = -112.0;
    private static final double TAG_PADDING = 5.0;

    private final Map<String, Drawing> drawings = new HashMap<String, Drawing>();

    /** Registra la animacion de cada efecto conocido por su nombre en el dominio. */
    EffectOverlay() {
        drawings.put("Congelado", EffectOverlay::paintIce);
        drawings.put("Aturdido", EffectOverlay::paintStars);
        drawings.put("Confundido", EffectOverlay::paintQuestions);
        drawings.put("Reducido", EffectOverlay::paintArrows);
    }

    /**
     * Pinta los efectos activos de un personaje.
     *
     * @param g Lienzo ya ubicado en las coordenadas nominales del personaje.
     * @param effects Nombres de los efectos activos.
     * @param time Reloj de animacion del personaje, en segundos.
     */
    void paint(Graphics2D g, String[] effects, double time) {
        for (String effect : effects) {
            drawings.getOrDefault(effect, EffectOverlay::paintName).paint(g, effect, time);
        }
    }

    /** Bloque de hielo translucido que encierra al personaje. */
    private static void paintIce(Graphics2D g, String name, double time) {
        g.setColor(ICE);
        g.fill(ICE_BLOCK);
        g.setColor(ICE_EDGE);
        g.setStroke(EDGE);
        g.draw(ICE_BLOCK);
        for (Line2D shine : ICE_SHINE) {
            g.draw(shine);
        }
    }

    /** Estrellas que giran sobre la cabeza. */
    private static void paintStars(Graphics2D g, String name, double time) {
        g.setColor(STAR);
        for (int i = 0; i < ORBITERS; i++) {
            AffineTransform saved = g.getTransform();
            g.translate(orbitX(time, i), orbitY(time, i));
            g.fill(STAR_SHAPE);
            g.setTransform(saved);
        }
    }

    /** Signos de pregunta que giran sobre la cabeza. */
    private static void paintQuestions(Graphics2D g, String name, double time) {
        g.setColor(DIZZY);
        g.setFont(Resources.TEXT_FONT);
        for (int i = 0; i < ORBITERS; i++) {
            g.drawString(QUESTION, (float) orbitX(time, i), (float) orbitY(time, i));
        }
    }

    /** Flechas que aprietan al personaje desde arriba. */
    private static void paintArrows(Graphics2D g, String name, double time) {
        g.setColor(SHRINK);
        for (int i = -1; i <= 1; i += 2) {
            AffineTransform saved = g.getTransform();
            g.translate(i * ORBIT_WIDTH, ORBIT_Y + ORBIT_HEIGHT * Math.sin(time * ORBIT_SPEED));
            g.fill(ARROW);
            g.setTransform(saved);
        }
    }

    /** Respaldo: etiqueta con el nombre del efecto sobre la cabeza. */
    private static void paintName(Graphics2D g, String name, double time) {
        g.setFont(Resources.TEXT_FONT);
        FontMetrics metrics = g.getFontMetrics();
        double width = metrics.stringWidth(name) + 2.0 * TAG_PADDING;
        g.setColor(TAG);
        g.fill(new RoundRectangle2D.Double(-width / 2.0, TAG_Y - metrics.getAscent(), width, metrics.getHeight(),
                TAG_PADDING, TAG_PADDING));
        g.setColor(STAR);
        g.drawString(name, (float) (-width / 2.0 + TAG_PADDING), (float) TAG_Y);
    }

    private static double orbitX(double time, int index) {
        return ORBIT_WIDTH * Math.cos(time * ORBIT_SPEED + index * 2.0 * Math.PI / ORBITERS);
    }

    private static double orbitY(double time, int index) {
        return ORBIT_Y + ORBIT_HEIGHT * Math.sin(time * ORBIT_SPEED + index * 2.0 * Math.PI / ORBITERS);
    }

    /** Animacion de un efecto de estado. */
    private interface Drawing {
        void paint(Graphics2D g, String name, double time);
    }
}
