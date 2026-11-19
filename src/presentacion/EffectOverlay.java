package presentacion;

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
 * Dibujo de los efectos de estado sobre un personaje. Cada efecto conocido
 * tiene
 * su propia animacion en una tabla por nombre; un efecto sin animacion se
 * muestra
 * con su nombre, de modo que un efecto nuevo del dominio siempre se ve.
 *
 * Todo se pinta en las coordenadas nominales del personaje: origen en los pies,
 * 60 de ancho y 90 de alto, con la cabeza arriba (y negativa).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class EffectOverlay {

    private static final Color HIELO = new Color(170, 225, 255, 125);
    private static final Color BORDE_HIELO = new Color(225, 246, 255);
    private static final Color ESTRELLA = new Color(255, 226, 70);
    private static final Color MAREO = new Color(214, 140, 255);
    private static final Color REDUCCION = new Color(110, 235, 255);
    private static final Color ETIQUETA = new Color(0, 0, 0, 170);
    private static final BasicStroke BORDE = new BasicStroke(2f);
    private static final RoundRectangle2D BLOQUE_HIELO = new RoundRectangle2D.Double(-37.0, -97.0, 74.0, 99.0, 12.0,
            12.0);
    private static final Line2D[] BRILLOS_HIELO = { new Line2D.Double(-26.0, -70.0, -8.0, -88.0),
            new Line2D.Double(-28.0, -48.0, -2.0, -74.0), new Line2D.Double(10.0, -12.0, 28.0, -30.0) };
    private static final Polygon FORMA_ESTRELLA = new Polygon(new int[] { 0, 2, 7, 3, 4, 0, -4, -3, -7, -2 },
            new int[] { -7, -2, -2, 1, 6, 3, 6, 1, -2, -2 }, 10);
    private static final Polygon FLECHA = new Polygon(new int[] { -6, 6, 0 }, new int[] { -8, -8, 2 }, 3);
    private static final String INTERROGACION = "?";
    private static final int ORBITANTES = 3;
    private static final double ORBITA_Y = -100.0;
    private static final double ANCHO_ORBITA = 24.0;
    private static final double ALTO_ORBITA = 6.0;
    private static final double VELOCIDAD_ORBITA = 5.0;
    private static final double ETIQUETA_Y = -112.0;
    private static final double MARGEN_ETIQUETA = 5.0;

    private final Map<String, Drawing> dibujos = new HashMap<String, Drawing>();

    /**
     * Registra la animacion de cada efecto conocido por su nombre en el dominio.
     */
    EffectOverlay() {
        dibujos.put("Congelado", EffectOverlay::paintIce);
        dibujos.put("Aturdido", EffectOverlay::paintStars);
        dibujos.put("Confundido", EffectOverlay::paintQuestions);
        dibujos.put("Reducido", EffectOverlay::paintArrows);
    }

    /**
     * Pinta los efectos activos de un personaje.
     *
     * @param g       Lienzo ya ubicado en las coordenadas nominales del personaje.
     * @param efectos Nombres de los efectos activos.
     * @param tiempo  Reloj de animacion del personaje, en segundos.
     */
    void paint(Graphics2D g, String[] efectos, double tiempo) {
        for (String efecto : efectos) {
            dibujos.getOrDefault(efecto, EffectOverlay::paintName).paint(g, efecto, tiempo);
        }
    }

    /** Bloque de hielo translucido que encierra al personaje. */
    private static void paintIce(Graphics2D g, String nombre, double tiempo) {
        g.setColor(HIELO);
        g.fill(BLOQUE_HIELO);
        g.setColor(BORDE_HIELO);
        g.setStroke(BORDE);
        g.draw(BLOQUE_HIELO);
        for (Line2D brillo : BRILLOS_HIELO) {
            g.draw(brillo);
        }
    }

    /** Estrellas que giran sobre la cabeza. */
    private static void paintStars(Graphics2D g, String nombre, double tiempo) {
        g.setColor(ESTRELLA);
        for (int i = 0; i < ORBITANTES; i++) {
            AffineTransform guardado = g.getTransform();
            g.translate(orbitX(tiempo, i), orbitY(tiempo, i));
            g.fill(FORMA_ESTRELLA);
            g.setTransform(guardado);
        }
    }

    /** Signos de pregunta que giran sobre la cabeza. */
    private static void paintQuestions(Graphics2D g, String nombre, double tiempo) {
        g.setColor(MAREO);
        g.setFont(Resources.FUENTE_TEXTO);
        for (int i = 0; i < ORBITANTES; i++) {
            g.drawString(INTERROGACION, (float) orbitX(tiempo, i), (float) orbitY(tiempo, i));
        }
    }

    /** Flechas que aprietan al personaje desde arriba. */
    private static void paintArrows(Graphics2D g, String nombre, double tiempo) {
        g.setColor(REDUCCION);
        for (int i = -1; i <= 1; i += 2) {
            AffineTransform guardado = g.getTransform();
            g.translate(i * ANCHO_ORBITA, ORBITA_Y + ALTO_ORBITA * Math.sin(tiempo * VELOCIDAD_ORBITA));
            g.fill(FLECHA);
            g.setTransform(guardado);
        }
    }

    /** Respaldo: etiqueta con el nombre del efecto sobre la cabeza. */
    private static void paintName(Graphics2D g, String nombre, double tiempo) {
        g.setFont(Resources.FUENTE_TEXTO);
        FontMetrics metricas = g.getFontMetrics();
        double ancho = metricas.stringWidth(nombre) + 2.0 * MARGEN_ETIQUETA;
        g.setColor(ETIQUETA);
        g.fill(new RoundRectangle2D.Double(-ancho / 2.0, ETIQUETA_Y - metricas.getAscent(), ancho,
                metricas.getHeight(), MARGEN_ETIQUETA, MARGEN_ETIQUETA));
        g.setColor(ESTRELLA);
        g.drawString(nombre, (float) (-ancho / 2.0 + MARGEN_ETIQUETA), (float) ETIQUETA_Y);
    }

    private static double orbitX(double tiempo, int indice) {
        return ANCHO_ORBITA * Math.cos(tiempo * VELOCIDAD_ORBITA + indice * 2.0 * Math.PI / ORBITANTES);
    }

    private static double orbitY(double tiempo, int indice) {
        return ORBITA_Y + ALTO_ORBITA * Math.sin(tiempo * VELOCIDAD_ORBITA + indice * 2.0 * Math.PI / ORBITANTES);
    }

    /** Animacion de un efecto de estado. */
    private interface Drawing {
        void paint(Graphics2D g, String nombre, double tiempo);
    }
}
