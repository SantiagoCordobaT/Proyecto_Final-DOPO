package presentation;

import static presentation.Texts.text;

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

    private static final String TITLE = "POOBSoccer";
    private static final String MENU = "menu";
    private static final String MATCH = "match";

    private final HeadSoccer model;
    private final CardLayout cards = new CardLayout();
    private final JPanel deck = new JPanel(cards);
    private final MainMenuP menu;
    private final MatchP match;

    private HeadSoccerGUI(HeadSoccer model) {
        super(TITLE);
        this.model = model;
        menu = new MainMenuP(this);
        match = new MatchP(this, model);
        deck.add(menu, MENU);
        deck.add(match, MATCH);
        setLayout(new BorderLayout());
        add(deck, BorderLayout.CENTER);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    /** Muestra el menu principal. */
    void showMenu() {
        cards.show(deck, MENU);
    }

    /** Muestra la pantalla del partido ya iniciado en la fachada y arranca su ciclo. */
    void showMatch() {
        cards.show(deck, MATCH);
        match.start();
    }

    /** Empieza una configuracion nueva y abre el dialogo de modalidad y tipo de partido. */
    void openGameModes() {
        model.newGame();
        new GameModeD(this, model).open();
    }

    /** Abre el dialogo de seleccion de personajes. */
    void openCharacterSelect() {
        new CharacterSelectD(this, model).open();
    }

    /** Abre la configuracion y, al cerrarla, rotula el menu en el idioma elegido. */
    void openSettings() {
        new SettingsD(this).open();
        menu.refresh();
    }

    /** Abre el resultado del partido terminado. */
    void showResult() {
        new ResultD(this, model).open();
    }

    /** Cierre seguro: detiene el ciclo del partido y libera la ventana. */
    void close() {
        match.stop();
        dispose();
    }

    /**
     * Muestra al usuario un error reportado por el dominio.
     *
     * @param parent Componente sobre el que se centra el mensaje.
     * @param e Excepcion del dominio.
     */
    static void report(Component parent, POOBSoccerException e) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), text("error.title"), JOptionPane.ERROR_MESSAGE);
    }

    private static void launch() {
        try {
            new HeadSoccerGUI(HeadSoccer.getInstance()).setVisible(true);
        } catch (POOBSoccerException e) {
            report(null, e);
        }
    }

    /**
     * Punto de entrada de la aplicacion.
     *
     * @param args No se usan.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HeadSoccerGUI::launch);
    }
}
