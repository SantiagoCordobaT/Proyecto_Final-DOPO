package presentacion;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.PlayerSummary;
import dominio.excepciones.POOBSoccerException;

/**
 * Diálogo de fin de partido con el ganador, el marcador final y los botones de
 * revancha o volver al menú.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class ResultD extends BaseDialog {

    private final HeadSoccerGUI ventana;

    ResultD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, Texts.t("Fin del partido", "Match over"));
        this.ventana = ventana;

        JLabel ganador = Style.etiqueta("", Style.FUENTE_SUBTITULO);
        JLabel marcador = Style.etiqueta("", Style.FUENTE_MARCADOR);
        JLabel estado = Style.etiqueta("", Style.FUENTE_NORMAL);
        ganador.setForeground(Style.RESALTE);

        JPanel cuerpo = new JPanel(new GridLayout(3, 1, 0, 6));
        cuerpo.setBackground(Style.FONDO);
        cuerpo.add(ganador);
        cuerpo.add(marcador);
        cuerpo.add(estado);
        ponerCentro(cuerpo);

        agregarBoton(Style.boton(Texts.t("Revancha", "Rematch"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar();
                ventana.abrirSeleccionPersonajes();
            }
        }));
        agregarBoton(Style.boton(Texts.t("Menú", "Menu"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar();
            }
        }));

        try {
            PlayerSummary j1 = modelo.obtenerResumenJugador(1);
            PlayerSummary j2 = modelo.obtenerResumenJugador(2);
            ganador.setText(Texts.t("Ganador:", "Winner:") + " " + modelo.obtenerNombreGanador());
            marcador.setText(j1.obtenerNombre() + "  " + j1.obtenerGoles() + " - " + j2.obtenerGoles() + "  "
                    + j2.obtenerNombre());
            estado.setText(modelo.obtenerEstadoPartido());
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    @Override
    protected void alCerrar() {
        dispose();
        ventana.mostrarMenu();
    }
}
