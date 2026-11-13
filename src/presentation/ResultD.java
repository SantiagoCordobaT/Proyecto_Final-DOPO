package presentation;

import static presentation.Texts.text;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Dialogo de fin del partido: ganador, tanteador y estado final. Ofrece la
 * revancha (volver a elegir personajes) o regresar al menu.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class ResultD extends StreetDialog {

    private final HeadSoccerGUI gui;
    private final JLabel winner;
    private final JLabel score;
    private final JLabel status;

    /**
     * @param gui Ventana principal.
     * @param model Fachada del dominio, con el partido ya terminado.
     */
    ResultD(HeadSoccerGUI gui, HeadSoccer model) {
        super(gui, text("result.title"));
        this.gui = gui;
        winner = resources.label("", Resources.HEADING_FONT);
        score = resources.label("", Resources.SCORE_FONT);
        status = resources.label("", Resources.TEXT_FONT);
        winner.setForeground(Resources.READY);
        JPanel body = resources.panel(new GridLayout(3, 1, 0, Resources.GAP / 2));
        body.add(winner);
        body.add(score);
        body.add(status);
        setBody(body);
        addButton(text("result.rematch"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                gui.openCharacterSelect();
            }
        });
        addButton(text("result.menu"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        try {
            winner.setText(text("result.winner") + " " + model.getWinnerName());
            score.setText(model.getPlayerName(1) + "  " + model.getPlayerGoals(1) + " - "
                    + model.getPlayerGoals(2) + "  " + model.getPlayerName(2));
            status.setText(model.getMatchStatus());
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }

    @Override
    protected void close() {
        super.close();
        gui.showMenu();
    }
}
