package presentacion;

import static presentacion.Texts.texto;

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

    private final MatchP partido;
    private final JTextArea controles;

    /**
     * @param dueno   Ventana principal.
     * @param partido Pantalla del partido en pausa.
     */
    PauseD(Frame dueno, MatchP partido) {
        super(dueno, texto("pause.title"));
        this.partido = partido;
        controles = new JTextArea(texto("help.controls"));
        controles.setEditable(false);
        controles.setFont(Resources.FUENTE_TEXTO);
        controles.setBackground(Resources.CONCRETO);
        controles.setForeground(Resources.TEXTO);
        controles.setBorder(BorderFactory.createEmptyBorder(Resources.ESPACIO, Resources.ESPACIO, Resources.ESPACIO,
                Resources.ESPACIO));
        establecerCuerpo(controles);
        agregarBoton(texto("pause.resume"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrar();
            }
        });
        agregarBoton(texto("pause.giveUp"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                partido.abandonar();
            }
        });
    }

    @Override
    protected void cerrar() {
        super.cerrar();
        partido.reanudarPartido();
    }
}
