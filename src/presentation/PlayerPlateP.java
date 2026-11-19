package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Placa de un jugador en el marcador: escudo del pais, nombre y barra de poder
 * (con el nombre del poder, o el aviso de poder listo cuando esta armado).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class PlayerPlateP extends JPanel {

    private static final int PERCENT = 100;
    private static final double PULSE_SECONDS = 0.5;
    private static final Color[] PULSE_COLORS = { Resources.READY, Resources.TEXT };

    private final HeadSoccer model;
    private final int number;
    private final Color color;
    private final TeamBadge badge = new TeamBadge();
    private final JLabel name;
    private final JProgressBar power;
    private double pulse;

    /**
     * @param model Fachada del dominio.
     * @param number Jugador (1 o 2) que muestra la placa.
     * @param badgeSide Lado del escudo ({@link BorderLayout#WEST} o {@link BorderLayout#EAST}).
     */
    PlayerPlateP(HeadSoccer model, int number, String badgeSide) {
        super(new BorderLayout(Resources.GAP, 0));
        Resources resources = Resources.getInstance();
        this.model = model;
        this.number = number;
        this.color = resources.playerColor(number);
        name = resources.label("", Resources.HEADING_FONT);
        power = resources.bar(PERCENT, color);
        power.setStringPainted(true);
        JPanel data = new JPanel(new GridLayout(2, 1, 0, Resources.GAP / 3));
        data.setOpaque(false);
        data.add(name);
        data.add(power);
        setBackground(Resources.ASPHALT);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, color),
                BorderFactory.createEmptyBorder(Resources.GAP / 2, Resources.GAP, Resources.GAP / 2, Resources.GAP)));
        add(badge, badgeSide);
        add(data, BorderLayout.CENTER);
    }

    /**
     * Actualiza la placa con el estado actual del jugador; la barra late mientras
     * el poder esta armado.
     *
     * @param seconds Segundos reales transcurridos desde el cuadro anterior.
     * @throws POOBSoccerException Si no hay un partido iniciado.
     */
    void refresh(double seconds) throws POOBSoccerException {
        boolean armed = model.isPlayerPowerArmed(number);
        badge.setCountry(model.getPlayerCountry(number));
        name.setText(model.getPlayerName(number).toUpperCase());
        power.setValue((int) Math.round(model.getPlayerPowerBar(number) * PERCENT));
        power.setString(armed ? text("hud.armed") : model.getPlayerPowerName(number));
        pulse = (pulse + seconds) % PULSE_SECONDS;
        power.setForeground(armed ? PULSE_COLORS[(int) (pulse * PULSE_COLORS.length / PULSE_SECONDS)] : color);
    }
}
