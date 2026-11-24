package presentacion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

/**
 * Constantes visuales y métodos simples para dar estilo uniforme a los paneles
 * y controles de Swing.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Estilo {

    static final Color FONDO = new Color(18, 28, 42);
    static final Color TARJETA = new Color(30, 46, 66);
    static final Color BOTON = new Color(46, 168, 92);
    static final Color RESALTE = new Color(245, 200, 66);
    static final Color TEXTO = new Color(242, 245, 248);
    static final Color JUGADOR_1 = new Color(240, 150, 40);
    static final Color JUGADOR_2 = new Color(60, 185, 235);
    static final int MARGEN = 12;

    static final Font FUENTE_TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 54);
    static final Font FUENTE_SUBTITULO = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    static final Font FUENTE_MARCADOR = new Font(Font.SANS_SERIF, Font.BOLD, 34);
    static final Font FUENTE_NORMAL = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    static final Font FUENTE_PEQUENA = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    private Estilo() {
    }

    static Color colorJugador(int numero) {
        return numero == 1 ? JUGADOR_1 : JUGADOR_2;
    }

    static Color[] coloresPais(String pais) {
        if ("Canadá".equals(pais)) {
            return new Color[] { new Color(216, 30, 44), Color.WHITE, new Color(216, 30, 44) };
        }
        if ("Japón".equals(pais)) {
            return new Color[] { Color.WHITE, new Color(188, 0, 45), Color.WHITE };
        }
        if ("Colombia".equals(pais)) {
            return new Color[] { new Color(252, 209, 22), new Color(0, 56, 147), new Color(206, 17, 38) };
        }
        if ("Brasil".equals(pais)) {
            return new Color[] { new Color(0, 156, 59), new Color(255, 223, 0), new Color(0, 39, 118) };
        }
        return new Color[] { Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY };
    }

    static JButton boton(String texto, ActionListener accion) {
        JButton b = new JButton(texto);
        b.setFont(FUENTE_NORMAL);
        b.setBackground(BOTON);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEXTO, 1),
                BorderFactory.createEmptyBorder(6, 18, 6, 18)));
        b.addActionListener(accion);
        return b;
    }

    static JLabel etiqueta(String texto, Font fuente) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(fuente);
        l.setForeground(TEXTO);
        return l;
    }

    static JList<String> lista(String[] elementos) {
        JList<String> l = new JList<>(elementos);
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setFont(FUENTE_NORMAL);
        l.setBackground(TARJETA);
        l.setForeground(TEXTO);
        l.setSelectionBackground(BOTON);
        l.setSelectionForeground(Color.WHITE);
        l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        if (elementos.length > 0) {
            l.setSelectedIndex(0);
        }
        return l;
    }

    static JProgressBar barra(int maximo, Color color) {
        JProgressBar b = new JProgressBar(0, maximo);
        b.setForeground(color);
        b.setBackground(TARJETA);
        b.setFont(FUENTE_NORMAL);
        b.setBorder(BorderFactory.createLineBorder(TEXTO));
        return b;
    }

    static void dibujarFondoCancha(Graphics g, int ancho, int alto, int sueloY) {
        g.setColor(new Color(22, 34, 54));
        g.fillRect(0, 0, ancho, sueloY);

        g.setColor(new Color(44, 138, 68));
        g.fillRect(0, sueloY, ancho, alto - sueloY);

        g.setColor(Color.WHITE);
        g.fillRect(0, sueloY, ancho, 4);
        g.fillRect(ancho / 2 - 2, sueloY - 100, 4, 100);
        g.drawOval(ancho / 2 - 50, sueloY - 50, 100, 100);
    }
}
