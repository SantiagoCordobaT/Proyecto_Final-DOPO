package presentation;

import static presentation.Texts.text;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * Dialogo de pausa: recuerda los controles y permite reanudar o abandonar.
 * Cerrar la ventana equivale a reanudar, para no dejar el partido congelado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class PauseD extends StreetDialog {

    private final MatchP match;
    private final JTextArea controls;

    /**
     * @param owner Ventana principal.
     * @param match Pantalla del partido en pausa.
     */
    PauseD(Frame owner, MatchP match) {
        super(owner, text("pause.title"));
        this.match = match;
        controls = new JTextArea(text("help.controls"));
        controls.setEditable(false);
        controls.setFont(Resources.TEXT_FONT);
        controls.setBackground(Resources.CONCRETE);
        controls.setForeground(Resources.TEXT);
        controls.setBorder(BorderFactory.createEmptyBorder(Resources.GAP, Resources.GAP, Resources.GAP, Resources.GAP));
        setBody(controls);
        addButton(text("pause.resume"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        addButton(text("pause.giveUp"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                match.giveUp();
            }
        });
    }

    @Override
    protected void close() {
        super.close();
        match.resumeGame();
    }
}
