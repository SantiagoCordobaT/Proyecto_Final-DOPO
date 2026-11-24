package presentacion;

import static presentacion.Texts.texto;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Ventana principal del juego. Administra el menú, la pantalla del partido y
 * los diálogos usando únicamente la fachada HeadSoccer.
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
    private final MainMenuP menu;
    private final MatchP partido;

    private HeadSoccerGUI(HeadSoccer modelo) {
        super(TITULO);
        this.modelo = modelo;
        menu = new MainMenuP(this);
        partido = new MatchP(this, modelo);
        baraja.add(menu, MENU);
        baraja.add(partido, PARTIDO);
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

    void mostrarMenu() {
        tarjetas.show(baraja, MENU);
    }

    void mostrarPartido() {
        tarjetas.show(baraja, PARTIDO);
        partido.iniciar();
    }

    void abrirModosJuego() {
        modelo.nuevoJuego();
        new GameModeD(this, modelo).abrir();
    }

    void abrirSeleccionPersonajes() {
        new CharacterSelectD(this, modelo).abrir();
    }

    void abrirConfiguracion() {
        new SettingsD(this).abrir();
        menu.refrescar();
    }

    void mostrarResultado() {
        new ResultD(this, modelo).abrir();
    }

    void cerrar() {
        partido.detener();
        dispose();
    }

    static void reportarError(Component padre, POOBSoccerException e) {
        JOptionPane.showMessageDialog(padre, e.getMessage(), texto("error.title"), JOptionPane.ERROR_MESSAGE);
    }

    private static void iniciarInterfaz() {
        try {
            new HeadSoccerGUI(HeadSoccer.obtenerInstancia()).setVisible(true);
        } catch (POOBSoccerException e) {
            reportarError(null, e);
        }
    }

    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                iniciarInterfaz();
            }
        });
    }
}
