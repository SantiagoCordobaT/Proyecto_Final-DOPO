package presentacion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Panel pequeño que dibuja las franjas con los colores del país del jugador.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class TeamBadge extends JPanel {

    private static final Dimension TAMANO = new Dimension(60, 40);

    private String pais = "";

    TeamBadge() {
        setPreferredSize(TAMANO);
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
        Color[] colores = Resources.obtenerInstancia().coloresEquipo(pais);
        int franja = getWidth() / colores.length;
        for (int i = 0; i < colores.length; i++) {
            g.setColor(colores[i]);
            g.fillRect(i * franja, 0, franja, getHeight());
        }
        g.setColor(Resources.TEXTO);
        g.drawRect(0, 0, colores.length * franja - 1, getHeight() - 1);
    }
}
