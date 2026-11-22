package presentacion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

final class Resources {

    static final Color ASFALTO = new Color(22, 22, 26);
    static final Color CONCRETO = new Color(52, 52, 58);
    static final Color ACENTO = new Color(255, 168, 0);
    private static final Color RIVAL = new Color(0, 196, 228);
    static final Color TEXTO = new Color(242, 240, 232);
    static final Color LISTO = new Color(255, 232, 80);
    static final int ESPACIO = 12;

    private static final String TIPOGRAFIA_CALLE = "Impact";
    static final Font FUENTE_TITULO = new Font(TIPOGRAFIA_CALLE, Font.ITALIC, 76);
    static final Font FUENTE_ENCABEZADO = new Font(TIPOGRAFIA_CALLE, Font.ITALIC, 30);
    static final Font FUENTE_MARCADOR = new Font(TIPOGRAFIA_CALLE, Font.PLAIN, 40);
    static final Font FUENTE_TEXTO = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    static final Font FUENTE_AYUDA = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    private static final int ANCHO = 900;
    private static final int SUELO = 450;
    private static final int ALTO = 490;
    private static final Color[] EQUIPO_NEUTRO = { Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY };

    private static Resources instancia;

    private final Map<String, Color[]> equipos = new HashMap<String, Color[]>();
    private final Color[] coloresJugador = { ACENTO, RIVAL };
    private BufferedImage fondo;

    private Resources() {
        equipos.put("Canadá", new Color[] { new Color(216, 30, 44), Color.WHITE, new Color(216, 30, 44) });
        equipos.put("Japón", new Color[] { Color.WHITE, new Color(188, 0, 45), Color.WHITE });
        equipos.put("Colombia", new Color[] { new Color(252, 209, 22), new Color(0, 56, 147), new Color(206, 17, 38) });
        equipos.put("Brasil", new Color[] { new Color(0, 156, 59), new Color(255, 223, 0), new Color(0, 39, 118) });
        UIManager.put("ProgressBar.selectionForeground", ASFALTO);
        UIManager.put("ProgressBar.selectionBackground", TEXTO);
        fondo = dibujarCalle(ANCHO, ALTO);
    }

    static Resources obtenerInstancia() {
        if (instancia == null) {
            instancia = new Resources();
        }
        return instancia;
    }

    Color[] coloresEquipo(String pais) {
        if (equipos.containsKey(pais)) {
            return equipos.get(pais);
        }
        return EQUIPO_NEUTRO;
    }

    Color colorJugador(int numero) {
        return coloresJugador[numero - 1];
    }

    int altoCancha(double suelo) {
        return (int) Math.round(suelo * ALTO / SUELO);
    }

    void dibujarFondo(Graphics g, int ancho, int alto) {
        if (fondo.getWidth() != ancho || fondo.getHeight() != alto) {
            fondo = dibujarCalle(ancho, alto);
        }
        g.drawImage(fondo, 0, 0, null);
    }

    JButton crearBoton(String texto, ActionListener escuchador) {
        JButton boton = new JButton(texto.toUpperCase());
        boton.setFont(FUENTE_TEXTO);
        boton.setBackground(ACENTO);
        boton.setForeground(ASFALTO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TEXTO, 2),
                BorderFactory.createEmptyBorder(ESPACIO / 2, ESPACIO * 2, ESPACIO / 2, ESPACIO * 2)));
        boton.addActionListener(escuchador);
        return boton;
    }

    JLabel crearEtiqueta(String texto, Font fuente) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    JList<String> crearLista(String[] elementos) {
        JList<String> lista = new JList<String>(elementos);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(FUENTE_TEXTO);
        lista.setBackground(CONCRETO);
        lista.setForeground(TEXTO);
        lista.setSelectionBackground(ACENTO);
        lista.setSelectionForeground(ASFALTO);
        lista.setBorder(BorderFactory.createEmptyBorder(ESPACIO / 2, ESPACIO, ESPACIO / 2, ESPACIO));
        lista.setSelectedIndex(0);
        return lista;
    }

    JProgressBar crearBarra(int maximo, Color color) {
        JProgressBar barra = new JProgressBar(0, maximo);
        barra.setForeground(color);
        barra.setBackground(CONCRETO);
        barra.setFont(FUENTE_TEXTO);
        barra.setBorder(BorderFactory.createLineBorder(TEXTO));
        return barra;
    }

    JPanel crearPanel(LayoutManager disposicion) {
        JPanel panel = new JPanel(disposicion);
        panel.setBackground(ASFALTO);
        return panel;
    }

    private static BufferedImage dibujarCalle(int ancho, int alto) {
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        g.scale(ancho / (double) ANCHO, alto / (double) ALTO);

        g.setColor(new Color(25, 30, 48));
        g.fillRect(0, 0, ANCHO, SUELO);

        g.setColor(new Color(40, 130, 60));
        g.fillRect(0, SUELO, ANCHO, ALTO - SUELO);

        g.setColor(Color.WHITE);
        g.fillRect(0, SUELO, ANCHO, 4);
        g.fillRect(ANCHO / 2 - 2, SUELO - 100, 4, 100);
        g.drawOval(ANCHO / 2 - 50, SUELO - 50, 100, 100);

        g.dispose();
        return imagen;
    }
}
