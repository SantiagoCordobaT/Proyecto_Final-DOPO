package presentacion;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Dibuja los efectos activos (hielo, aturdimiento, confusión o reducción)
 * encima de un jugador.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class EffectOverlay {

    void dibujar(Graphics2D g, String[] efectos) {
        for (int i = 0; i < efectos.length; i++) {
            String efecto = efectos[i];
            if ("Congelado".equals(efecto)) {
                g.setColor(new Color(150, 220, 255, 130));
                g.fillRect(-35, -95, 70, 95);
                g.setColor(new Color(200, 240, 255));
                g.drawRect(-35, -95, 70, 95);
            } else if ("Aturdido".equals(efecto)) {
                g.setColor(new Color(255, 230, 50));
                g.fillOval(-20, -105, 10, 10);
                g.fillOval(0, -110, 10, 10);
                g.fillOval(15, -105, 10, 10);
            } else if ("Confundido".equals(efecto)) {
                g.setColor(new Color(220, 150, 255));
                g.setFont(Style.FUENTE_SUBTITULO);
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
}
