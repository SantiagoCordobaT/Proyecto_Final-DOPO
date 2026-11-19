package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Marcador del partido: placa de cada jugador a los lados y, en el centro, el
 * estado del partido (reloj, meta o balones), el tanteador y la ayuda de pausa.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class HudP extends JPanel {

    private static final String SCORE_SEPARATOR = " - ";
    private static final double FLASH_SECONDS = 0.8;

    private final HeadSoccer model;
    private final PlayerPlateP left;
    private final PlayerPlateP right;
    private final JLabel status;
    private final JLabel score;
    private final JLabel hint;
    private double flash;

    /** @param model Fachada del dominio. */
    HudP(HeadSoccer model) {
        super(new GridLayout(1, 3));
        Resources resources = Resources.getInstance();
        this.model = model;
        left = new PlayerPlateP(model, 1, BorderLayout.WEST);
        right = new PlayerPlateP(model, 2, BorderLayout.EAST);
        status = resources.label("", Resources.TEXT_FONT);
        score = resources.label("", Resources.SCORE_FONT);
        hint = resources.label("", Resources.HINT_FONT);
        status.setForeground(Resources.READY);
        JPanel center = resources.panel(new BorderLayout());
        center.add(status, BorderLayout.NORTH);
        center.add(score, BorderLayout.CENTER);
        center.add(hint, BorderLayout.SOUTH);
        add(left);
        add(center);
        add(right);
    }

    /** Rotula la ayuda del marcador en el idioma actual. */
    void relabel() {
        hint.setText(text("match.hint"));
    }

    /**
     * Actualiza todo el marcador con el estado actual del partido; el tanteador
     * destella un instante cada vez que cambia.
     *
     * @param seconds Segundos reales transcurridos desde el cuadro anterior.
     * @throws POOBSoccerException Si no hay un partido iniciado.
     */
    void refresh(double seconds) throws POOBSoccerException {
        String current = model.getPlayerGoals(1) + SCORE_SEPARATOR + model.getPlayerGoals(2);
        if (!current.equals(score.getText())) {
            flash = FLASH_SECONDS;
            score.setText(current);
        }
        flash = Math.max(0.0, flash - seconds);
        score.setForeground(flash > 0.0 ? Resources.READY : Resources.TEXT);
        left.refresh(seconds);
        right.refresh(seconds);
        status.setText(model.getMatchStatus());
    }
}
