package presentacion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Panel pequeño que dibuja los colores del país del personaje.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class TeamBadge extends JPanel {

    private String pais = "";

    TeamBadge() {
        setPreferredSize(new Dimension(60, 40));
    }

    void establecerPais(String pais) {
        if (!pais.equals(this.pais)) {
            this.pais = pais;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color[] colores = Style.coloresPais(pais);
        int franja = getWidth() / colores.length;
        for (int i = 0; i < colores.length; i++) {
            g.setColor(colores[i]);
            g.fillRect(i * franja, 0, franja, getHeight());
        }
        g.setColor(Style.TEXTO);
        g.drawRect(0, 0, colores.length * franja - 1, getHeight() - 1);
    }
}
