package presentacion;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionListener;

import dominio.HeadSoccer;
import dominio.PlayerSummary;
import dominio.excepciones.POOBSoccerException;

/**
 * Ventana principal del juego. Centraliza el menú, el cambio de pantallas y los
 * diálogos de configuración, modo de juego, pausa y resultado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
public final class HeadSoccerGUI extends JFrame {

    private static final String TITULO = "POOBSoccer";
    private static final String MENU = "menu";
    private static final String PARTIDO = "match";

    private final HeadSoccer modelo;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel baraja = new JPanel(tarjetas);
    private final MatchPanel partido;

    private JLabel subtituloMenu;
    private JButton botonJugar;
    private JButton botonConfig;
    private JButton botonSalir;

    // índice extra para Revancha
    private int modoElegido;
    private int tipoElegido;

    private HeadSoccerGUI(HeadSoccer modelo) {
        super(TITULO);
        this.modelo = modelo;
        this.partido = new MatchPanel(this, modelo);

        baraja.add(construirMenu(), MENU);
        baraja.add(partido, PARTIDO);
        refrescarMenu();

        setLayout(new BorderLayout());
        add(baraja, BorderLayout.CENTER);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });
    }

    // Menú principal

    private JPanel construirMenu() {
        JPanel menu = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                UI.dibujarFondoCancha(g, getWidth(), getHeight(), (int) (getHeight() * 0.78));
            }
        };
        menu.setBorder(BorderFactory.createEmptyBorder(UI.MARGEN * 3, UI.MARGEN, UI.MARGEN * 3, UI.MARGEN));

        JLabel titulo = UI.etiqueta("POOBSOCCER", UI.FUENTE_TITULO);
        titulo.setForeground(UI.RESALTE);
        subtituloMenu = UI.etiqueta("", UI.FUENTE_SUBTITULO);

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 6));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtituloMenu);

        // Botón menú y texto en refrescarMenu()
        botonJugar = UI.boton("", this::abrirModosJuego);
        botonConfig = UI.boton("", this::abrirConfiguracion);
        botonSalir = UI.boton("", this::cerrar);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, UI.MARGEN * 2, 0));
        botones.setOpaque(false);
        botones.add(botonJugar);
        botones.add(botonConfig);
        botones.add(botonSalir);

        menu.add(encabezado, BorderLayout.NORTH);
        menu.add(botones, BorderLayout.SOUTH);
        return menu;
    }

    private void refrescarMenu() {
        subtituloMenu.setText(UI.t("Fútbol 1 vs 1", "1 vs 1 Soccer"));
        botonJugar.setText(UI.t("Jugar", "Play"));
        botonConfig.setText(UI.t("Configuración", "Settings"));
        botonSalir.setText(UI.t("Salir", "Exit"));
    }

    // Navegación principal

    void mostrarMenu() {
        tarjetas.show(baraja, MENU);
    }

    void mostrarPartido() {
        tarjetas.show(baraja, PARTIDO);
        partido.iniciar();
    }

    void cerrar() {
        partido.detener();
        dispose();
    }

    // Diálogos

    void abrirConfiguracion() {
        UI.Modal d = new UI.Modal(this, UI.t("Configuración", "Settings"));
        JList<String> idiomas = UI.lista(UI.obtenerIdiomas());
        idiomas.setSelectedIndex(UI.obtenerIdioma());

        // más opciones, JPanel(GridLayout) y ponerCentro()
        d.ponerCentro(UI.columnaLista(UI.t("Idioma", "Language"), idiomas));
        d.agregarBoton(UI.boton(UI.t("Guardar", "Save"), () -> {
            UI.establecerIdioma(idiomas.getSelectedIndex());
            d.dispose();
            refrescarMenu();
        }));
        d.abrir();
    }

    void abrirModosJuego() {
        UI.Modal d = new UI.Modal(this, UI.t("Modo de juego", "Game mode"));
        JList<String> modos = UI.lista(modelo.obtenerNombresModos());
        JList<String> tipos = UI.lista(modelo.obtenerNombresTiposPartido());

        // Para catálogo, 2 -> 3 y + columnaLista
        JPanel listas = new JPanel(new GridLayout(1, 2, UI.MARGEN, 0));
        listas.setBackground(UI.FONDO);
        listas.add(UI.columnaLista(UI.t("Modalidad", "Mode"), modos));
        listas.add(UI.columnaLista(UI.t("Tipo de partido", "Match type"), tipos));
        d.ponerCentro(listas);

        JButton seleccionar = UI.boton(UI.t("Seleccionar", "Select"), () -> {
            try {
                int modo = modos.getSelectedIndex();
                int tipo = tipos.getSelectedIndex();
                modelo.validarModoYTipo(modo, tipo);
                d.dispose();
                // lista y HeadSoccerGUI
                abrirSeleccionPersonajes(modo, tipo);
            } catch (POOBSoccerException e) {
                reportarError(d, e);
            }
        });
        d.agregarBoton(seleccionar);
        d.agregarBoton(UI.boton(UI.t("Volver", "Back"), d::dispose));

        // JList y listener
        ListSelectionListener cambio = e -> seleccionar
                .setEnabled(!modos.isSelectionEmpty() && !tipos.isSelectionEmpty());
        modos.addListSelectionListener(cambio);
        tipos.addListSelectionListener(cambio);
        d.abrir();
    }

    void abrirSeleccionPersonajes(int indiceModo, int indiceTipo) {
        this.modoElegido = indiceModo;
        this.tipoElegido = indiceTipo;
        abrirSeleccionPersonajes();
    }

    void abrirSeleccionPersonajes() {
        new CharacterSelectDialog(this, modelo, modoElegido, tipoElegido).abrir();
    }

    void abrirPausa() {
        UI.Modal d = new UI.Modal(this, UI.t("Pausa", "Pause"));
        d.alCerrarCon(partido::reanudarPartido);

        // tecla/acción MatchPanel, control
        String ayuda = UI.t(
                "Jugador 1: A y D mover, W saltar, S patear, Q poder.\n"
                        + "Jugador 2: flechas ← y → mover, ↑ saltar, ↓ patear, Ctrl derecho poder.\n"
                        + "La barra de poder se llena con el tiempo. Llena, el poder se arma\n"
                        + "y la siguiente patada al balón lo lanza contra el rival.",
                "Player 1: A and D move, W jump, S kick, Q power.\n"
                        + "Player 2: arrows ← and → move, ↑ jump, ↓ kick, right Ctrl power.\n"
                        + "The power bar fills over time. When full, the power is armed\n"
                        + "and the next kick of the ball fires it at the rival.");

        JTextArea controles = new JTextArea(ayuda);
        controles.setEditable(false);
        controles.setFont(UI.FUENTE_NORMAL);
        controles.setBackground(UI.TARJETA);
        controles.setForeground(UI.TEXTO);
        controles.setBorder(BorderFactory.createEmptyBorder(UI.MARGEN, UI.MARGEN, UI.MARGEN, UI.MARGEN));
        d.ponerCentro(controles);

        d.agregarBoton(UI.boton(UI.t("Reanudar", "Resume"), d::alCerrar));
        d.agregarBoton(UI.boton(UI.t("Abandonar", "Give up"), () -> {
            d.dispose();
            partido.abandonar();
        }));
        d.abrir();
    }

    void mostrarResultado() {
        UI.Modal d = new UI.Modal(this, UI.t("Fin del partido", "Match over"));
        d.alCerrarCon(this::mostrarMenu);

        JLabel ganador = UI.etiqueta("", UI.FUENTE_SUBTITULO);
        JLabel marcador = UI.etiqueta("", UI.FUENTE_MARCADOR);
        JLabel estado = UI.etiqueta("", UI.FUENTE_NORMAL);
        ganador.setForeground(UI.RESALTE);

        // dato extra al fin GridLayout(4, 1) y etiqueta
        JPanel cuerpo = new JPanel(new GridLayout(3, 1, 0, 6));
        cuerpo.setBackground(UI.FONDO);
        cuerpo.add(ganador);
        cuerpo.add(marcador);
        cuerpo.add(estado);
        d.ponerCentro(cuerpo);

        d.agregarBoton(UI.boton(UI.t("Revancha", "Rematch"), () -> {
            d.alCerrar();
            abrirSeleccionPersonajes();
        }));
        d.agregarBoton(UI.boton(UI.t("Menú", "Menu"), d::alCerrar));

        try {
            PlayerSummary j1 = modelo.obtenerResumenJugador(1);
            PlayerSummary j2 = modelo.obtenerResumenJugador(2);
            ganador.setText(UI.t("Ganador:", "Winner:") + " " + modelo.obtenerNombreGanador());
            marcador.setText(j1.obtenerNombre() + "  " + j1.obtenerGoles() + " - " + j2.obtenerGoles() + "  "
                    + j2.obtenerNombre());
            estado.setText(modelo.obtenerEstadoPartido());
        } catch (POOBSoccerException e) {
            reportarError(d, e);
        }
        d.abrir();
    }

    static void reportarError(Component padre, POOBSoccerException e) {
        JOptionPane.showMessageDialog(padre, e.getMessage(), UI.t("Error", "Error"), JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(() -> {
            try {
                new HeadSoccerGUI(new HeadSoccer()).setVisible(true);
            } catch (POOBSoccerException e) {
                reportarError(null, e);
            }
        });
    }
}
