package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Seleccion de personajes: la tarjeta del jugador 1 y la del jugador 2 frente a
 * frente. Seleccionar registra ambos personajes e inicia el partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterSelectP extends StepScreen {

    private static final String VERSUS = "VS";
    private static final Font VERSUS_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 64);

    private final CharacterCard first;
    private final CharacterCard second;

    /**
     * @param gui Ventana que contiene la pantalla.
     * @param game Fachada del juego.
     */
    CharacterSelectP(HeadSoccerGUI gui, HeadSoccer game) {
        super(gui, game, "select.title");
        first = new CharacterCard(game, "select.player1");
        second = new CharacterCard(game, "select.player2");
        JLabel versus = new JLabel(VERSUS, SwingConstants.CENTER);
        versus.setFont(VERSUS_FONT);

        JPanel cards = new JPanel(new GridLayout(1, 3));
        cards.add(first);
        cards.add(versus);
        cards.add(second);
        add(cards, BorderLayout.CENTER);
    }

    @Override
    protected void refresh() {
        first.refresh();
        second.refresh();
    }

    @Override
    protected void select() {
        try {
            game.selectPlayer(1, first.getSelectedIndex());
            game.selectPlayer(2, second.getSelectedIndex());
            game.startMatch();
            JOptionPane.showMessageDialog(this, text("select.ready"));
        } catch (POOBSoccerException e) {
            report(this, e);
        }
    }

    @Override
    protected void back() {
        gui.showSetup();
    }
}
