package presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Marcador del partido: placa de cada jugador a los lados y, en el centro, el
 * estado del partido (reloj, meta o balones) sobre el tanteador.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class HudP extends JPanel {

    private static final String SCORE_SEPARATOR = " - ";

    private final HeadSoccer model;
    private final PlayerPlateP left;
    private final PlayerPlateP right;
    private final JLabel status;
    private final JLabel score;

    /** @param model Fachada del dominio. */
    HudP(HeadSoccer model) {
        super(new GridLayout(1, 3));
        Resources resources = Resources.getInstance();
        this.model = model;
        left = new PlayerPlateP(model, 1, BorderLayout.WEST);
        right = new PlayerPlateP(model, 2, BorderLayout.EAST);
        status = resources.label("", Resources.TEXT_FONT);
        score = resources.label("", Resources.SCORE_FONT);
        status.setForeground(Resources.READY);
        JPanel center = resources.panel(new BorderLayout());
        center.add(status, BorderLayout.NORTH);
        center.add(score, BorderLayout.CENTER);
        add(left);
        add(center);
        add(right);
    }

    /**
     * Actualiza todo el marcador con el estado actual del partido.
     *
     * @throws POOBSoccerException Si no hay un partido iniciado.
     */
    void refresh() throws POOBSoccerException {
        left.refresh();
        right.refresh();
        status.setText(model.getMatchStatus());
        score.setText(model.getPlayerGoals(1) + SCORE_SEPARATOR + model.getPlayerGoals(2));
    }
}
