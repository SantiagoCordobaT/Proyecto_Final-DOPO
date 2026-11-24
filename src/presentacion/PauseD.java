package presentacion;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * Diálogo de pausa que recuerda los controles y deja reanudar o salir al menú.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class PauseD extends BaseDialog {

    private final MatchP partido;

    PauseD(Frame dueno, MatchP partido) {
        super(dueno, Texts.t("Pausa", "Pause"));
        this.partido = partido;

        String ayuda = Texts.t(
                "Jugador 1: A y D mover, W saltar, S patear, Q poder.\n"
                        + "Jugador 2: flechas ← y → mover, ↑ saltar, ↓ patear, Ctrl derecho poder.\n"
                        + "La barra de poder se llena con el tiempo. Llena, el poder se arma\n"
                        + "y la siguiente patada al balón lo lanza contra el rival.",
                "Player 1: A and D move, W jump, S kick, Q power.\n"
                        + "Player 2: arrows ← and → move, ↑ jump, ↓ kick, right Ctrl power.\n"
                        + "The power bar fills over time. When full, the power is armed\n"
                        + "and the next kick of the ball fires it at the rival.");

        JTextArea controles = new JTextArea(ayuda);
        controles.setEditable(false);
        controles.setFont(Style.FUENTE_NORMAL);
        controles.setBackground(Style.TARJETA);
        controles.setForeground(Style.TEXTO);
        controles.setBorder(BorderFactory.createEmptyBorder(Style.MARGEN, Style.MARGEN, Style.MARGEN, Style.MARGEN));
        ponerCentro(controles);

        agregarBoton(Style.boton(Texts.t("Reanudar", "Resume"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar();
            }
        }));
        agregarBoton(Style.boton(Texts.t("Abandonar", "Give up"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                partido.abandonar();
            }
        }));
    }

    @Override
    protected void alCerrar() {
        dispose();
        partido.reanudarPartido();
    }
}
