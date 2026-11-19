package presentacion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Escudo de un equipo: franjas con los colores de su pais.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class TeamBadge extends JPanel {

    private static final Dimension TAMANO = new Dimension(60, 40);

    private String pais = "";

    /** Crea el escudo con colores neutros hasta que se le indique un pais. */
    TeamBadge() {
        setPreferredSize(TAMANO);
    }

    /** @param pais Pais cuyos colores debe mostrar el escudo. */
    void setCountry(String pais) {
        if (!pais.equals(this.pais)) {
            this.pais = pais;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color[] colores = Resources.getInstance().teamColors(pais);
        int franja = getWidth() / colores.length;
        for (int i = 0; i < colores.length; i++) {
            g.setColor(colores[i]);
            g.fillRect(i * franja, 0, franja, getHeight());
        }
        g.setColor(Resources.TEXTO);
        g.drawRect(0, 0, colores.length * franja - 1, getHeight() - 1);
    }
}
