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
 * Ventana principal de POOBSoccer. Mantiene la unica referencia de la vista al
 * dominio (la fachada {@link HeadSoccer}), compone el menu y la pantalla del
 * partido, y abre los dialogos de configuracion, seleccion y resultado.
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

    /** Muestra el menu principal. */
    void mostrarMenu() {
        tarjetas.show(baraja, MENU);
    }

    /**
     * Muestra la pantalla del partido ya iniciado en la fachada y arranca su ciclo.
     */
    void mostrarPartido() {
        tarjetas.show(baraja, PARTIDO);
        partido.iniciar();
    }

    /**
     * Empieza una configuracion nueva y abre el dialogo de modalidad y tipo de
     * partido.
     */
    void abrirModosJuego() {
        modelo.nuevoJuego();
        new GameModeD(this, modelo).abrir();
    }

    /** Abre el dialogo de seleccion de personajes. */
    void abrirSeleccionPersonajes() {
        new CharacterSelectD(this, modelo).abrir();
    }

    /**
     * Abre la configuracion y, al cerrarla, rotula el menu en el idioma elegido.
     */
    void abrirConfiguracion() {
        new SettingsD(this).abrir();
        menu.refrescar();
    }

    /** Abre el resultado del partido terminado. */
    void mostrarResultado() {
        new ResultD(this, modelo).abrir();
    }

    /** Cierre seguro: detiene el ciclo del partido y libera la ventana. */
    void cerrar() {
        partido.detener();
        dispose();
    }

    /**
     * Muestra al usuario un error reportado por el dominio.
     *
     * @param padre Componente sobre el que se centra el mensaje.
     * @param e     Excepcion del dominio.
     */
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

    /**
     * Punto de entrada de la aplicacion.
     *
     * @param argumentos No se usan.
     */
    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                iniciarInterfaz();
            }
        });
    }
}
