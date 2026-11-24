package presentacion;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.ResumenJugador;
import dominio.excepciones.POOBSoccerException;

/**
 * Diálogo de fin de partido con el ganador, el marcador final y los botones de
 * revancha o volver al menú.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class ResultD extends DialogoBase {

    private final HeadSoccerGUI ventana;

    ResultD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, Texts.t("Fin del partido", "Match over"));
        this.ventana = ventana;

        JLabel ganador = Estilo.etiqueta("", Estilo.FUENTE_SUBTITULO);
        JLabel marcador = Estilo.etiqueta("", Estilo.FUENTE_MARCADOR);
        JLabel estado = Estilo.etiqueta("", Estilo.FUENTE_NORMAL);
        ganador.setForeground(Estilo.RESALTE);

        JPanel cuerpo = new JPanel(new GridLayout(3, 1, 0, 6));
        cuerpo.setBackground(Estilo.FONDO);
        cuerpo.add(ganador);
        cuerpo.add(marcador);
        cuerpo.add(estado);
        ponerCentro(cuerpo);

        agregarBoton(Estilo.boton(Texts.t("Revancha", "Rematch"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar();
                ventana.abrirSeleccionPersonajes();
            }
        }));
        agregarBoton(Estilo.boton(Texts.t("Menú", "Menu"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar();
            }
        }));

        try {
            ResumenJugador j1 = modelo.obtenerResumenJugador(1);
            ResumenJugador j2 = modelo.obtenerResumenJugador(2);
            ganador.setText(Texts.t("Ganador:", "Winner:") + " " + modelo.obtenerNombreGanador());
            marcador.setText(j1.nombre + "  " + j1.goles + " - " + j2.goles + "  " + j2.nombre);
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
