package presentation;

import static presentation.Texts.text;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Resultado del partido terminado: el ganador, el marcador frente a frente y el
 * estado final segun el tipo de partido (por ejemplo la racha de Supervivencia).
 * Siguiente vuelve a la seleccion de personajes.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class ResultP extends Screen {

    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 48);
    private static final Font SCORE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 36);
    private static final Font TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 22);
    private static final int GAP = 12;

    private final JLabel title = new JLabel();
    private final JLabel winner = new JLabel();
    private final JLabel score = new JLabel();
    private final JLabel status = new JLabel();
    private final JButton next = new JButton();

    /**
     * @param gui Ventana que contiene la pantalla.
     * @param game Fachada del juego: da el resultado del partido.
     */
    ResultP(HeadSoccerGUI gui, HeadSoccer game) {
        super(gui, game);
        setLayout(new GridBagLayout());
        title.setFont(TITLE_FONT);
        winner.setFont(TEXT_FONT);
        score.setFont(SCORE_FONT);
        status.setFont(TEXT_FONT);

        GridBagConstraints at = new GridBagConstraints();
        at.gridx = 0;
        at.insets = new Insets(GAP, GAP, GAP, GAP);
        add(title, at);
        add(winner, at);
        add(score, at);
        add(status, at);
        add(next, at);

        next.addActionListener(e -> gui.showCharacterSelect());
    }

    @Override
    protected void onShow() {
        title.setText(text("result.title"));
        next.setText(text("result.next"));
        try {
            winner.setText(text("result.winner") + " " + game.getWinnerName());
            score.setText(game.getPlayerName(1) + "  " + game.getPlayerGoals(1) + " - "
                    + game.getPlayerGoals(2) + "  " + game.getPlayerName(2));
            status.setText(game.getMatchStatus());
        } catch (POOBSoccerException e) {
            report(this, e);
        }
    }
}
