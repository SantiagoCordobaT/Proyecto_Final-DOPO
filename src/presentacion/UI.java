package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Utilidades compartidas de la interfaz: colores, fuentes, idioma, fábrica de
 * componentes Swing, escudo de país y diálogo modal base.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class UI {

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

    private static final String[] IDIOMAS = { "Español", "English" };
    private static int idioma;

    private UI() {
    }

    // Idioma

    static String t(String espanol, String ingles) {
        return idioma == 1 ? ingles : espanol;
    }

    static String[] obtenerIdiomas() {
        return IDIOMAS.clone();
    }

    static int obtenerIdioma() {
        return idioma;
    }

    static void establecerIdioma(int nuevoIdioma) {
        idioma = nuevoIdioma == 1 ? 1 : 0;
    }

    // Colores

    static Color colorJugador(int numero) {
        return numero == 1 ? JUGADOR_1 : JUGADOR_2;
    }

    // países, paleta 3 colores
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

    // Fábrica de componentes

    static JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.setFont(FUENTE_NORMAL);
        b.setBackground(BOTON);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEXTO, 1),
                BorderFactory.createEmptyBorder(6, 18, 6, 18)));
        b.addActionListener(e -> accion.run());
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

    static JPanel columnaLista(String titulo, JList<String> lista) {
        JPanel columna = new JPanel(new BorderLayout(0, 6));
        columna.setBackground(FONDO);
        columna.add(etiqueta(titulo, FUENTE_NORMAL), BorderLayout.NORTH);
        columna.add(new JScrollPane(lista), BorderLayout.CENTER);
        return columna;
    }

    // tipos cancha, sobrecarga con nombreCancha (no romper firma del menú)
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

    // Escudo por país

    @SuppressWarnings("serial")
    static final class Badge extends JPanel {

        private String pais = "";

        Badge() {
            setPreferredSize(new Dimension(60, 40));
        }

        void establecerPais(String nuevoPais) {
            if (!nuevoPais.equals(this.pais)) {
                this.pais = nuevoPais;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color[] colores = coloresPais(pais);
            int franja = getWidth() / colores.length;
            for (int i = 0; i < colores.length; i++) {
                g.setColor(colores[i]);
                g.fillRect(i * franja, 0, franja, getHeight());
            }
            g.setColor(TEXTO);
            g.drawRect(0, 0, colores.length * franja - 1, getHeight() - 1);
        }
    }

    // Diálogo modal base: new UI.Modal + ponerCentro + agregarBoton + abrir()
    @SuppressWarnings("serial")
    static class Modal extends JDialog {

        private final JPanel contenido;
        private final JPanel botones;
        private Runnable accionCerrar;

        Modal(Frame dueno, String titulo) {
            super(dueno, titulo, true);
            contenido = new JPanel(new BorderLayout(MARGEN, MARGEN));
            contenido.setBackground(FONDO);
            contenido.setBorder(BorderFactory.createEmptyBorder(MARGEN, MARGEN * 2, MARGEN, MARGEN * 2));
            contenido.add(etiqueta(titulo, FUENTE_SUBTITULO), BorderLayout.NORTH);

            botones = new JPanel(new FlowLayout(FlowLayout.CENTER, MARGEN, 0));
            botones.setBackground(FONDO);
            contenido.add(botones, BorderLayout.SOUTH);

            setContentPane(contenido);
            setResizable(false);
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    alCerrar();
                }
            });
        }

        void ponerCentro(JComponent componente) {
            contenido.add(componente, BorderLayout.CENTER);
        }

        void agregarBoton(JButton boton) {
            botones.add(boton);
        }

        void alCerrarCon(Runnable accion) {
            this.accionCerrar = accion;
        }

        protected void alCerrar() {
            dispose();
            if (accionCerrar != null) {
                accionCerrar.run();
            }
        }

        void abrir() {
            pack();
            setLocationRelativeTo(getOwner());
            setVisible(true);
        }
    }
}
