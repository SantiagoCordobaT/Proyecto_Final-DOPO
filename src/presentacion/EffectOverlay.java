package presentacion;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Dibuja los indicadores visuales de los efectos temporales sobre un jugador.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class EffectOverlay {

    private static final Color HIELO = new Color(150, 220, 255, 130);
    private static final Color BORDE_HIELO = new Color(200, 240, 255);
    private static final Color ESTRELLA = new Color(255, 230, 50);
    private static final Color MAREO = new Color(220, 150, 255);

    void dibujar(Graphics2D g, String[] efectos) {
        for (int i = 0; i < efectos.length; i++) {
            dibujarEfecto(g, efectos[i]);
        }
    }

    private void dibujarEfecto(Graphics2D g, String efecto) {
        if ("Congelado".equals(efecto)) {
            g.setColor(HIELO);
            g.fillRect(-35, -95, 70, 95);
            g.setColor(BORDE_HIELO);
            g.drawRect(-35, -95, 70, 95);
        } else if ("Aturdido".equals(efecto)) {
            g.setColor(ESTRELLA);
            g.fillOval(-20, -105, 10, 10);
            g.fillOval(0, -110, 10, 10);
            g.fillOval(15, -105, 10, 10);
        } else if ("Confundido".equals(efecto)) {
            g.setColor(MAREO);
            g.setFont(Resources.FUENTE_ENCABEZADO);
            g.drawString("?", -15, -95);
            g.drawString("?", 5, -100);
        } else if ("Reducido".equals(efecto)) {
            g.setColor(Color.CYAN);
            g.drawString("↓", -5, -95);
        } else {
            g.setColor(Color.WHITE);
            g.drawString(efecto, -20, -100);
        }
    }
}
