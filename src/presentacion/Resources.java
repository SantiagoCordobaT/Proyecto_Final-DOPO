package presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionListener;
import java.awt.font.GlyphVector;
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

/**
 * Gestor centralizado de los recursos graficos de la vista (Singleton): paleta,
 * fuentes, colores de cada equipo, el fondo de cancha callejera (que se pinta
 * una sola vez) y la fabrica de componentes Swing con el estilo del juego.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
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
    private static final Font FUENTE_GRAFFITI = new Font(TIPOGRAFIA_CALLE, Font.ITALIC, 92);

    // Medidas nominales del fondo; al pintarlo se escala al tamano del lienzo
    private static final int ANCHO = 900;
    private static final int SUELO = 450;
    private static final int ALTO = 490;
    private static final int MURO_ARRIBA = SUELO - 150;
    private static final int MALLA_ARRIBA = MURO_ARRIBA - 90;
    private static final int ANCHO_EDIFICIO = 75;
    private static final int ALTO_LADRILLO = 15;
    private static final int ANCHO_LADRILLO = 40;
    private static final int PASO_MALLA = 12;
    private static final int PASO_POSTE = 150;
    private static final int LAMPARA_Y = 28;
    private static final int APERTURA_CONO = 220;
    private static final int HALO_LAMPARA = 64;
    private static final int ANCHO_LOSA = 75;
    private static final int APERTURA_LOSA = 5;
    private static final String GRAFFITI = "POOB";
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

    /** @return La unica instancia del gestor de recursos. */
    static Resources obtenerInstancia() {
        if (instancia == null) {
            instancia = new Resources();
        }
        return instancia;
    }

    /**
     * @param pais Pais del personaje, tal como lo nombra el dominio.
     * @return Los tres colores del equipo; colores neutros si el pais no tiene
     *         equipacion.
     */
    Color[] coloresEquipo(String pais) {
        return equipos.getOrDefault(pais, EQUIPO_NEUTRO);
    }

    /**
     * @param numero Jugador (1 o 2).
     * @return Color que identifica a ese jugador en la cancha y el marcador.
     */
    Color colorJugador(int numero) {
        return coloresJugador[numero - 1];
    }

    /**
     * @param suelo Coordenada Y del suelo de la cancha.
     * @return Alto del lienzo que deja el suelo del fondo justo en esa coordenada.
     */
    int altoCancha(double suelo) {
        return (int) Math.round(suelo * ALTO / SUELO);
    }

    /**
     * Pinta el fondo callejero del tamano indicado. El fondo se dibuja una sola vez
     * por tamano y se reutiliza en cada cuadro.
     */
    void dibujarFondo(Graphics g, int ancho, int alto) {
        if (fondo.getWidth() != ancho || fondo.getHeight() != alto) {
            fondo = dibujarCalle(ancho, alto);
        }
        g.drawImage(fondo, 0, 0, null);
    }

    /** @return Boton con el estilo del juego y su escuchador ya suscrito. */
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

    /**
     * @return Etiqueta centrada con la fuente indicada y el color de texto del
     *         juego.
     */
    JLabel crearEtiqueta(String texto, Font fuente) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    /**
     * @return Lista de seleccion unica con el estilo del juego y su primer elemento
     *         elegido.
     */
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

    /** @return Barra de 0 a {@code maximo} del color indicado. */
    JProgressBar crearBarra(int maximo, Color color) {
        JProgressBar barra = new JProgressBar(0, maximo);
        barra.setForeground(color);
        barra.setBackground(CONCRETO);
        barra.setFont(FUENTE_TEXTO);
        barra.setBorder(BorderFactory.createLineBorder(TEXTO));
        return barra;
    }

    /** @return Panel oscuro con el administrador de disposicion indicado. */
    JPanel crearPanel(LayoutManager disposicion) {
        JPanel panel = new JPanel(disposicion);
        panel.setBackground(ASFALTO);
        return panel;
    }

    private static BufferedImage dibujarCalle(int ancho, int alto) {
        // Imagen en el formato de la pantalla: copiarla en cada cuadro es lo mas rapido
        // posible
        BufferedImage imagen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration().createCompatibleImage(ancho, alto);
        Graphics2D g = imagen.createGraphics();
        g.scale(ancho / (double) ANCHO, alto / (double) ALTO);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        dibujarEdificios(g);
        dibujarMalla(g);
        dibujarMuro(g);
        dibujarPiso(g);
        dibujarReflectores(g);
        dibujarVineta(g);
        g.dispose();
        return imagen;
    }

    /** Cielo nocturno y edificios con ventanas encendidas. */
    private static void dibujarEdificios(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(8, 10, 30), 0, SUELO, new Color(70, 44, 80)));
        g.fillRect(0, 0, ANCHO, SUELO);
        Color iluminada = new Color(250, 205, 110);
        Color oscura = new Color(30, 34, 60);
        for (int edificio = 0; edificio * ANCHO_EDIFICIO < ANCHO; edificio++) {
            int izquierda = edificio * ANCHO_EDIFICIO;
            int arriba = MURO_ARRIBA - 150 - (edificio * 53) % 130;
            g.setColor(new Color(18, 20, 40));
            g.fillRect(izquierda + 3, arriba, ANCHO_EDIFICIO - 6, MURO_ARRIBA - arriba);
            for (int fila = 0; arriba + 12 + fila * 22 < MURO_ARRIBA - 12; fila++) {
                for (int columna = 0; columna < 3; columna++) {
                    g.setColor((columna * 7 + fila * 13 + edificio * 3) % 4 == 0 ? iluminada : oscura);
                    g.fillRect(izquierda + 12 + columna * 20, arriba + 12 + fila * 22, 9, 11);
                }
            }
        }
    }

    /** Malla metalica sobre el muro. */
    private static void dibujarMalla(Graphics2D g) {
        int altoMalla = MURO_ARRIBA - MALLA_ARRIBA;
        g.setColor(new Color(10, 10, 14, 90));
        g.fillRect(0, MALLA_ARRIBA, ANCHO, altoMalla);
        g.setColor(new Color(190, 190, 190, 60));
        g.setStroke(new BasicStroke(1f));
        for (int x = -altoMalla; x < ANCHO; x += PASO_MALLA) {
            g.drawLine(x, MURO_ARRIBA, x + altoMalla, MALLA_ARRIBA);
            g.drawLine(x + altoMalla, MURO_ARRIBA, x, MALLA_ARRIBA);
        }
        g.setColor(new Color(120, 120, 126));
        g.fillRect(0, MALLA_ARRIBA, ANCHO, 4);
        for (int x = 0; x <= ANCHO; x += PASO_POSTE) {
            g.fillRect(x - 2, MALLA_ARRIBA, 4, altoMalla);
        }
    }

    /** Muro de ladrillo con grafiti. */
    private static void dibujarMuro(Graphics2D g) {
        g.setColor(new Color(112, 60, 46));
        g.fillRect(0, MURO_ARRIBA, ANCHO, SUELO - MURO_ARRIBA);
        g.setColor(new Color(74, 40, 32));
        for (int fila = 0; MURO_ARRIBA + fila * ALTO_LADRILLO < SUELO; fila++) {
            int y = MURO_ARRIBA + fila * ALTO_LADRILLO;
            g.drawLine(0, y, ANCHO, y);
            for (int x = (fila % 2) * ANCHO_LADRILLO / 2; x < ANCHO; x += ANCHO_LADRILLO) {
                g.drawLine(x, y, x, y + ALTO_LADRILLO);
            }
        }
        g.setColor(new Color(255, 60, 120, 150));
        g.fillOval(ANCHO / 2 - 210, MURO_ARRIBA + 28, 190, 86);
        g.setColor(new Color(0, 196, 228, 140));
        g.fillOval(ANCHO / 2 + 20, MURO_ARRIBA + 22, 200, 96);
        GlyphVector glifos = FUENTE_GRAFFITI.createGlyphVector(g.getFontRenderContext(), GRAFFITI);
        Shape firma = glifos.getOutline((float) (ANCHO - glifos.getLogicalBounds().getWidth()) / 2f, MURO_ARRIBA + 108f);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(firma);
        g.setPaint(new GradientPaint(0, MURO_ARRIBA + 30, LISTO, 0, MURO_ARRIBA + 110, ACENTO));
        g.fill(firma);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(firma);
        g.setPaint(new GradientPaint(0, MURO_ARRIBA, new Color(0, 0, 0, 0), 0, SUELO, new Color(0, 0, 0, 130)));
        g.fillRect(0, MURO_ARRIBA, ANCHO, SUELO - MURO_ARRIBA);
    }

    /**
     * Dos postes de luz con su haz sobre la cancha, el charco de luz en el piso y
     * el halo de la lampara.
     */
    private static void dibujarReflectores(Graphics2D g) {
        Color brillo = new Color(255, 250, 210, 150);
        Color transparente = new Color(255, 250, 210, 0);
        for (int x = ANCHO / 6; x < ANCHO; x += ANCHO * 2 / 3) {
            g.setPaint(new GradientPaint(0, LAMPARA_Y, new Color(255, 250, 210, 70), 0, SUELO, transparente));
            g.fill(new Polygon(new int[] { x - 30, x + 30, x + APERTURA_CONO, x - APERTURA_CONO },
                    new int[] { LAMPARA_Y + 18, LAMPARA_Y + 18, SUELO, SUELO }, 4));
            g.setColor(new Color(255, 250, 210, 34));
            g.fillOval(x - APERTURA_CONO, SUELO + 4, 2 * APERTURA_CONO, ALTO - SUELO - 8);
            g.setColor(new Color(40, 40, 46));
            g.fillRect(x - 4, LAMPARA_Y + 10, 8, SUELO - LAMPARA_Y - 10);
            g.setPaint(new RadialGradientPaint(x, LAMPARA_Y + 9f, HALO_LAMPARA, new float[] { 0f, 1f },
                    new Color[] { brillo, transparente }));
            g.fillOval(x - HALO_LAMPARA, LAMPARA_Y + 9 - HALO_LAMPARA, 2 * HALO_LAMPARA, 2 * HALO_LAMPARA);
            g.setColor(new Color(236, 234, 214));
            g.fillRoundRect(x - 34, LAMPARA_Y, 68, 18, 8, 8);
        }
    }

    /** Bordes oscurecidos: centran la mirada en la cancha. */
    private static void dibujarVineta(Graphics2D g) {
        g.setPaint(new RadialGradientPaint(ANCHO / 2f, ALTO / 2f, ANCHO * 0.62f, new float[] { 0.55f, 1f },
                new Color[] { new Color(0, 0, 0, 0), new Color(0, 0, 0, 150) }));
        g.fillRect(0, 0, ANCHO, ALTO);
    }

    /**
     * Piso de concreto en perspectiva con la linea de la cancha, la marca central y
     * grietas.
     */
    private static void dibujarPiso(Graphics2D g) {
        g.setPaint(new GradientPaint(0, SUELO, new Color(104, 104, 110), 0, ALTO, new Color(56, 56, 62)));
        g.fillRect(0, SUELO, ANCHO, ALTO - SUELO);
        g.setColor(new Color(40, 40, 44, 110));
        g.setStroke(new BasicStroke(1f));
        for (int x = -ANCHO / 2; x <= ANCHO / 2; x += ANCHO_LOSA) {
            g.drawLine(ANCHO / 2 + x, SUELO, ANCHO / 2 + x * APERTURA_LOSA / 4, ALTO);
        }
        g.drawLine(0, SUELO + (ALTO - SUELO) / 3, ANCHO, SUELO + (ALTO - SUELO) / 3);
        g.setColor(new Color(40, 40, 44));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolyline(new int[] { 120, 150, 172, 210 }, new int[] { ALTO, SUELO + 26, SUELO + 18, SUELO + 4 }, 4);
        g.drawPolyline(new int[] { 700, 676, 640 }, new int[] { ALTO, SUELO + 20, SUELO + 8 }, 3);
        g.setColor(new Color(238, 200, 60));
        g.setStroke(new BasicStroke(2f));
        g.drawOval(ANCHO / 2 - 70, SUELO + 10, 140, 18);
        g.setStroke(new BasicStroke(4f));
        g.drawLine(0, SUELO, ANCHO, SUELO);
        g.drawLine(ANCHO / 2, SUELO - 18, ANCHO / 2, SUELO);
    }
}
