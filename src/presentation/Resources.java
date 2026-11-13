package presentation;

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

    static final Color ASPHALT = new Color(22, 22, 26);
    static final Color CONCRETE = new Color(52, 52, 58);
    static final Color ACCENT = new Color(255, 168, 0);
    private static final Color RIVAL = new Color(0, 196, 228);
    static final Color TEXT = new Color(242, 240, 232);
    static final Color READY = new Color(255, 232, 80);
    static final int GAP = 12;

    private static final String STREET_FACE = "Impact";
    static final Font TITLE_FONT = new Font(STREET_FACE, Font.ITALIC, 76);
    static final Font HEADING_FONT = new Font(STREET_FACE, Font.ITALIC, 30);
    static final Font SCORE_FONT = new Font(STREET_FACE, Font.PLAIN, 40);
    static final Font TEXT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    static final Font HINT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    private static final Font GRAFFITI_FONT = new Font(STREET_FACE, Font.ITALIC, 92);

    // Medidas nominales del fondo; al pintarlo se escala al tamano del lienzo
    private static final int WIDTH = 900;
    private static final int GROUND = 450;
    private static final int HEIGHT = 490;
    private static final int WALL_TOP = GROUND - 150;
    private static final int FENCE_TOP = WALL_TOP - 90;
    private static final int BUILDING_WIDTH = 75;
    private static final int BRICK_HEIGHT = 15;
    private static final int BRICK_WIDTH = 40;
    private static final int FENCE_STEP = 12;
    private static final int POST_STEP = 150;
    private static final int LAMP_Y = 28;
    private static final int CONE_SPREAD = 220;
    private static final int LAMP_HALO = 64;
    private static final int SLAB_WIDTH = 75;
    private static final int SLAB_SPREAD = 5;
    private static final String GRAFFITI = "POOB";
    private static final Color[] NEUTRAL_TEAM = { Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY };

    private static Resources instance;

    private final Map<String, Color[]> teams = new HashMap<String, Color[]>();
    private final Color[] playerColors = { ACCENT, RIVAL };
    private BufferedImage backdrop;

    private Resources() {
        teams.put("Canadá", new Color[] { new Color(216, 30, 44), Color.WHITE, new Color(216, 30, 44) });
        teams.put("Japón", new Color[] { Color.WHITE, new Color(188, 0, 45), Color.WHITE });
        teams.put("Colombia", new Color[] { new Color(252, 209, 22), new Color(0, 56, 147), new Color(206, 17, 38) });
        teams.put("Brasil", new Color[] { new Color(0, 156, 59), new Color(255, 223, 0), new Color(0, 39, 118) });
        UIManager.put("ProgressBar.selectionForeground", ASPHALT);
        UIManager.put("ProgressBar.selectionBackground", TEXT);
        backdrop = paintStreet(WIDTH, HEIGHT);
    }

    /** @return La unica instancia del gestor de recursos. */
    static Resources getInstance() {
        if (instance == null) {
            instance = new Resources();
        }
        return instance;
    }

    /**
     * @param country Pais del personaje, tal como lo nombra el dominio.
     * @return Los tres colores del equipo; colores neutros si el pais no tiene equipacion.
     */
    Color[] teamColors(String country) {
        return teams.getOrDefault(country, NEUTRAL_TEAM);
    }

    /**
     * @param number Jugador (1 o 2).
     * @return Color que identifica a ese jugador en la cancha y el marcador.
     */
    Color playerColor(int number) {
        return playerColors[number - 1];
    }

    /**
     * @param ground Coordenada Y del suelo de la cancha.
     * @return Alto del lienzo que deja el suelo del fondo justo en esa coordenada.
     */
    int courtHeight(double ground) {
        return (int) Math.round(ground * HEIGHT / GROUND);
    }

    /**
     * Pinta el fondo callejero del tamano indicado. El fondo se dibuja una sola vez
     * por tamano y se reutiliza en cada cuadro.
     */
    void paintBackdrop(Graphics g, int width, int height) {
        if (backdrop.getWidth() != width || backdrop.getHeight() != height) {
            backdrop = paintStreet(width, height);
        }
        g.drawImage(backdrop, 0, 0, null);
    }

    /** @return Boton con el estilo del juego y su escuchador ya suscrito. */
    JButton button(String text, ActionListener listener) {
        JButton button = new JButton(text.toUpperCase());
        button.setFont(TEXT_FONT);
        button.setBackground(ACCENT);
        button.setForeground(ASPHALT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TEXT, 2),
                BorderFactory.createEmptyBorder(GAP / 2, GAP * 2, GAP / 2, GAP * 2)));
        button.addActionListener(listener);
        return button;
    }

    /** @return Etiqueta centrada con la fuente indicada y el color de texto del juego. */
    JLabel label(String text, Font font) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(TEXT);
        return label;
    }

    /** @return Lista de seleccion unica con el estilo del juego y su primer elemento elegido. */
    JList<String> list(String[] items) {
        JList<String> list = new JList<String>(items);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(TEXT_FONT);
        list.setBackground(CONCRETE);
        list.setForeground(TEXT);
        list.setSelectionBackground(ACCENT);
        list.setSelectionForeground(ASPHALT);
        list.setBorder(BorderFactory.createEmptyBorder(GAP / 2, GAP, GAP / 2, GAP));
        list.setSelectedIndex(0);
        return list;
    }

    /** @return Barra de 0 a {@code maximum} del color indicado. */
    JProgressBar bar(int maximum, Color color) {
        JProgressBar bar = new JProgressBar(0, maximum);
        bar.setForeground(color);
        bar.setBackground(CONCRETE);
        bar.setFont(TEXT_FONT);
        bar.setBorder(BorderFactory.createLineBorder(TEXT));
        return bar;
    }

    /** @return Panel oscuro con el administrador de disposicion indicado. */
    JPanel panel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(ASPHALT);
        return panel;
    }

    private static BufferedImage paintStreet(int width, int height) {
        // Imagen en el formato de la pantalla: copiarla en cada cuadro es lo mas rapido posible
        BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration().createCompatibleImage(width, height);
        Graphics2D g = image.createGraphics();
        g.scale(width / (double) WIDTH, height / (double) HEIGHT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintSkyline(g);
        paintFence(g);
        paintWall(g);
        paintFloor(g);
        paintFloodlights(g);
        paintVignette(g);
        g.dispose();
        return image;
    }

    /** Cielo nocturno y edificios con ventanas encendidas. */
    private static void paintSkyline(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(8, 10, 30), 0, GROUND, new Color(70, 44, 80)));
        g.fillRect(0, 0, WIDTH, GROUND);
        Color lit = new Color(250, 205, 110);
        Color dark = new Color(30, 34, 60);
        for (int building = 0; building * BUILDING_WIDTH < WIDTH; building++) {
            int left = building * BUILDING_WIDTH;
            int top = WALL_TOP - 150 - (building * 53) % 130;
            g.setColor(new Color(18, 20, 40));
            g.fillRect(left + 3, top, BUILDING_WIDTH - 6, WALL_TOP - top);
            for (int row = 0; top + 12 + row * 22 < WALL_TOP - 12; row++) {
                for (int column = 0; column < 3; column++) {
                    g.setColor((column * 7 + row * 13 + building * 3) % 4 == 0 ? lit : dark);
                    g.fillRect(left + 12 + column * 20, top + 12 + row * 22, 9, 11);
                }
            }
        }
    }

    /** Malla metalica sobre el muro. */
    private static void paintFence(Graphics2D g) {
        int fenceHeight = WALL_TOP - FENCE_TOP;
        g.setColor(new Color(10, 10, 14, 90));
        g.fillRect(0, FENCE_TOP, WIDTH, fenceHeight);
        g.setColor(new Color(190, 190, 190, 60));
        g.setStroke(new BasicStroke(1f));
        for (int x = -fenceHeight; x < WIDTH; x += FENCE_STEP) {
            g.drawLine(x, WALL_TOP, x + fenceHeight, FENCE_TOP);
            g.drawLine(x + fenceHeight, WALL_TOP, x, FENCE_TOP);
        }
        g.setColor(new Color(120, 120, 126));
        g.fillRect(0, FENCE_TOP, WIDTH, 4);
        for (int x = 0; x <= WIDTH; x += POST_STEP) {
            g.fillRect(x - 2, FENCE_TOP, 4, fenceHeight);
        }
    }

    /** Muro de ladrillo con grafiti. */
    private static void paintWall(Graphics2D g) {
        g.setColor(new Color(112, 60, 46));
        g.fillRect(0, WALL_TOP, WIDTH, GROUND - WALL_TOP);
        g.setColor(new Color(74, 40, 32));
        for (int row = 0; WALL_TOP + row * BRICK_HEIGHT < GROUND; row++) {
            int y = WALL_TOP + row * BRICK_HEIGHT;
            g.drawLine(0, y, WIDTH, y);
            for (int x = (row % 2) * BRICK_WIDTH / 2; x < WIDTH; x += BRICK_WIDTH) {
                g.drawLine(x, y, x, y + BRICK_HEIGHT);
            }
        }
        g.setColor(new Color(255, 60, 120, 150));
        g.fillOval(WIDTH / 2 - 210, WALL_TOP + 28, 190, 86);
        g.setColor(new Color(0, 196, 228, 140));
        g.fillOval(WIDTH / 2 + 20, WALL_TOP + 22, 200, 96);
        GlyphVector glyphs = GRAFFITI_FONT.createGlyphVector(g.getFontRenderContext(), GRAFFITI);
        Shape tag = glyphs.getOutline((float) (WIDTH - glyphs.getLogicalBounds().getWidth()) / 2f, WALL_TOP + 108f);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(tag);
        g.setPaint(new GradientPaint(0, WALL_TOP + 30, READY, 0, WALL_TOP + 110, ACCENT));
        g.fill(tag);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(tag);
        g.setPaint(new GradientPaint(0, WALL_TOP, new Color(0, 0, 0, 0), 0, GROUND, new Color(0, 0, 0, 130)));
        g.fillRect(0, WALL_TOP, WIDTH, GROUND - WALL_TOP);
    }

    /** Dos postes de luz con su haz sobre la cancha, el charco de luz en el piso y el halo de la lampara. */
    private static void paintFloodlights(Graphics2D g) {
        Color glow = new Color(255, 250, 210, 150);
        Color none = new Color(255, 250, 210, 0);
        for (int x = WIDTH / 6; x < WIDTH; x += WIDTH * 2 / 3) {
            g.setPaint(new GradientPaint(0, LAMP_Y, new Color(255, 250, 210, 70), 0, GROUND, none));
            g.fill(new Polygon(new int[] { x - 30, x + 30, x + CONE_SPREAD, x - CONE_SPREAD },
                    new int[] { LAMP_Y + 18, LAMP_Y + 18, GROUND, GROUND }, 4));
            g.setColor(new Color(255, 250, 210, 34));
            g.fillOval(x - CONE_SPREAD, GROUND + 4, 2 * CONE_SPREAD, HEIGHT - GROUND - 8);
            g.setColor(new Color(40, 40, 46));
            g.fillRect(x - 4, LAMP_Y + 10, 8, GROUND - LAMP_Y - 10);
            g.setPaint(new RadialGradientPaint(x, LAMP_Y + 9f, LAMP_HALO, new float[] { 0f, 1f },
                    new Color[] { glow, none }));
            g.fillOval(x - LAMP_HALO, LAMP_Y + 9 - LAMP_HALO, 2 * LAMP_HALO, 2 * LAMP_HALO);
            g.setColor(new Color(236, 234, 214));
            g.fillRoundRect(x - 34, LAMP_Y, 68, 18, 8, 8);
        }
    }

    /** Bordes oscurecidos: centran la mirada en la cancha. */
    private static void paintVignette(Graphics2D g) {
        g.setPaint(new RadialGradientPaint(WIDTH / 2f, HEIGHT / 2f, WIDTH * 0.62f, new float[] { 0.55f, 1f },
                new Color[] { new Color(0, 0, 0, 0), new Color(0, 0, 0, 150) }));
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    /** Piso de concreto en perspectiva con la linea de la cancha, la marca central y grietas. */
    private static void paintFloor(Graphics2D g) {
        g.setPaint(new GradientPaint(0, GROUND, new Color(104, 104, 110), 0, HEIGHT, new Color(56, 56, 62)));
        g.fillRect(0, GROUND, WIDTH, HEIGHT - GROUND);
        g.setColor(new Color(40, 40, 44, 110));
        g.setStroke(new BasicStroke(1f));
        for (int x = -WIDTH / 2; x <= WIDTH / 2; x += SLAB_WIDTH) {
            g.drawLine(WIDTH / 2 + x, GROUND, WIDTH / 2 + x * SLAB_SPREAD / 4, HEIGHT);
        }
        g.drawLine(0, GROUND + (HEIGHT - GROUND) / 3, WIDTH, GROUND + (HEIGHT - GROUND) / 3);
        g.setColor(new Color(40, 40, 44));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolyline(new int[] { 120, 150, 172, 210 }, new int[] { HEIGHT, GROUND + 26, GROUND + 18, GROUND + 4 }, 4);
        g.drawPolyline(new int[] { 700, 676, 640 }, new int[] { HEIGHT, GROUND + 20, GROUND + 8 }, 3);
        g.setColor(new Color(238, 200, 60));
        g.setStroke(new BasicStroke(2f));
        g.drawOval(WIDTH / 2 - 70, GROUND + 10, 140, 18);
        g.setStroke(new BasicStroke(4f));
        g.drawLine(0, GROUND, WIDTH, GROUND);
        g.drawLine(WIDTH / 2, GROUND - 18, WIDTH / 2, GROUND);
    }
}
