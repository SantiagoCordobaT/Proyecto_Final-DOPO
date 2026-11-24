package presentacion;

import static presentacion.Texts.texto;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Diálogo que muestra el ganador, el marcador final y la opción de revancha o
 * volver al menú.
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

    ResultD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, texto("result.title"));
        this.ventana = ventana;
        ganador = recursos.crearEtiqueta("", Resources.FUENTE_ENCABEZADO);
        marcador = recursos.crearEtiqueta("", Resources.FUENTE_MARCADOR);
        estado = recursos.crearEtiqueta("", Resources.FUENTE_TEXTO);
        ganador.setForeground(Resources.LISTO);
        JPanel cuerpo = recursos.crearPanel(new GridLayout(3, 1, 0, Resources.ESPACIO / 2));
        cuerpo.add(ganador);
        cuerpo.add(marcador);
        cuerpo.add(estado);
        establecerCuerpo(cuerpo);
        agregarBoton(texto("result.rematch"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrar();
                ventana.abrirSeleccionPersonajes();
            }
        });
        agregarBoton(texto("result.menu"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrar();
            }
        });
        try {
            ganador.setText(texto("result.winner") + " " + modelo.obtenerNombreGanador());
            marcador.setText(modelo.obtenerNombreJugador(1) + "  " + modelo.obtenerGolesJugador(1) + " - "
                    + modelo.obtenerGolesJugador(2) + "  " + modelo.obtenerNombreJugador(2));
            estado.setText(modelo.obtenerEstadoPartido());
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    @Override
    protected void cerrar() {
        super.cerrar();
        ventana.mostrarMenu();
    }
}
