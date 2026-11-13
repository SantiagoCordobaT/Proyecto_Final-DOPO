package presentation;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Ventana principal de POOBSoccer: un mazo de pantallas (CardLayout) por el que
 * se navega del menu a la configuracion del partido, a la seleccion de personajes
 * y al partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
public final class HeadSoccerGUI extends JFrame {

    private static final String TITLE = "POOBSoccer";

    private final CardLayout cards = new CardLayout();
    private final JPanel deck = new JPanel(cards);
    private final MainMenuP menu;
    private final MatchSetupP setup;
    private final CharacterSelectP characterSelect;
    private final GamePanel match;
    private final ResultP result;

    /**
     * Crea la ventana y sus pantallas (Creator). La ventana toma el tamano de su
     * pantalla mas grande, la del partido.
     *
     * @param game Fachada del juego que usan las pantallas.
     */
    private HeadSoccerGUI(HeadSoccer game) {
        super(TITLE);
        menu = register(new MainMenuP(this, game));
        setup = register(new MatchSetupP(this, game));
        characterSelect = register(new CharacterSelectP(this, game));
        match = register(new GamePanel(this, game));
        result = register(new ResultP(this, game));
        setContentPane(deck);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /** Muestra el menu principal. */
    void showMenu() {
        display(menu);
    }

    /** Muestra la eleccion de modalidad y tipo de partido. */
    void showSetup() {
        display(setup);
    }

    /** Muestra la seleccion de personajes. */
    void showCharacterSelect() {
        display(characterSelect);
    }

    /** Muestra el partido ya iniciado y arranca su ciclo de juego. */
    void showGame() {
        display(match);
    }

    /** Muestra el resultado del partido terminado. */
    void showResult() {
        display(result);
    }

    private <S extends Screen> S register(S screen) {
        deck.add(screen, screen.getName());
        return screen;
    }

    private void display(Screen screen) {
        cards.show(deck, screen.getName());
        screen.enter();
    }

    /**
     * Abre la ventana en el menu principal. Si el juego no se puede crear, informa el
     * error y no abre la ventana.
     */
    private static void launch() {
        try {
            HeadSoccerGUI gui = new HeadSoccerGUI(HeadSoccer.getInstance());
            gui.showMenu();
            gui.setVisible(true);
        } catch (POOBSoccerException e) {
            Screen.report(null, e);
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
