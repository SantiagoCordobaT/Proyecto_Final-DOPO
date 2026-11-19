package presentacion;

import static presentacion.Texts.text;

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

    private final HeadSoccerGUI ventana;
    private final JLabel ganador;
    private final JLabel marcador;
    private final JLabel estado;

    /**
     * @param ventana Ventana principal.
     * @param modelo  Fachada del dominio, con el partido ya terminado.
     */
    ResultD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, text("result.title"));
        this.ventana = ventana;
        ganador = recursos.label("", Resources.FUENTE_ENCABEZADO);
        marcador = recursos.label("", Resources.FUENTE_MARCADOR);
        estado = recursos.label("", Resources.FUENTE_TEXTO);
        ganador.setForeground(Resources.LISTO);
        JPanel cuerpo = recursos.panel(new GridLayout(3, 1, 0, Resources.ESPACIO / 2));
        cuerpo.add(ganador);
        cuerpo.add(marcador);
        cuerpo.add(estado);
        setBody(cuerpo);
        addButton(text("result.rematch"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                ventana.openCharacterSelect();
            }
        });
        addButton(text("result.menu"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        try {
            ganador.setText(text("result.winner") + " " + modelo.getWinnerName());
            marcador.setText(modelo.getPlayerName(1) + "  " + modelo.getPlayerGoals(1) + " - "
                    + modelo.getPlayerGoals(2) + "  " + modelo.getPlayerName(2));
            estado.setText(modelo.getMatchStatus());
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }

    @Override
    protected void close() {
        super.close();
        ventana.showMenu();
    }
}
